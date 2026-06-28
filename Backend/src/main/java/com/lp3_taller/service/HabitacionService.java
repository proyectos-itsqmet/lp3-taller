package com.lp3_taller.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lp3_taller.dto.HabitacionRequest;
import com.lp3_taller.dto.HabitacionResponse;
import com.lp3_taller.exception.ResourceInUseException;
import com.lp3_taller.exception.ResourceNotFoundException;
import com.lp3_taller.mapper.EntityMapper;
import com.lp3_taller.model.Habitacion;
import com.lp3_taller.model.Hotel;
import com.lp3_taller.repository.HabitacionRepository;
import com.lp3_taller.repository.HotelRepository;
import com.lp3_taller.repository.ReservaRepository;

@Service
public class HabitacionService {

    private final HabitacionRepository habitacionRepository;
    private final HotelRepository hotelRepository;
    private final ReservaRepository reservaRepository;
    private final EntityMapper mapper;

    public HabitacionService(HabitacionRepository habitacionRepository,
                             HotelRepository hotelRepository,
                             ReservaRepository reservaRepository,
                             EntityMapper mapper) {
        this.habitacionRepository = habitacionRepository;
        this.hotelRepository = hotelRepository;
        this.reservaRepository = reservaRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<HabitacionResponse> listar() {
        return habitacionRepository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public HabitacionResponse obtener(Long id) {
        Habitacion habitacion = habitacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Habitacion", id));
        return mapper.toResponse(habitacion);
    }

    @Transactional(readOnly = true)
    public List<HabitacionResponse> listarPorHotel(Long hotelId) {
        return habitacionRepository.findByHotel_Id(hotelId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public HabitacionResponse crear(HabitacionRequest request) {
        Habitacion habitacion = mapper.toEntity(request);
        habitacion.setHotel(resolverHotel(request.hotelId()));
        Habitacion guardada = habitacionRepository.save(habitacion);
        return mapper.toResponse(guardada);
    }

    @Transactional
    public HabitacionResponse actualizar(Long id, HabitacionRequest request) {
        Habitacion habitacion = habitacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Habitacion", id));
        mapper.applyToEntity(request, habitacion);
        habitacion.setHotel(resolverHotel(request.hotelId()));
        Habitacion guardada = habitacionRepository.save(habitacion);
        return mapper.toResponse(guardada);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!habitacionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Habitacion", id);
        }
        long n = reservaRepository.countByHabitacion_Id(id);
        if (n > 0) {
            throw new ResourceInUseException("No se puede eliminar la habitacion porque tiene " + n
                    + (n == 1 ? " reserva asociada." : " reservas asociadas.")
                    + " Elimine primero las reservas.");
        }
        habitacionRepository.deleteById(id);
    }

    private Hotel resolverHotel(Long hotelId) {
        return hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", hotelId));
    }
}
