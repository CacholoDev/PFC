package com.pfcdaw.pfcdaw.controller;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.pfcdaw.pfcdaw.dto.PedidoCreateDto;
import com.pfcdaw.pfcdaw.model.PedidoEntity;
import com.pfcdaw.pfcdaw.repository.PedidoRepository;
import com.pfcdaw.pfcdaw.service.PedidoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private static final Logger log = LoggerFactory.getLogger(PedidoController.class);
    private final PedidoRepository pedidoRepository;
    private final PedidoService pedidoService;

    public PedidoController(PedidoRepository pedidoRepository, PedidoService pedidoService) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public ResponseEntity<List<PedidoEntity>> getAllPedidos() {
        log.info("[GET /pedidos] Listando todos los pedidos");
        List<PedidoEntity> pedidos = pedidoRepository.findAll();
        log.debug("[GET /pedidos] Encontrados: {}", pedidos.size());
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoEntity> getPedidoById(@PathVariable @NonNull Long id) {
        log.info("[GET /pedidos/{}] Buscando pedido", id);
        return pedidoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("[GET /pedidos/{}] No encontrado", id);
                    return ResponseEntity.notFound().build();
                });
    }
    
    // Obter todos os pedidos dun cliente específico
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoEntity>> getPedidosByCliente(@PathVariable Long clienteId) {
        log.info("[GET /pedidos/cliente/{}] Buscando pedidos del cliente", clienteId);
        List<PedidoEntity> pedidos = pedidoRepository.findByClienteId(clienteId);
        log.debug("[GET /pedidos/cliente/{}] Encontrados: {}", clienteId, pedidos.size());
        return ResponseEntity.ok(pedidos);
    }

    @PostMapping
    public ResponseEntity<PedidoEntity> createPedido(@Valid @RequestBody @NonNull PedidoCreateDto dto) {
        log.info("[POST /pedidos] Creando pedido para cliente: {}", dto.getClienteId());
        PedidoEntity pedidoGuardado = pedidoService.createPedido(dto);
        log.info("[POST /pedidos] Pedido creado con ID: {}", pedidoGuardado.getId());
        // uri location para ver na resposta onde se atopa o recurso creado, asi o frontEnd sabe donde facer o get
        var location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(pedidoGuardado.getId()).toUri();

        return ResponseEntity.created(location).body(pedidoGuardado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePedido(@PathVariable @NonNull Long id) {
        if (!pedidoRepository.existsById(id)) {
            log.warn("[DELETE /pedidos/{}] No encontrado", id);
            return ResponseEntity.notFound().build();
        }
        log.info("[DELETE /pedidos/{}] Eliminando pedido", id);
        pedidoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PedidoEntity> updatePedido(@PathVariable @NonNull Long id,
    @Valid @RequestBody @NonNull PedidoEntity pedidoActualizado) {
    log.info("[PUT /pedidos/{}] Actualizando estado", id);
    return pedidoRepository.findById(id)
    .map(pedido -> {
        log.debug("[PUT /pedidos/{}] Antes: estado={}", id, pedido.getEstado());
                // NOTE: desde que o modelo usa LineaPedido:
                // Solo actualizar estado (PENDIENTE -> EN_PREPARACION -> COMPLETADO -> ENTREGADO)
                pedido.setEstado(pedidoActualizado.getEstado());
                // Non toca total, recalculase auto co @PreUpdate + @PrePersist
                // Non toca cliente(si se quixera cambiar de cliente, mellor borrar e facer outro pedido)
                // Non toca lineas, xestionase aparte + controla stock
                PedidoEntity pedidoGuardado = pedidoRepository.save(pedido);
                log.info("[PUT /pedidos/{}] Estado actualizado a: {}", id, pedidoGuardado.getEstado());
                return ResponseEntity.ok(pedidoGuardado);
            })
            .orElseGet(() -> {
                log.warn("[PUT /pedidos/{}] No encontrado", id);
                return ResponseEntity.notFound().build();
            });
}

}

 