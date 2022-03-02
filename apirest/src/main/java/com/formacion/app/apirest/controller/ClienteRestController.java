package com.formacion.app.apirest.controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.formacion.app.apirest.entity.Cliente;
import com.formacion.app.apirest.service.ClienteService;

@RestController
@RequestMapping("/api")
public class ClienteRestController {
	@Autowired
	private ClienteService servicio;

	@GetMapping("/clientes")
	public List<Cliente> index() {
		return servicio.findAll();
	}

	// Si quiero que dos url utilicen el mismo método, por ejemplo:
	// @GetMapping({"/clientes","/todos"})
	/*
	 * @GetMapping("/clientes/{id}")//Paso el id en la dirección public Cliente
	 * findClienteById(@PathVariable Long id){ return servicio.findById(id); }
	 */

	@GetMapping("/clientes/{id}") // Paso el id en la dirección
	public ResponseEntity<?> findClienteById(@PathVariable Long id) {

		Cliente cliente = null;
		Map<String, Object> response = new HashMap<>();

		try {

			cliente = servicio.findById(id);

		} catch (DataAccessException e) {
			response.put("mensaje", "Error al reallizar consulta a base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));

			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);

		}

		if (cliente == null) {
			response.put("mensaje", "El cliente ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Cliente>(cliente, HttpStatus.OK);
	}
	/*
	 * @PostMapping("/cliente")
	 * 
	 * @ResponseStatus(HttpStatus.CREATED) public Cliente saveCliente(@RequestBody
	 * Cliente cliente) { return servicio.save(cliente); }
	 */

	@PostMapping("/cliente/guardarCliente")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> saveCliente(@RequestBody Cliente cliente) {
		Map<String, Object> response = new HashMap<>();

		try {
			servicio.save(cliente);
		} catch (DataAccessException e) {

			response.put("mensaje", "Error al realizar la insert a la base de datos");
			response.put("error", e.getMessage().concat(": ".concat(e.getMostSpecificCause().getMessage())));

			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "¡El cliente ha sido creado con exito!");
		response.put("cliente", cliente);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	/*
	 * @PutMapping("/cliente/{id}")
	 * 
	 * @ResponseStatus(HttpStatus.CREATED) public Cliente upDateCliente(@RequestBody
	 * Cliente cliente, @PathVariable Long id) {
	 * 
	 * Cliente clienteupdate = servicio.findById(id);
	 * 
	 * clienteupdate.setId(id); clienteupdate.setNombre(cliente.getNombre());
	 * clienteupdate.setApellido(cliente.getApellido());
	 * clienteupdate.setEmail(cliente.getEmail());
	 * clienteupdate.setTelefono(cliente.getTelefono());
	 * clienteupdate.setCreateAd(cliente.getCreateAd());
	 * 
	 * return servicio.save(clienteupdate); }
	 */
	@PutMapping("/cliente/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> upDateCliente(@RequestBody Cliente cliente, @PathVariable Long id) {

		Cliente clienteActual = servicio.findById(id);

		Map<String, Object> response = new HashMap<>();

		if (clienteActual == null) {
			response.put("mensaje", "El cliente ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {
			clienteActual.setId(id);
			clienteActual.setNombre(cliente.getNombre());
			clienteActual.setApellido(cliente.getApellido());
			clienteActual.setEmail(cliente.getEmail());
			clienteActual.setTelefono(cliente.getTelefono());
			clienteActual.setCreateAd(cliente.getCreateAd());

			servicio.save(clienteActual);
		} catch (DataAccessException e) {

			response.put("mensaje", "Error al realizar la actualización a la base de datos");
			response.put("error", e.getMessage().concat(": ".concat(e.getMostSpecificCause().getMessage())));

			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "¡El cliente ha sido actualizado con exito!");
		response.put("cliente", clienteActual);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	/*
	 * @DeleteMapping("/clientes/{id}") public void deleteCliente(@PathVariable Long
	 * id){ servicio.delete(id);
	 * 
	 * }
	 */
	/*
	 * @DeleteMapping("/clientes/{id}") public Cliente deleteCliente(@PathVariable
	 * Long id) { return servicio.deleteConRetorno(id); }
	 */
	@DeleteMapping("/clientes/{id}")
	public ResponseEntity<?> deleteCliente(@PathVariable Long id) {

		Cliente clienteABorrar = servicio.findById(id);

		Map<String, Object> response = new HashMap<>();

		if (clienteABorrar == null) {
			response.put("mensaje", "El cliente ID: ".concat(id.toString().concat(" no se pudo eliminar")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {
			// Borro la foto si existe
			String nombreFotoAnterior = clienteABorrar.getImagen();

			if (nombreFotoAnterior != null && nombreFotoAnterior.length() > 0) {
				Path rutaFotoAnterior = Paths.get("uploads").resolve(nombreFotoAnterior).toAbsolutePath();
				File archivoFotoAnterior = rutaFotoAnterior.toFile();
				if (archivoFotoAnterior.exists() && archivoFotoAnterior.canRead()) {
					archivoFotoAnterior.delete();
				}
			}

			servicio.delete(id);

		} catch (DataAccessException e) {

			response.put("mensaje", "Error al realizar la eliminación en la base de datos");
			response.put("error", e.getMessage().concat(": ".concat(e.getMostSpecificCause().getMessage())));

			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "¡El cliente ha sido borrado con exito!");
		response.put("cliente", clienteABorrar);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	// Subida de imagenes
	@PostMapping("/cliente/upload")
	// ojo con la prueba guardo en postman
	public ResponseEntity<?> upload(@RequestParam("archivo") MultipartFile archivo, @RequestParam("id") Long id) {

		Map<String, Object> response = new HashMap<String, Object>();

		Cliente cliente = servicio.findById(id);

		if (!archivo.isEmpty()) {

			// String nombreArchivo = archivo.getOriginalFilename();
			// Con esta nueva línea voy a asignar un id aleatorio al nombre del archivo y
			// así puedo subir la misma imagen a va varios clientes.
			// Es decir, puedo tener dos clientes o más con la misma foto.
			String nombreArchivo = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename().replace(" ", " ");
			// Problema va a añadir tantas fotos como se manejen desde el cliente. Estén en
			// uso o no. Es decir no machaca img de un mismo cliente.
			Path rutaArchivo = Paths.get("uploads").resolve(nombreArchivo).toAbsolutePath();

			try {
				Files.copy(archivo.getInputStream(), rutaArchivo);

			} catch (IOException e) {
				response.put("mensaje", "Error al subir la imagen");
				response.put("error", e.getMessage().concat(": ".concat(e.getCause().getMessage())));
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}

			String nombreFotoAnterior = cliente.getImagen();

			if (nombreFotoAnterior != null && nombreFotoAnterior.length() > 0) {
				Path rutaFotoAnterior = Paths.get("uploads").resolve(nombreFotoAnterior).toAbsolutePath();
				File archivoFotoAnterior = rutaFotoAnterior.toFile();
				if (archivoFotoAnterior.exists() && archivoFotoAnterior.canRead()) {
					archivoFotoAnterior.delete();
				}
			}

			cliente.setImagen(nombreArchivo);
			servicio.save(cliente);
			response.put("cliente", cliente);
			response.put("mensaje", "Has subido correctamente " + nombreArchivo);
		}

		return new ResponseEntity<Cliente>(cliente, HttpStatus.CREATED);
	}

	@GetMapping("/uploads/imagen/{nombreImagen:.+}")
	public ResponseEntity<Resource> verImagen(@PathVariable String nombreImagen) {

		Path rutaImagen = Paths.get("uploads").resolve(nombreImagen).toAbsolutePath();

		Resource recurso = null;
		try {
			recurso = new UrlResource(rutaImagen.toUri());

		} catch (MalformedURLException e) {

			e.printStackTrace();
		}
		if (!recurso.exists() && !recurso.isReadable()) {
			throw new RuntimeException("Error, no se puede cargar la imagen " + nombreImagen);
		}
		HttpHeaders cabecera = new HttpHeaders();
		cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\" " + recurso.getFilename() + "\"");

		return new ResponseEntity<Resource>(recurso, cabecera, HttpStatus.OK);
		//Para probar este método vamos a postman y subimos una imagen.
		//Copiamos el nombre de la imagen
		//http://localhost:9000/api/uploads/imagen/nombredelaimagen
		//Y al dar enter, se podrá descargar la imagen.
	}

}
