package com.pfcdaw.pfcdaw.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pfcdaw.pfcdaw.model.ProductoEntity;

public interface ProductoRepository extends JpaRepository<ProductoEntity, Long> {
    // No es necesario agregar métodos adicionales, JpaRepository ya proporciona
    // métodos CRUD como save, findById, findAll, deleteById, etc.
    
}
