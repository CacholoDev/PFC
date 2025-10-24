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

import com.pfcdaw.pfcdaw.model.PedidoEntity;
import com.pfcdaw.pfcdaw.repository.PedidoRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private static final Logger log = LoggerFactory.getLogger(PedidoController.class);
    private final PedidoRepository pedidoRepository;

    public PedidoController(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @GetMapping
    public ResponseEntity<List<PedidoEntity>> getAllPedidos() {
        log.info("Listando todos los pedidos");
        List<PedidoEntity> pedidos = pedidoRepository.findAll();
        log.debug("Pedidos encontrados: {}", pedidos.size());
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoEntity> getPedidoById(@PathVariable Long id) {
        log.info("Buscando pedido con ID: {}", id);
        return pedidoRepository.findById(id)
                .map(pedido -> {
                    log.info("Pedido encontrado: {}", pedido.getId());
                    return ResponseEntity.ok(pedido);
                })
                .orElseGet(() -> {
                    log.warn("Pedido con ID {} no encontrado", id);
                    return ResponseEntity.notFound().build();
                });
    }

    // Obtener todos os pedidos dun cliente específico
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoEntity>> getPedidosByCliente(@PathVariable Long clienteId) {
        log.info("Buscando pedidos del cliente con ID: {}", clienteId);
        List<PedidoEntity> pedidos = pedidoRepository.findByClienteId(clienteId);
        log.debug("Pedidos encontrados para el cliente {}: {}", clienteId, pedidos.size());
        return ResponseEntity.ok(pedidos);
    }

    @PostMapping
    public ResponseEntity<PedidoEntity> createPedido(@Valid @RequestBody PedidoEntity nuevoPedido) {
        log.info("Creando nuevo pedido...");
        PedidoEntity pedidoGuardado = pedidoRepository.save(nuevoPedido);
        log.info("Pedido creado con ID: {}", pedidoGuardado.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoGuardado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePedido(@PathVariable Long id) {
        if (!pedidoRepository.existsById(id)) {
            log.warn("Pedido con ID {} no encontrado", id);
            return ResponseEntity.notFound().build();
        }
        log.info("Pedido con ID {} eliminado", id);
        pedidoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoEntity> updatePedido(@PathVariable Long id,
            @Valid @RequestBody PedidoEntity pedidoActualizado) {
        log.info("Actualizando pedido con ID: {}", id);
        log.debug("Datos recibidos para actualización: {}", pedidoActualizado);
        return pedidoRepository.findById(id)
                .map(pedido -> {
                    log.debug("Pedido antes de actualizar: id={}, total={}, estado={}", pedido.getId(),
                            pedido.getTotal(), pedido.getEstado());

                    pedido.setProductos(pedidoActualizado.getProductos());
                    pedido.setTotal(pedidoActualizado.getTotal());
                    pedido.setEstado(pedidoActualizado.getEstado());

                    PedidoEntity pedidoGuardado = pedidoRepository.save(pedido);

                    log.info("Pedido con ID {} actualizado", id);
                    log.debug("Pedido después de actualizar: {}", pedidoGuardado);
                    return ResponseEntity.ok(pedidoGuardado);
                })
                .orElseGet(() -> {
                    log.warn("Pedido con ID {} no encontrado", id);
                    return ResponseEntity.notFound().build();
                });
    }

}
