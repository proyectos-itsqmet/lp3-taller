package com.code_hub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.code_hub.model.Proyecto;

public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {

    long countByCliente_Id(Long clienteId);
}
