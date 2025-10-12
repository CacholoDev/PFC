package com.pfcdaw.pfcdaw.controller;


import java.util.List;

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

    private final ProductoRepository productoRepository;

    public ProductoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

//listar productos nn poñemos path porque colle o requestmapping do controlador
    @GetMapping
    public ResponseEntity<List<ProductoEntity>> getAllProductos() {
        List<ProductoEntity> productos = productoRepository.findAll();
        return ResponseEntity.ok(productos);
    }
    
//listar por id
    @GetMapping("/{id}")
    public ResponseEntity<ProductoEntity> getProductoById(@PathVariable Long id) {
        return productoRepository.findById(id)
        .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    
    }   
    
 //crear producto
    @PostMapping
    public ResponseEntity<ProductoEntity> createProducto(@Valid @RequestBody ProductoEntity producto) {
        ProductoEntity nuevoProducto = productoRepository.save(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }
 

  //delete producto
    @DeleteMapping("/{id}")  
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
        if (!productoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productoRepository.deleteById(id);
        return ResponseEntity.noContent().build(); //204 No Content
    }

 //actualizar producto
    @PutMapping("/{id}")
    public ResponseEntity<ProductoEntity> updateProducto(@PathVariable Long id,@Valid @RequestBody ProductoEntity productoDetalles) {
        return productoRepository.findById(id)
            .map(producto -> {
                producto.setNombre(productoDetalles.getNombre());
                producto.setDescripcion(productoDetalles.getDescripcion());
                producto.setPrecio(productoDetalles.getPrecio());
                ProductoEntity productoActualizado = productoRepository.save(producto);
                return ResponseEntity.ok(productoActualizado);
            })
            .orElse(ResponseEntity.notFound().build());
    }


}
