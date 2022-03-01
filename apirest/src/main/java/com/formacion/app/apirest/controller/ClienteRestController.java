package com.formacion.app.apirest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.formacion.app.apirest.entity.Cliente;
import com.formacion.app.apirest.service.ClienteService;

@RestController
@RequestMapping("/api")
public class ClienteRestController {
	@Autowired
	private ClienteService servicio;
	
	@GetMapping("/clientes")
	public List<Cliente> index(){
		return servicio.findAll();
	}
	
	//Si quiero que dos url utilicen el mismo método, por ejemplo:
	// @GetMapping({"/clientes","/todos"})
		
	@GetMapping("/clientes/{id}")//Paso el id en la dirección
	public Cliente findClienteById(@PathVariable Long id){
		return servicio.findById(id);
	}
	
	@PostMapping("/cliente")
	@ResponseStatus(HttpStatus.CREATED)
	public Cliente saveCliente(@RequestBody Cliente cliente) {
		return servicio.save(cliente);
	}
	
	@PutMapping("/cliente/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public Cliente upDateCliente(@RequestBody Cliente cliente, @PathVariable Long id) {
		
		Cliente clienteupdate = servicio.findById(id);
		
		clienteupdate.setId(id);
		clienteupdate.setNombre(cliente.getNombre());
		clienteupdate.setApellido(cliente.getApellido());
		clienteupdate.setEmail(cliente.getEmail());
		clienteupdate.setTelefono(cliente.getTelefono());
		clienteupdate.setCreateAd(cliente.getCreateAd());
				
		return servicio.save(clienteupdate);
	}
	/*
	@DeleteMapping("/clientes/{id}")
	public void deleteCliente(@PathVariable Long id){
		servicio.delete(id);
		
	}
	*/
	@DeleteMapping("/clientes/{id}")
	public Cliente deleteCliente(@PathVariable Long id) {
		return servicio.deleteConRetorno(id);
	}
}
