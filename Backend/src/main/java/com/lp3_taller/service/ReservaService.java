package com.lp3_taller.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lp3_taller.dto.ReservaRequest;
import com.lp3_taller.dto.ReservaResponse;
import com.lp3_taller.exception.ResourceInUseException;
import com.lp3_taller.exception.ResourceNotFoundException;
import com.lp3_taller.mapper.EntityMapper;
import com.lp3_taller.model.Habitacion;
import com.lp3_taller.model.Reserva;
import com.lp3_taller.model.Usuario;
import com.lp3_taller.repository.HabitacionRepository;
import com.lp3_taller.repository.PagoRepository;
import com.lp3_taller.repository.ReservaRepository;
import com.lp3_taller.repository.UsuarioRepository;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final HabitacionRepository habitacionRepository;
    private final PagoRepository pagoRepository;
    private final EntityMapper mapper;

    public ReservaService(ReservaRepository reservaRepository,
                          UsuarioRepository usuarioRepository,
                          HabitacionRepository habitacionRepository,
                          PagoRepository pagoRepository,
                          EntityMapper mapper) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.habitacionRepository = habitacionRepository;
        this.pagoRepository = pagoRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> listar() {
        return reservaRepository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservaResponse obtener(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", id));
        return mapper.toResponse(reserva);
    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> listarPorUsuario(Long usuarioId) {
        return reservaRepository.findByUsuario_Id(usuarioId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public ReservaResponse crear(ReservaRequest request) {
        validarFechas(request);

        Reserva reserva = mapper.toEntity(request);
        reserva.setUsuario(resolverUsuario(request.usuarioId()));
        reserva.setHabitacion(resolverHabitacion(request.habitacionId()));

        Reserva guardada = reservaRepository.save(reserva);
        return mapper.toResponse(guardada);
    }

    @Transactional
    public ReservaResponse actualizar(Long id, ReservaRequest request) {
        validarFechas(request);

        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", id));

        mapper.applyToEntity(request, reserva);
        reserva.setUsuario(resolverUsuario(request.usuarioId()));
        reserva.setHabitacion(resolverHabitacion(request.habitacionId()));

        Reserva actualizada = reservaRepository.save(reserva);
        return mapper.toResponse(actualizada);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!reservaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reserva", id);
        }
        long n = pagoRepository.countByReserva_Id(id);
        if (n > 0) {
            throw new ResourceInUseException("No se puede eliminar la reserva porque tiene " + n
                    + (n == 1 ? " pago asociado." : " pagos asociados.")
                    + " Elimine primero los pagos.");
        }
        reservaRepository.deleteById(id);
    }

    private Usuario resolverUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", usuarioId));
    }

    private Habitacion resolverHabitacion(Long habitacionId) {
        return habitacionRepository.findById(habitacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Habitacion", habitacionId));
    }

    private void validarFechas(ReservaRequest request) {
        if (!request.fechaSalida().isAfter(request.fechaEntrada())) {
            throw new IllegalArgumentException(
                    "La fecha de salida debe ser posterior a la fecha de entrada");
        }
    }
}
