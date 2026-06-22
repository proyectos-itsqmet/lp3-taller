package com.lp3_taller.service;

import com.lp3_taller.model.Reserva;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {

    private final List<Reserva> reservas = List.of(
            new Reserva(1L, 1L, 1L, LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 3), "CONFIRMADA", 90.00),
            new Reserva(2L, 2L, 3L, LocalDate.of(2026, 7, 5), LocalDate.of(2026, 7, 8), "PENDIENTE", 360.00)
    );

    public List<Reserva> listar() {
        return reservas;
    }

    public Optional<Reserva> buscarPorId(Long id) {
        return reservas.stream()
                .filter(reserva -> reserva.getId().equals(id))
                .findFirst();
    }

    public List<Reserva> listarPorUsuario(Long usuarioId) {
        return reservas.stream()
                .filter(reserva -> reserva.getUsuarioId().equals(usuarioId))
                .toList();
    }
}
