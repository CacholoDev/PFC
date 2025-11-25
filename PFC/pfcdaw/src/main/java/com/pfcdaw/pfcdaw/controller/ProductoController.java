package com.pfcdaw.pfcdaw.controller;

import java.io.File;
import java.io.IOException;
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

    // putt con MultiparFile, actualizar foto
    @PutMapping("/{id}/ActFoto")
    public ResponseEntity<ProductoEntity> actualizarFoto(@PathVariable Long id,
            @RequestParam MultipartFile imagenFile) {
        log.info("[PUT /productos/{}/ActFoto] Actualizando foto", id);
        // facemos a url donde se vai gardar a imaxe
        if(imagenFile.isEmpty()) {
            log.error("[PUT /productos/{}/ActFoto] Error: archivo de imagen vacío", id);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El archivo de imagen está vacío");
        }
        String imagenUrl = "/assets/images/" + imagenFile.getOriginalFilename();
        // gardamos a imaxe na base de datos
        productoService.actualizarFoto(id, imagenUrl);
        // transferimos o ficheiro
        try {
            imagenFile.transferTo(new File(imagenUrl));
            log.info("[PUT /productos/{}/ActFoto] Imagen guardada en: {}", id, imagenUrl);
        } catch (IOException | IllegalStateException e) {
            log.error("[PUT /productos/{}/ActFoto] Error al guardar la imagen: {}", id, e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar la imagen");
        }
        // obtemos o producto actualizado para a resposta
        ProductoEntity productoActualizado = productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Producto no encontrado"));
        return ResponseEntity.ok(productoActualizado);
    }

}


/*
@PutMapping("/{id}/ActFoto")
public ResponseEntity<ProductoEntity> actualizarFoto(@PathVariable Long id,
        @RequestParam MultipartFile imagenFile) {
    log.info("[PUT /productos/{}/ActFoto] Actualizando foto", id);

    // Ruta física donde guardar la imagen
    String nombreArchivo = imagenFile.getOriginalFilename();
    if (nombreArchivo == null || nombreArchivo.contains("..")) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre de archivo no válido");
    }
    String rutaFisica = "src/main/resources/static/assets/images/" + nombreArchivo;
    String urlPublica = "/assets/images/" + nombreArchivo;

    // Guardar el archivo físicamente
    try {
        imagenFile.transferTo(new File(rutaFisica));
        log.info("[PUT /productos/{}/ActFoto] Imagen guardada en: {}", id, rutaFisica);
    } catch (Exception e) {
        log.error("[PUT /productos/{}/ActFoto] Error al guardar la imagen: {}", id, e.getMessage());
        throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar la imagen");
    }

    // Actualizar la URL en la base de datos
    productoService.actualizarFoto(id, urlPublica);

    // Devolver el producto actualizado
    ProductoEntity productoActualizado = productoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Producto no encontrado"));
    log.info("[PUT /productos/{}/ActFoto] Foto actualizada: {}", id, urlPublica);
    return ResponseEntity.ok(productoActualizado);
}


/* Configuración alternativa usando application.properties
// app.upload.dir=/ruta/absoluta/a/static/assets/images/
// app.upload.dir=C:/Users/Usuario/PFC/PFC/pfcdaw/src/main/resources/static/assets/images/
@Value("${app.upload.dir}")
private String uploadDir;
...
String nombreArchivo = imagenFile.getOriginalFilename();
String rutaFisica = uploadDir + nombreArchivo;
String urlPublica = "/assets/images/" + nombreArchivo;
*/