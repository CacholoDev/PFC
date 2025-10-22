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

import com.pfcdaw.pfcdaw.model.ProductoEntity;
import com.pfcdaw.pfcdaw.repository.ProductoRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    private static final Logger log = LoggerFactory.getLogger(ProductoController.class);
    private final ProductoRepository productoRepository;

    public ProductoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // listar productos nn poñemos path porque colle o requestmapping do controlador
    @GetMapping
    public ResponseEntity<List<ProductoEntity>> getAllProductos() {
        log.info("Listando todos los productos"); // usase info para mensaxes informativas / accions normales
        List<ProductoEntity> productos = productoRepository.findAll();
        log.debug("Productos encontrados: {}", productos.size()); // usase debug para mensaxes de depuración / detalles
                                                                  // tecnicos
        return ResponseEntity.ok(productos);
    }

    // listar por id
    @GetMapping("/{id}")
    public ResponseEntity<ProductoEntity> getProductoById(@PathVariable Long id) {
        log.info("Buscando producto con ID: {}", id);
        return productoRepository.findById(id)
                .map(producto -> {
                    log.info("Producto encontrado: {}", producto.getNombre());
                    return ResponseEntity.ok(producto);
                })
                .orElseGet(() -> {
                    log.warn("Producto con ID {} no encontrado", id); // usase warn para mensaxes de advertencia /
                                                                      // posibles problemas
                    return ResponseEntity.notFound().build();
                });

    }

    // crear producto
    @PostMapping
    public ResponseEntity<ProductoEntity> createProducto(@Valid @RequestBody ProductoEntity producto) {
        log.info("Creando nuevo producto: {}", producto.getNombre());
        ProductoEntity nuevoProducto = productoRepository.save(producto);
        log.info("Producto creado con ID: {}", nuevoProducto.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }

    // delete producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
        log.info("Eliminando producto con ID: {}, y nombre: {}", id,
                productoRepository.findById(id).map(ProductoEntity::getNombre).orElseGet(() -> "No encontrado"));
        if (!productoRepository.existsById(id)) {
            log.warn("Producto con ID {} no encontrado", id);
            return ResponseEntity.notFound().build();
        }
        log.info("Producto con ID {} eliminado", id);
        productoRepository.deleteById(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // actualizar producto
    @PutMapping("/{id}")
    public ResponseEntity<ProductoEntity> updateProducto(@PathVariable Long id, @Valid @RequestBody ProductoEntity p) {
        log.info("Solicitud PUT recibida para producto ID={}", id);
        log.debug("Datos recibidos para actualización: {}", p);
        return productoRepository.findById(id)
                .map(producto -> {
                    log.debug("Producto antes de actualizar: id={}, nombre={}, descripcion={}, precio={}",
                            producto.getId(), producto.getNombre(), producto.getDescripcion(), producto.getPrecio());

                    producto.setNombre(p.getNombre());
                    producto.setDescripcion(p.getDescripcion());
                    producto.setPrecio(p.getPrecio());

                    ProductoEntity productoActualizado = productoRepository.save(producto);

                    log.info("Producto actualizado correctamente: id={}", productoActualizado.getId());
                    log.debug("Producto después de actualizar: {}", productoActualizado);
                    return ResponseEntity.ok(productoActualizado);
                })
                .orElseGet(() -> {
                    log.warn("No se puede actualizar: producto con ID {} no encontrado", id);
                    return ResponseEntity.notFound().build();
                });
    }

}
