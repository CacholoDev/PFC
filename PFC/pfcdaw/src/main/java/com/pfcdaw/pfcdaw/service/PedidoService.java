package com.pfcdaw.pfcdaw.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.pfcdaw.pfcdaw.dto.PedidoCreateDto;
import com.pfcdaw.pfcdaw.model.ClienteEntity;
import com.pfcdaw.pfcdaw.model.EstadoPedidoEnum;
import com.pfcdaw.pfcdaw.model.PedidoEntity;
import com.pfcdaw.pfcdaw.model.ProductoEntity;
import com.pfcdaw.pfcdaw.repository.ClienteRepository;
import com.pfcdaw.pfcdaw.repository.PedidoRepository;
import com.pfcdaw.pfcdaw.repository.ProductoRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional // si falla algo fai rollback na bd
public class PedidoService {

    // SLF4J logger - use the service class as the logger name
    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;

    public PedidoService(PedidoRepository pedidoRepository, ClienteRepository clienteRepository,
            ProductoRepository productoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
    }

    public PedidoEntity createPedido(PedidoCreateDto dto) {
        // comprobamos nulls e listas vacias
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payload del pedido es obligatorio");
        }

        if (dto.getClienteId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "clienteId es obligatorio");
        }

        if (dto.getProductoIds() == null || dto.getProductoIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lista de productos vacía");
        }

        // Comprobar que o cliente existe
        ClienteEntity cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cliente no encontrado"));

        log.debug("createPedido: clienteId={}, productoIds={}", dto.getClienteId(), dto.getProductoIds());

        // Comprobar que os produtos existen
        List<ProductoEntity> productos = productoRepository.findAllById(dto.getProductoIds());
        if (productos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Productos no encontrados");
        }

        // calcular total, miramos primeiro si nn e null
        productos.stream()
                .filter(p -> p.getPrecio() == null)
                .findAny()
                .ifPresent(p -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Precio inválido en producto id=" + p.getId());
                });

        double total = productos.stream().mapToDouble(p -> p.getPrecio()).sum();

        // Crear o pedido
        PedidoEntity pedido = PedidoEntity.builder()
                .cliente(cliente)
                .productos(productos)
                .total(total)
                .estado(EstadoPedidoEnum.PENDIENTE)
                .build();

        return pedidoRepository.save(pedido);
    }

}
