package com.lp3_taller.service;

import com.lp3_taller.model.Pago;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PagoService {

    private final List<Pago> pagos = List.of(
            new Pago(1L, 1L, 90.00, LocalDate.of(2026, 7, 1), "TARJETA"),
            new Pago(2L, 2L, 100.00, LocalDate.of(2026, 7, 5), "EFECTIVO")
    );

    public List<Pago> listar() {
        return pagos;
    }

    public Optional<Pago> buscarPorId(Long id) {
        return pagos.stream()
                .filter(pago -> pago.getId().equals(id))
                .findFirst();
    }

    public List<Pago> listarPorReserva(Long reservaId) {
        return pagos.stream()
                .filter(pago -> pago.getReservaId().equals(reservaId))
                .toList();
    }
}
