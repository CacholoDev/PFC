package com.pfcdaw.pfcdaw.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.pfcdaw.pfcdaw.dto.PedidoCreateDto;
import com.pfcdaw.pfcdaw.model.ClienteEntity;
import com.pfcdaw.pfcdaw.model.EstadoPedidoEnum;
import com.pfcdaw.pfcdaw.model.LineaPedido;
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
    // Crear un pedido a partir do DTO (producto + cantida)
    // Crear lineas + reducir stock
    // crear pedido → crear lineas → añadir lineas o pedido → .save → reducir stock(productoService)
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

        // comprobar que os produtos existen, .keySet para buscar clave-valor no Map do PedidoDTO
        List<ProductoEntity> productos = productoRepository.findAllById(dto.getProductos().keySet());
        // warning se non se atopan todos os productos (posible futura mellora)
        if (productos.size() < dto.getProductos().size()) {
            log.warn("Algunos IDs de productos no fueron encontrados. Solicitados: {}, Encontrados: {}",
                    dto.getProductos().keySet(),
                    productos.stream().map(ProductoEntity::getId).toList());
        }
        if (productos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Productos no encontrados");
        }

        // Validar cantidades antes de calcular total
        dto.getProductos().forEach((id, cantidad) -> {
            if (cantidad == null || cantidad < 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "La cantidad del producto " + id + " debe ser mayor a 0");
            }
        });

        // calcular total, miramos primeiro si o precio nn e null
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

        // Crear o pedido + líneas
        PedidoEntity pedido = PedidoEntity.builder()
                .cliente(cliente)
                .total(total)
                .estado(EstadoPedidoEnum.PENDIENTE)
                .build();

        List<LineaPedido> lineas = new ArrayList<>();
        for (ProductoEntity producto : productos) {
            Integer cantidad = dto.getProductos().get(producto.getId());
            Double subtotal = producto.getPrecio() * cantidad;

            LineaPedido linea = LineaPedido.builder()
                    .pedido(pedido)
                    .producto(producto)
                    .cantidad(cantidad)
                    .pTotal(subtotal)
                    .build();
            lineas.add(linea);
        }

        pedido.setLineasPedido(lineas);

        // Gardar o pedido (cascade ALL persiste as líneas)
        PedidoEntity pedidoGuardado = pedidoRepository.save(pedido);

        // Reducir stock dos produtos
        for (LineaPedido linea : lineas) {
            productoService.reducirStock(linea.getProducto().getId(), linea.getCantidad());
            log.info("Stock reducido en {} unidades para producto '{}'", linea.getCantidad(), linea.getProducto().getNombre());
        }

        return pedidoGuardado;
    }

}

