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

    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final ProductoService productoService;

    public PedidoService(PedidoRepository pedidoRepository, ClienteRepository clienteRepository,
            ProductoRepository productoRepository, ProductoService productoService) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.productoService = productoService;
    }

    public PedidoEntity createPedido(PedidoCreateDto dto) {
        // comprobamos nulls e listas vacias
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payload del pedido es obligatorio");
        }

        if (dto.getClienteId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "clienteId es obligatorio");
        }

        if (dto.getProductos() == null || dto.getProductos().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lista de productos vacía");
        }

        // Comprobar que o cliente existe
        ClienteEntity cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cliente no encontrado"));

        log.debug("createPedido: clienteId={}, productoIds={}", dto.getClienteId(), dto.getProductos());

        // Comprobar que os produtos existen, .keySet para buscar clave-valor do Map do
        // PedidoDTO
        List<ProductoEntity> productos = productoRepository.findAllById(dto.getProductos().keySet());
        if (productos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Productos no encontrados");
        }

        // calcular total, miramos primeiro si nn e null
        productos.stream()
                .filter(p -> p.getPrecio() == null)
                .findAny() // busca productos con precio nulo
                .ifPresent(p -> { // si atopou algo tira o error
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Precio inválido en producto id=" + p.getId());
                });

        double total = productos.stream()
                .mapToDouble(p -> p.getPrecio() * dto.getProductos().get(p.getId()))
                .sum();

        // Crear o pedido
        PedidoEntity pedido = PedidoEntity.builder()
                .cliente(cliente)
                .productos(productos)
                .total(total)
                .estado(EstadoPedidoEnum.PENDIENTE)
                .build();

        PedidoEntity pedidoGuardado = pedidoRepository.save(pedido);  
        // Reducir stock dos produtos
        for (ProductoEntity producto : productos) {
            Integer cantidad = dto.getProductos().get(producto.getId());
            productoService.reducirStock(producto.getId(), cantidad);
            log.info("Stock reducido en {} unidades para producto '{}'", cantidad, producto.getNombre());
        }

        return pedidoGuardado;
    }

}
