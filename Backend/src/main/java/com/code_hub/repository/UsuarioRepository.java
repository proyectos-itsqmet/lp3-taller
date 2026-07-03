package com.code_hub.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.code_hub.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
