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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.pfcdaw.pfcdaw.dto.StockUpdateDto;
import com.pfcdaw.pfcdaw.model.ProductoEntity;
import com.pfcdaw.pfcdaw.repository.ProductoRepository;
import com.pfcdaw.pfcdaw.service.ProductoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    private static final Logger log = LoggerFactory.getLogger(ProductoController.class);
    private final ProductoRepository productoRepository;
    private final ProductoService productoService;

    public ProductoController(ProductoRepository productoRepository, ProductoService productoService) {
        this.productoRepository = productoRepository;
        this.productoService = productoService;
    }

    // listar productos nn poñemos path porque colle o requestmapping do controlador
    @GetMapping
    public ResponseEntity<List<ProductoEntity>> getAllProductos() {
        log.info("[GET /productos] Listando todos los productos");
        List<ProductoEntity> productos = productoRepository.findAll();
        log.debug("[GET /productos] Productos encontrados: {}", productos.size());
        return ResponseEntity.ok(productos);
    }

    // listar por id
    @GetMapping("/{id}")
    public ResponseEntity<ProductoEntity> getProductoById(@PathVariable @NonNull Long id) {
        return productoRepository.findById(id)
                .map(producto -> {
                    log.info("[GET /productos/{}] Producto encontrado: {}", id, producto.getNombre());
                    return ResponseEntity.ok(producto);
                })
                .orElseGet(() -> {
                    log.warn("[GET /productos/{}] Producto no encontrado", id);
                    return ResponseEntity.notFound().build();
                });

    }

    // crear producto
    @PostMapping
    public ResponseEntity<ProductoEntity> createProducto(@Valid @RequestBody ProductoEntity producto) {
        log.info("[POST /productos] Creando nuevo producto: {}", producto.getNombre());
        ProductoEntity nuevoProducto = productoRepository.save(producto);
        log.info("[POST /productos] Producto creado con ID: {}", nuevoProducto.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }

    // delete producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable @NonNull Long id) {
        if (!productoRepository.existsById(id)) {
            log.warn("[DELETE /productos/{}] Producto no encontrado", id);
            return ResponseEntity.notFound().build();
        }
        log.info("[DELETE /productos/{}] Eliminando producto", id);
        productoRepository.deleteById(id);
        log.info("[DELETE /productos/{}] Producto eliminado", id);
        return ResponseEntity.noContent().build();
    }

    // actualizar producto
    @PutMapping("/{id}")
    public ResponseEntity<ProductoEntity> updateProducto(@PathVariable @NonNull Long id,
            @Valid @RequestBody @NonNull ProductoEntity p) {
        log.info("[PUT /productos/{}] Solicitud de actualización recibida", id);
        log.debug("[PUT /productos/{}] Datos recibidos: {}", id, p);
        return productoRepository.findById(id)
                .map(producto -> {
                    log.debug("[PUT /productos/{}] Antes: nombre={}, precio={}", id, producto.getNombre(),
                            producto.getPrecio());
                    // PUT: solo campos "informativos" (nombre, descripción, precio)
                    producto.setNombre(p.getNombre());
                    producto.setDescripcion(p.getDescripcion());
                    // a foto cambiala con endpoint /productos/{id}/ActFoto
                    // producto.setImagenUrl(p.getImagenUrl());
                    producto.setPrecio(p.getPrecio());
                    // Stock SOLO por:
                    // - POST /productos/{id}/AumStock ## - POST /productos/{id}/RedStock

                    ProductoEntity productoActualizado = productoRepository.save(producto);

                    log.info("[PUT /productos/{}] Producto actualizado: {}", id, productoActualizado.getNombre());
                    log.debug("[PUT /productos/{}] Después: {}", id, productoActualizado);
                    return ResponseEntity.ok(productoActualizado);
                })
                .orElseGet(() -> {
                    log.warn("[PUT /productos/{}] Producto no encontrado para actualizar", id);
                    return ResponseEntity.notFound().build();
                });
    }

    // POST para aumentar stock
    @PostMapping("/{id}/AumStock")
    public ResponseEntity<ProductoEntity> aumentarStock(
            @PathVariable Long id,
            @Valid @RequestBody StockUpdateDto dto) {

        log.info("[POST /productos/{}/AumStock] Aumentando {} unidades", id, dto.getCantidad());
        productoService.aumentarStock(id, dto.getCantidad()); // usa o SERVICE (validacions incluidas)

        ProductoEntity productoActualizado = productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Producto no encontrado"));

        return ResponseEntity.ok(productoActualizado);
    }

    // POST para reducir stock
    @PostMapping("/{id}/RedStock")
    public ResponseEntity<ProductoEntity> reducirStock(
            @PathVariable Long id,
            @Valid @RequestBody StockUpdateDto dto) {

        log.info("[POST /productos/{}/RedStock] Reduciendo {} unidades", id, dto.getCantidad());
        productoService.reducirStock(id, dto.getCantidad()); // usa o SERVICE (validacions incluidas)

        ProductoEntity productoActualizado = productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Producto no encontrado"));

        return ResponseEntity.ok(productoActualizado);
    }

    // putt actualizar foto con MultiparFile q ven a logica desde o ProductoService
    @PutMapping("/{id}/ActFoto")
    public ResponseEntity<ProductoEntity> actualizarFoto(@PathVariable Long id,
            @RequestParam MultipartFile imagenFile) {
       
        log.info("[PUT /productos/{}/ActFoto] Actualizando foto", id);
        ProductoEntity productoActualizado = productoService.actualizarFoto(id, imagenFile);

        log.info("[PUT /productos/{}/ActFoto] Foto actualizada correctamente", id);
        return ResponseEntity.ok(productoActualizado);
    }

}

