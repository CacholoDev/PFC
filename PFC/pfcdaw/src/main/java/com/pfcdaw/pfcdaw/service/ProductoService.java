package com.pfcdaw.pfcdaw.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.pfcdaw.pfcdaw.model.ProductoEntity;
import com.pfcdaw.pfcdaw.repository.ProductoRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProductoService {

    private static final Logger log = LoggerFactory.getLogger(ProductoService.class);
    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public void reducirStock(Long productoId, int cantidad) {

        log.info("Reduciendo stock del producto ID {} en {}", productoId, cantidad);
        ProductoEntity producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        // verificar si hay stock, por que si o stock e menor a cantidad a restar, non hai stock suficiente
        if (producto.getStock() < cantidad) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Stock insuficiente para el producto ID " + productoId);
        }
        // reducimos stock
        producto.setStock(producto.getStock() - cantidad);
        // gardamos
        productoRepository.save(producto);
        log.info("Stock reducido en {} para el producto ID {}. Nuevo stock: {}", cantidad, productoId,
                producto.getStock());
    }

    public void aumentarStock(Long productoId, int cantidad) {
        ProductoEntity producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        if (cantidad < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La cantidad debe ser mayor que 0 " + productoId);

        }
        producto.setStock(producto.getStock() + cantidad);
        productoRepository.save(producto);
        log.info("Stock aumentado en {} para el producto ID {}. Nuevo stock: {}", cantidad, productoId,
                producto.getStock());
    }

}