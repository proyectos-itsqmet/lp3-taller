package com.lp3_taller.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lp3_taller.model.Hotel;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    @Query("select count(h) from Hotel h join h.servicios s where s.id = :servicioId")
    long contarHotelesPorServicio(@Param("servicioId") Long servicioId);
}