/* unigetui
pa probar:
En local:
Usa Postman o curl para hacer un PUT al endpoint de subir imagen (/productos/{id}/ActFoto) con un archivo.
Verifica que la imagen aparece en la carpeta de destino y que la URL pública es accesible desde el navegador.
(Reemplaza {id} por el ID real del producto, por ejemplo: 1)
URL:
http://localhost:8080/productos/{1,2,3,4......id}/ActFoto
Método:
PUT

Tipo de Body:
Selecciona form-data

Clave:
imagenFile (debe coincidir con el nombre del parámetro en el backend)

Valor:
Elige un archivo de imagen desde tu PC.

    --¿Cómo subir un archivo en Postman?
Key (Clave)	| Value (Valor)	        | Tipo
imagenFile	| [Selecciona archivo]  | File
En el body, selecciona form-data.
En la fila donde pones la clave imagenFile, verás una columna llamada "Tipo" (a la izquierda del valor).
Haz clic en el desplegable y selecciona File.
Ahora, en la columna "Valor", aparecerá un botón para "Select Files" o "Elegir archivo".
Haz clic ahí y selecciona la imagen desde tu PC (no se arrastra, se selecciona con el explorador de archivos).

En Docker:
Levanta los contenedores (docker-compose up --build).
Haz el mismo test con Postman/curl apuntando a la URL del backend o Nginx.
Comprueba que la imagen se guarda en el volumen compartido y que puedes acceder a ella vía Nginx (por ejemplo, http://localhost:8081/static/assets/images/uploads/tuimagen.jpg).

URL:
http://localhost:8081/productos/{1,2.....id}/ActFoto
Método:
PUT

Tipo de Body:
form-data

Clave:
imagenFile

Valor:
Elige el archivo de imagen.

COMPROBACION: http://localhost:8081/static/assets/images/uploads/tuimagen.jpg

## resumen ##
1. Define la ruta física en application.properties y usa @Value en el backend.
2. Crea el directorio en el Dockerfile y/o cópialo si tienes imágenes de ejemplo.
3. Monta el volumen en docker-compose para persistencia.
4. Configura el alias en Nginx para servir las imágenes.
5. Guarda en la base de datos solo la URL pública.
6. (Opcional) Usa variables de entorno para cambiar la ruta según el entorno (local o Docker).


1. properties
2. dockerfile 
3. docker-compose
4. backend
4.5 mover logica do controller o service para usar o @value
5. nginx.conf
6. frontend
tocamos (productoController,productoService,crearPedidoCliente cambiomos o .img por .src, app.properties,
docker-compose.yml,nginx.conf, Dockerfile)



 */