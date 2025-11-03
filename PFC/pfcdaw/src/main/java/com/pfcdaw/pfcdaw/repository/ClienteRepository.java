package com.pfcdaw.pfcdaw.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pfcdaw.pfcdaw.model.ClienteEntity;

public interface ClienteRepository extends JpaRepository<ClienteEntity, Long> {
    Optional<ClienteEntity> findByEmail(String email);
}
