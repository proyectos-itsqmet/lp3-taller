package com.code_hub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.code_hub.model.Empleado;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    boolean existsByEmail(String email);

    boolean existsByDocumento(String documento);

    @Query("select count(p) from Proyecto p join p.empleados e where e.id = :empleadoId")
    long contarProyectosPorEmpleado(@Param("empleadoId") Long empleadoId);
}
