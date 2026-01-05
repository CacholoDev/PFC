package com.pfcdaw.pfcdaw.controller;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    public ClienteController(ClienteRepository clienteRepository, PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
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
    public ResponseEntity<ClienteEntity> createCliente(@Valid @RequestBody ClienteEntity nuevoCliente) {
        log.info("[POST /clientes] Creando cliente: {}", nuevoCliente.getEmail());
        // Hashear la contraseña antes de guardar
        nuevoCliente.setPassword(passwordEncoder.encode(nuevoCliente.getPassword()));
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
    public ResponseEntity<?> updateCliente(@PathVariable @NonNull Long id,
        @Valid @RequestBody @NonNull ClienteEntity clienteActualizado) {
        log.info("[PUT /clientes/{}] Actualizando cliente", id);
        return clienteRepository.findById(id)
                .map(cliente -> {
                    // Comprobar si o mail querese cambiar e xa existe noutro cliente
                    ClienteEntity clienteConEmail = clienteRepository.findByEmail(clienteActualizado.getEmail()).orElse(null);
                    if (clienteConEmail != null && !clienteConEmail.getId().equals(cliente.getId())) {
                        log.warn("[PUT /clientes/{}] Email duplicado: {}", id, clienteActualizado.getEmail());
                        return ResponseEntity.status(HttpStatus.CONFLICT).build();
                    }
                    cliente.setNombre(clienteActualizado.getNombre());
                    cliente.setApellido(clienteActualizado.getApellido());
                    cliente.setEmail(clienteActualizado.getEmail());
                    cliente.setDireccion(clienteActualizado.getDireccion());
                    cliente.setNombreEmpresa(clienteActualizado.getNombreEmpresa());
                    cliente.setTelefono(clienteActualizado.getTelefono());
                    
                    // Solo hashear si la contraseña cambió (no es un hash BCrypt)
                    String nuevaPassword = clienteActualizado.getPassword();
                    if (nuevaPassword != null && !nuevaPassword.isEmpty()) {
                        // Si NO empieza por $2a$ (formato BCrypt), hashearla
                        if (!nuevaPassword.startsWith("$2a$") && !nuevaPassword.startsWith("$2b$") && !nuevaPassword.startsWith("$2y$")) {
                            cliente.setPassword(passwordEncoder.encode(nuevaPassword));
                            log.debug("[PUT /clientes/{}] Contraseña actualizada y hasheada", id);
                        } else {
                            // Ya está hasheada, no volver a hashear
                            log.debug("[PUT /clientes/{}] Contraseña ya hasheada, no se modifica", id);
                        }
                    }
                    
                    cliente.setRole(clienteActualizado.getRole());
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
