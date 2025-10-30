package com.pfcdaw.pfcdaw.controller;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
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
        log.info("[GET /clientes] Listando todos los clientes");
        List<ClienteEntity> clientes = clienteRepository.findAll();
        log.debug("[GET /clientes] Encontrados: {}", clientes.size());
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteEntity> getClienteById(@PathVariable @NonNull Long id) {
        log.info("[GET /clientes/{}] Buscando cliente", id);
        return clienteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("[GET /clientes/{}] No encontrado", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<ClienteEntity> createCliente(@Valid @RequestBody @NonNull ClienteEntity nuevoCliente) {
        log.info("[POST /clientes] Creando cliente: {}", nuevoCliente.getEmail());
        ClienteEntity clienteGuardado = clienteRepository.save(nuevoCliente);
        log.info("[POST /clientes] Cliente creado con ID: {}", clienteGuardado.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteGuardado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable @NonNull Long id) {
        if (!clienteRepository.existsById(id)) {
            log.warn("[DELETE /clientes/{}] No encontrado", id);
            return ResponseEntity.notFound().build();
        }
        log.info("[DELETE /clientes/{}] Eliminando cliente", id);
        clienteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteEntity> updateCliente(@PathVariable @NonNull Long id,
        @Valid @RequestBody @NonNull ClienteEntity clienteActualizado) {
        log.info("[PUT /clientes/{}] Actualizando cliente", id);
        return clienteRepository.findById(id)
                .map(cliente -> {
                    cliente.setNombre(clienteActualizado.getNombre());
                    cliente.setApellido(clienteActualizado.getApellido());
                    cliente.setEmail(clienteActualizado.getEmail());
                    cliente.setDireccion(clienteActualizado.getDireccion());
                    cliente.setTelefono(clienteActualizado.getTelefono());
                    ClienteEntity clienteGuardado = clienteRepository.save(cliente);
                    log.info("[PUT /clientes/{}] Actualizado correctamente", id);
                    return ResponseEntity.ok(clienteGuardado);
                })
                .orElseGet(() -> {
                    log.warn("[PUT /clientes/{}] No encontrado", id);
                    return ResponseEntity.notFound().build();
                });
    }

}
