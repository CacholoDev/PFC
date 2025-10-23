package com.pfcdaw.pfcdaw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pfcdaw.pfcdaw.model.PedidoEntity;

public interface PedidoRepository extends JpaRepository<PedidoEntity, Long> {
    
    // Busca todos os pedidos dun cliente específico
    List<PedidoEntity> findByClienteId(Long clienteId);

}
