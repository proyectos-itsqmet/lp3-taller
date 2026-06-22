package com.lp3_taller.service;

import com.lp3_taller.model.Habitacion;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HabitacionService {

    private final List<Habitacion> habitaciones = List.of(
            new Habitacion(1L, 1L, "101", "SIMPLE", 45.00, 1, true, "https://images.pexels.com/photos/28174031/pexels-photo-28174031.jpeg"),
            new Habitacion(2L, 1L, "102", "DOBLE", 70.00, 2, true, "https://images.pexels.com/photos/34016639/pexels-photo-34016639.jpeg"),
            new Habitacion(3L, 2L, "201", "SUITE", 120.00, 2, true, "https://images.pexels.com/photos/28909289/pexels-photo-28909289.jpeg"),
            new Habitacion(4L, 2L, "202", "DOBLE", 80.00, 2, false, "https://images.pexels.com/photos/37658584/pexels-photo-37658584.jpeg")
    );

    public List<Habitacion> listar() {
        return habitaciones;
    }

    public Optional<Habitacion> buscarPorId(Long id) {
        return habitaciones.stream()
                .filter(habitacion -> habitacion.getId().equals(id))
                .findFirst();
    }

    public List<Habitacion> listarPorHotel(Long hotelId) {
        return habitaciones.stream()
                .filter(habitacion -> habitacion.getHotelId().equals(hotelId))
                .toList();
    }
}
