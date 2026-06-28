package com.lp3_taller.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lp3_taller.dto.PagoRequest;
import com.lp3_taller.dto.PagoResponse;
import com.lp3_taller.exception.ResourceNotFoundException;
import com.lp3_taller.mapper.EntityMapper;
import com.lp3_taller.model.Pago;
import com.lp3_taller.model.Reserva;
import com.lp3_taller.repository.PagoRepository;
import com.lp3_taller.repository.ReservaRepository;

@Service
@Transactional(readOnly = true)
public class PagoService {

    private final PagoRepository pagoRepository;
    private final ReservaRepository reservaRepository;
    private final EntityMapper mapper;

    public PagoService(PagoRepository pagoRepository,
                       ReservaRepository reservaRepository,
                       EntityMapper mapper) {
        this.pagoRepository = pagoRepository;
        this.reservaRepository = reservaRepository;
        this.mapper = mapper;
    }

    public List<PagoResponse> listar() {
        return pagoRepository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public PagoResponse obtener(Long id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago", id));
        return mapper.toResponse(pago);
    }

    public List<PagoResponse> listarPorReserva(Long reservaId) {
        return pagoRepository.findByReserva_Id(reservaId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public PagoResponse crear(PagoRequest request) {
        Reserva reserva = loadReserva(request.reservaId());
        Pago pago = mapper.toEntity(request);
        pago.setReserva(reserva);
        Pago saved = pagoRepository.save(pago);
        return mapper.toResponse(saved);
    }

    @Transactional
    public PagoResponse actualizar(Long id, PagoRequest request) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago", id));
        Reserva reserva = loadReserva(request.reservaId());
        mapper.applyToEntity(request, pago);
        pago.setReserva(reserva);
        Pago saved = pagoRepository.save(pago);
        return mapper.toResponse(saved);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!pagoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pago", id);
        }
        pagoRepository.deleteById(id);
    }

    private Reserva loadReserva(Long reservaId) {
        return reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", reservaId));
    }
}
