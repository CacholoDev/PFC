package com.pfcdaw.pfcdaw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pfcdaw.pfcdaw.model.ClienteEntity;

public interface ClienteRepository extends JpaRepository<ClienteEntity, Long> {

}
