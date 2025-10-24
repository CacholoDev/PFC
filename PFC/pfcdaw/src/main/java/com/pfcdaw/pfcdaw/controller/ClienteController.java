package com.pfcdaw.pfcdaw.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pfcdaw.pfcdaw.model.ClienteEntity;
import com.pfcdaw.pfcdaw.repository.ClienteRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private static final Logger log = LoggerFactory.getLogger(ClienteController.class);
    private final ClienteRepository clienteRepository;

    public ClienteController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @GetMapping
    public ResponseEntity<List<ClienteEntity>> getAllClientes() {
        log.info("Listando todos los clientes");
        List<ClienteEntity> clientes = clienteRepository.findAll();
        log.debug("Clientes encontrados: {}", clientes.size());
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteEntity> getClienteById(@PathVariable Long id) {
        log.info("Buscando cliente con ID: {}", id);
        return clienteRepository.findById(id)
                .map(cliente -> {
                    log.info("Cliente encontrado: {}", cliente.getNombre());
                    return ResponseEntity.ok(cliente);
                })
                .orElseGet(() -> {
                    log.warn("Cliente con ID {} no encontrado", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<ClienteEntity> createCliente(@Valid @RequestBody ClienteEntity nuevoCliente) {
        log.info("Creando nuevo cliente...");
        ClienteEntity clienteGuardado = clienteRepository.save(nuevoCliente);
        log.info("Cliente creado con ID: {}", clienteGuardado.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteGuardado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        log.info("Eliminando cliente con ID: {}, y nombre: {}", id, clienteRepository.findById(id).map(ClienteEntity::getNombre).orElse("Desconocido"));
        if (clienteRepository.existsById(id)) {
            clienteRepository.deleteById(id);
            log.info("Cliente con ID {} eliminado", id);
            return ResponseEntity.noContent().build();
        } else {
            log.warn("Cliente con ID {} no encontrado para eliminar", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteEntity> updateCliente(@PathVariable Long id,
            @Valid @RequestBody ClienteEntity clienteActualizado) {
        log.info("Actualizando cliente con ID: {}", id);
        return clienteRepository.findById(id)
                .map(cliente -> {
                    cliente.setNombre(clienteActualizado.getNombre());
                    cliente.setApellido(clienteActualizado.getApellido());
                    cliente.setEmail(clienteActualizado.getEmail());
                    cliente.setDireccion(clienteActualizado.getDireccion());
                    cliente.setTelefono(clienteActualizado.getTelefono());
                    ClienteEntity clienteGuardado = clienteRepository.save(cliente);
                    log.info("Cliente con ID {} actualizado", id);
                    return ResponseEntity.ok(clienteGuardado);
                })
                .orElseGet(() -> {
                    log.warn("Cliente con ID {} no encontrado para actualizar", id);
                    return ResponseEntity.notFound().build();
                });
    }

}
