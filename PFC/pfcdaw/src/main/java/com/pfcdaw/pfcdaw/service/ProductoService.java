package com.pfcdaw.pfcdaw.service;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.pfcdaw.pfcdaw.model.ProductoEntity;
import com.pfcdaw.pfcdaw.repository.ProductoRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProductoService {

    private static final Logger log = LoggerFactory.getLogger(ProductoService.class);
    private final ProductoRepository productoRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // reducir stock
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

    // aumentar stock
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

    // actualizarFoto
    public ProductoEntity actualizarFoto(Long productoId, MultipartFile imagenFile) {
        ProductoEntity producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        // validar ficheiro
        if (imagenFile == null || imagenFile.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El archivo de imagen no puede estar vacío");
        }
        // validar e evitar posibles ataques usando ../ no nombre do arquivo e que non te vacio
        String nombreArchivo = imagenFile.getOriginalFilename();
        if (nombreArchivo == null || nombreArchivo.contains("..")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Nombre de archivo inválido: " + nombreArchivo);
        }

        // crear directorio uploads se non existe
        File directorioUploads = new File(uploadDir + "uploads/");
        if (!directorioUploads.exists()) {
            directorioUploads.mkdirs();
        }
        // aqui xenerariamos o nombre unico en caso de querer que si escribe o admin o mismo nombre non se pise
        // String extension = nombreArchivo.substring(nombreArchivo.lastIndexOf("."));
        // nombreArchivo = productoId + "_" + System.currentTimeMillis() OU
        // UUID.randomUUID() + extension;

        // rutas
        String rutaFisica = uploadDir + "uploads/" + nombreArchivo;
        String urlPublica = "/assets/images/uploads/" + nombreArchivo;

        // gardar arquivo
        try {
            imagenFile.transferTo(new File(rutaFisica));
            log.info("Imagen guardada en: {}", rutaFisica);
        } catch (IOException | IllegalStateException e) {
            log.error("Error guardando imagen: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error guardando imagen");
        }

        // actualizar BD
        producto.setImagenUrl(urlPublica);
        productoRepository.save(producto);
        log.info("Imagen actualizada para producto ID {}. URL: {}", productoId, urlPublica);
        return producto;
    }

}
















/*
 * @Service
 * 
 * @Transactional
 * public class ProductoService {
 * 
 * private static final Logger log =
 * LoggerFactory.getLogger(ProductoService.class);
 * private final ProductoRepository productoRepository;
 * 
 * // AÑADIR esta propiedad
 * 
 * @Value("${app.upload.dir}")
 * private String uploadDir;
 * 
 * public ProductoService(ProductoRepository productoRepository) {
 * this.productoRepository = productoRepository;
 * }
 * 
 * // ACTUALIZAR este método
 * public ProductoEntity actualizarFoto(Long productoId, MultipartFile
 * imagenFile) {
 * // Validaciones
 * if (imagenFile.isEmpty()) {
 * throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
 * "El archivo de imagen está vacío");
 * }
 * 
 * String nombreOriginal = imagenFile.getOriginalFilename();
 * if (nombreOriginal == null || nombreOriginal.contains("..")) {
 * throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
 * "Nombre de archivo no válido");
 * }
 * 
 * // Crear directorio uploads si no existe
 * File directorioUploads = new File(uploadDir + "uploads/");
 * if (!directorioUploads.exists()) {
 * directorioUploads.mkdirs();
 * }
 * 
 * // Generar nombre único
 * String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
 * String nombreArchivo = productoId + "_" + System.currentTimeMillis() +
 * extension;
 * 
 * // Rutas
 * String rutaFisica = uploadDir + "uploads/" + nombreArchivo;
 * String urlPublica = "/assets/images/uploads/" + nombreArchivo;
 * 
 * // Guardar archivo
 * try {
 * imagenFile.transferTo(new File(rutaFisica));
 * log.info("Imagen guardada en: {}", rutaFisica);
 * } catch (IOException e) {
 * log.error("Error guardando imagen: {}", e.getMessage());
 * throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
 * "Error guardando imagen");
 * }
 * 
 * // Actualizar BD
 * ProductoEntity producto = productoRepository.findById(productoId)
 * .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
 * "Producto no encontrado"));
 * 
 * producto.setImagenUrl(urlPublica);
 * ProductoEntity productoActualizado = productoRepository.save(producto);
 * 
 * log.info("Imagen actualizada para producto ID {}. URL: {}", productoId,
 * urlPublica);
 * return productoActualizado;
 * }
 * }
 */