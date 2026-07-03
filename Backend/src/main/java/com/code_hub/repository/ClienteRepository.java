package com.code_hub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.code_hub.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByRuc(String ruc);
}
