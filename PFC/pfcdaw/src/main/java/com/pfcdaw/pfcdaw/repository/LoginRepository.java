package com.pfcdaw.pfcdaw.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pfcdaw.pfcdaw.model.LoginUsuario;

public interface LoginRepository extends JpaRepository<LoginUsuario, Long> {
    LoginUsuario findByEmail(String mail);
}
