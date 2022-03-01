package com.formacion.app.apirest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.formacion.app.apirest.dao.ClienteDao;
import com.formacion.app.apirest.entity.Cliente;

@Service
public class ClienteServiceImpl implements ClienteService {

	@Autowired
	private ClienteDao clienteDao;

	@Override
	@Transactional(readOnly=true)
	//Transactional se usa con los apirest y poder inyectar los repositorios, true cuando es get.
	//Poner en todos los métodos.
	public List<Cliente> findAll() {		
		return (List<Cliente>) clienteDao.findAll();
	}

	@Override
	@Transactional(readOnly=true)
	public Cliente findById(Long id) {		
		return clienteDao.findById(id).orElse(null);
	}

	@Override
	@Transactional //No es un get, así que simplemente así
	public Cliente save(Cliente cliente) {		
		return clienteDao.save(cliente);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		clienteDao.deleteById(id);
		
	}

	@Override
	@Transactional(readOnly=true)
	public Cliente deleteConRetorno(Long id) {
		Cliente c = clienteDao.findById(id).get();
		clienteDao.deleteById(id);
		return c;
				
	}
	
	

}
