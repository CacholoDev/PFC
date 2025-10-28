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

        // Crear o pedido y sus líneas
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

        // Guardar pedido (cascade ALL persiste las líneas)
        PedidoEntity pedidoGuardado = pedidoRepository.save(pedido);

        // Reducir stock dos produtos
        for (LineaPedido linea : lineas) {
            productoService.reducirStock(linea.getProducto().getId(), linea.getCantidad());
            log.info("Stock reducido en {} unidades para producto '{}'", linea.getCantidad(), linea.getProducto().getNombre());
        }

        return pedidoGuardado;
    }

}

/*
 PedidoEntity pedido = PedidoEntity.builder()
    .cliente(cliente)
    .total(total)
    .estado(EstadoPedidoEnum.PENDIENTE)
    .build();

// Crear as líneas do pedido
List<LineaPedido> lineas = new ArrayList<>();
for (ProductoEntity producto : productos) {
    Integer cantidad = dto.getProductos().get(producto.getId());
    Double subtotal = producto.getPrecio() * cantidad;
    
    LineaPedido linea = LineaPedido.builder()
        .pedido(pedido)
        .producto(producto)
        .cantidad(cantidad)
        .subtotal(subtotal)
        .build();
    
    lineas.add(linea);
}

pedido.setLineas(lineas);  // Asignar las líneas al pedido
 */

 /*
    Resumen corto
LineaPedido es la entidad que representa una línea concreta de un pedido: enlaza un Pedido con un Producto y además guarda datos de la relación (cantidad, subtotal/pTotal, etc.).
La razón de crear LineaPedido es que la relación pedido↔producto necesita atributos extra (cantidad, precio en el momento del pedido), por eso no sirve un ManyToMany simple.

    ¿Por qué PedidoEntity y ProductoEntity tienen listas de LineaPedido?
PedidoEntity tiene List<LineaPedido> lineasPedido para poder navegar desde un pedido a sus líneas (ej. mostrar el detalle del pedido: producto + cantidad + subtotal). Es la vista "desde el pedido".
ProductoEntity puede tener List<LineaPedido> para poder navegar desde un producto a todas las líneas donde ha sido vendido (historial, estadísticas). Es opcional: sirve para consultas inversas pero no es estrictamente necesario si no vas a necesitarlas.
Ambas colecciones son la representación en memoria de la relación. La tabla real que contiene los FKs es lineas_pedido (o como la llames), que guarda pedido_id, producto_id, cantidad, p_total.

    Quién es el "dueño" de la relación en JPA (owning side)
El owning side son las entidades que contienen la columna de FK en la base de datos. En nuestro caso:
LineaPedido tiene @ManyToOne a PedidoEntity y @ManyToOne a ProductoEntity. Por tanto LineaPedido es el owning side (contiene pedido_id y producto_id).
PedidoEntity y ProductoEntity tienen @OneToMany(mappedBy="pedido") / mappedBy="producto": son el lado inverso, no crean nuevas FKs.
Consecuencia práctica: para persistir la relación debes asignar las referencias en LineaPedido (linea.setPedido(pedido); linea.setProducto(producto);). Cuando guardes pedido con cascade ALL, JPA insertará las filas de lineas_pedido con las FKs correctas.

    Flujo típico al crear un pedido (lo que vas a implementar en PedidoService)
Crear el PedidoEntity (cliente, fecha, estado provisional). No necesitas añadir aún las líneas si vas a construirlas después.
Por cada productId+cantidad en el DTO:
Buscar ProductoEntity product.
Crear LineaPedido linea = new LineaPedido();
linea.setProducto(product); linea.setCantidad(cantidad); linea.setpTotal(product.getPrecio() * cantidad);
linea.setPedido(pedido);
(Opcional) añadir la línea a producto.getLineasPedido().add(linea) si mantienes consistencia bidireccional en memoria.
Añadir linea a la lista local List<LineaPedido>.
pedido.setLineasPedido(listLineas);
Pedido saved = pedidoRepository.save(pedido); — si en PedidoEntity tienes cascade = CascadeType.ALL en lineasPedido, JPA persiste las líneas automáticamente junto con el pedido.
Llamar a productoService.reducirStock(productId, cantidad) para cada línea (todo dentro del mismo método @Transactional para que si algo falla se haga rollback).
Nota: puedes reducir stock antes o después del save mientras estés en la misma transacción; si reduces antes y luego el save falla, el rollback revertirá la reducción también.

    Consideraciones prácticas / recomendaciones
Evita validar (por ejemplo @NotNull, @Size) las colecciones lineasPedido en ProductoEntity/PedidoEntity si creas productos pedidos sin líneas: pueden dar problemas al persistir. Mejor validar en DTO/servicio.
Para evitar recursión JSON (order → lines → product → lines → ...), usa:
@JsonIgnore en la colección inversa que no quieras exponer (por ejemplo en ProductoEntity.lineasPedido) o
@JsonManagedReference / @JsonBackReference.
Mantén la consistencia bidireccional en memoria si vas a navegar por ambos lados: cuando añades la linea también añade a pedido.getLineasPedido() y (si la colección existe) a producto.getLineasPedido().
Considera añadir @Version en ProductoEntity para evitar overselling en concurrencia alta (opcional para demo).
Es normal y correcto que PedidoEntity y ProductoEntity “reciban” o mantengan colecciones de LineaPedido — son vistas diferentes de la misma relación (desde el pedido y desde el producto).

    Esquema en BD (ejemplo)
tabla productos (id, nombre, precio, stock, ...)
tabla pedidos (id, fecha, total, cliente_id, estado, ...)
tabla lineas_pedido (id, pedido_id, producto_id, cantidad, p_total)
  */
