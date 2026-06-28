package com.lp3_taller.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lp3_taller.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByEmail(String email);

    boolean existsByDocumento(String documento);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByDocumentoAndIdNot(String documento, Long id);
}
