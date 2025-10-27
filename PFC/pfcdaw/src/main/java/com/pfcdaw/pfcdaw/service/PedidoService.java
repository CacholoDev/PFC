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

/*
  Si el usuario pasa IDs mezclados (algunos correctos, otros no), el pedido se guardará con solo los productos encontrados (posible inconsistencia con lo que el cliente pidió).
bro respecto a esto, aunque el usuario pase ids mezclados, solo queremos los correctos , osea los encontrados para que vamos a guardar algo que no existe?

No informa claramente qué IDs faltan.: lo hablaamos en la anterior cuestion, nos da igual porque solo queremos los que existen

Si una cantidad es 0 o negativa y productoService.reducirStock no maneja eso explícitamente, puede haber errores o comportamientos inesperados. : la cantidad no va a ser 0 ni negativa ya lo manejamos en otra clase

Las cantidades del pedido NO se almacenan en la BDD: tu modelo ManyToMany solo guarda relación producto-pedido, no la cantidad por línea. : esto no entiendo del todo, para que queremos guardar en la tabla producto pedido ? solo manejamos el stock no importa no ?  
Starting (7/7) Decidir comportamiento ante IDs mezclados (mantener solo existentes o error)

Perfecto — gracias por aclararlo, lo tengo claro ahora. Resumo y propongo qué hacer (tú decides):

Tu postura (la que prefieres)
Si se pasan IDs mezclados, quieres quedarte solo con los productos existentes y seguir; no te importa que se ignoren los IDs que no existen.
Las cantidades ya están validadas en otra capa y no serán 0/negativas.
No necesitas guardar la cantidad por producto en la base de datos: solo manejas stock y la relación many-to-many es suficiente.
Implicaciones técnicas breves
Ignorar IDs inexistentes es perfectamente razonable. Lo único a tener en cuenta:
El cliente que llamará a la API no sabrá automáticamente que algunos IDs fueron ignorados, a menos que lo comuniquemos (por ejemplo, en la respuesta o en logs).
Desde el punto de vista de datos, guardar solo los productos existentes evita insertar relaciones inválidas (buena idea).
No guardar cantidades por línea significa que no podrás mostrar "cantidad pedida" en el propio pedido más adelante ni facturar por línea; si eso no es necesario para el PFC, está bien.
Riesgo de concurrencia (oversell) permanece si no usamos locks/version; ProductoService.reducirStock hace comprobación y save, pero sin @Version puede haber condiciones de carrera. Si aceptas ese riesgo para la demo, lo dejamos.
Opciones de implementación (elige una)
Opción 1 (leniente simple, la que recomiendas): Modifico PedidoService para IGNORAR IDs no encontrados — en vez de lanzar BAD_REQUEST los elimino de la lista. Si al final no queda ningún producto valido devuelvo BAD_REQUEST. También añado un log con los IDs ignorados. (Cambio pequeño, mínimo impacto).
Opción 2 (leniente + feedback): Igual que la 1, pero además devuelvo en la respuesta al cliente un JSON con los IDs aceptados y los ignorados (por ejemplo: {"pedido": {...}, "ignorados": [3,7]}). Requiere crear un DTO de respuesta y cambiar PedidoController.createPedido para devolverlo.
Opción 3 (estricto): Mantengo/implemento la validación estricta: si faltan IDs => BAD_REQUEST con la lista de faltantes (esto es lo que hace ahora mi cambio).
Opción 4 (no tocar): Revierto mi cambio y dejo todo como estaba en tu versión original.
Recomendación
Para el PFC yo haría la Opción 1 (leniente simple) y además añadiría un log a INFO o WARN indicando los IDs ignorados. Así la API funciona con lo que existe, pero tienes rastro en logs para depurar. Si luego quieres feedback al cliente, puedes pasar a la Opción 2.
¿Quieres que lo implemente ya?
Responde con 1, 2, 3 o 4 y lo hago.
Si eliges 1 o 2: implemento y ejecuto mvnw -DskipTests package para verificar.
Si eliges 2: te muestro el DTO de respuesta y el ejemplo curl.
 */