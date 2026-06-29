package com.lp3_taller.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lp3_taller.model.Habitacion;

public interface HabitacionRepository extends JpaRepository<Habitacion, Long> {

    List<Habitacion> findByHotel_Id(Long hotelId);

    long countByHotel_Id(Long hotelId);
}
