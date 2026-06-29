package com.lp3_taller.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lp3_taller.model.Pago;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByReserva_Id(Long reservaId);

    long countByReserva_Id(Long reservaId);
}
