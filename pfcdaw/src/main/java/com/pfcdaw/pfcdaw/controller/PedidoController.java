package com.pfcdaw.pfcdaw.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pfcdaw.pfcdaw.model.PedidoEntity;
import com.pfcdaw.pfcdaw.repository.PedidoRepository;

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
                log.warn("Pedido no encontrado");
                return ResponseEntity.notFound().build();
            });
        }

    @PostMapping
    public ResponseEntity<PedidoEntity> createPedido(@RequestBody PedidoEntity nuevoPedido) {
        log.info("Creando nuevo pedido...");
        PedidoEntity pedidoGuardado = pedidoRepository.save(nuevoPedido);
        log.info("Pedido creado con ID: {}", pedidoGuardado.getId());
        return ResponseEntity.ok(pedidoGuardado);
    }
