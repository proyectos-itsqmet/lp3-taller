package com.lp3_taller.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lp3_taller.model.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByUsuario_Id(Long usuarioId);

    long countByUsuario_Id(Long usuarioId);

    long countByHabitacion_Id(Long habitacionId);
}
