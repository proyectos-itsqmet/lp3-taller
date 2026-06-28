package com.lp3_taller.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lp3_taller.dto.HotelRequest;
import com.lp3_taller.dto.HotelResponse;
import com.lp3_taller.exception.ResourceInUseException;
import com.lp3_taller.exception.ResourceNotFoundException;
import com.lp3_taller.mapper.EntityMapper;
import com.lp3_taller.model.Hotel;
import com.lp3_taller.model.Servicio;
import com.lp3_taller.repository.HabitacionRepository;
import com.lp3_taller.repository.HotelRepository;
import com.lp3_taller.repository.ServicioRepository;

@Service
public class HotelService {

    private static final String RECURSO = "Hotel";
    private static final String RECURSO_SERVICIO = "Servicio";

    private final HotelRepository hotelRepository;
    private final ServicioRepository servicioRepository;
    private final HabitacionRepository habitacionRepository;
    private final EntityMapper mapper;

    public HotelService(HotelRepository hotelRepository,
                        ServicioRepository servicioRepository,
                        HabitacionRepository habitacionRepository,
                        EntityMapper mapper) {
        this.hotelRepository = hotelRepository;
        this.servicioRepository = servicioRepository;
        this.habitacionRepository = habitacionRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<HotelResponse> listar() {
        List<HotelResponse> responses = new ArrayList<>();
        for (Hotel hotel : hotelRepository.findAll()) {
            hotel.getServicios().size(); 
            responses.add(mapper.toResponse(hotel));
        }
        return responses;
    }

    @Transactional(readOnly = true)
    public HotelResponse obtener(Long id) {
        Hotel hotel = findHotelOrThrow(id);
        hotel.getServicios().size();
        return mapper.toResponse(hotel);
    }

    @Transactional
    public HotelResponse crear(HotelRequest request) {
        Hotel hotel = mapper.toEntity(request);
        hotel.setServicios(resolveServicios(request.servicioIds()));
        Hotel saved = hotelRepository.save(hotel);
        return mapper.toResponse(saved);
    }

    @Transactional
    public HotelResponse actualizar(Long id, HotelRequest request) {
        Hotel hotel = findHotelOrThrow(id);
        mapper.applyToEntity(request, hotel);
        if (request.servicioIds() != null) {
            hotel.setServicios(resolveServicios(request.servicioIds()));
        }
        Hotel saved = hotelRepository.save(hotel);
        return mapper.toResponse(saved);
    }

    @Transactional
    public void eliminar(Long id) {
        Hotel hotel = findHotelOrThrow(id);
        long n = habitacionRepository.countByHotel_Id(id);
        if (n > 0) {
            throw new ResourceInUseException("No se puede eliminar el hotel porque tiene " + n
                    + (n == 1 ? " habitacion asociada." : " habitaciones asociadas.")
                    + " Elimine primero las habitaciones.");
        }
        hotelRepository.delete(hotel);
    }

    @Transactional
    public HotelResponse asignarServicio(Long hotelId, Long servicioId) {
        Hotel hotel = findHotelOrThrow(hotelId);
        Servicio servicio = servicioRepository.findById(servicioId)
                .orElseThrow(() -> new ResourceNotFoundException(RECURSO_SERVICIO, servicioId));
        hotel.getServicios().add(servicio);
        Hotel saved = hotelRepository.save(hotel);
        return mapper.toResponse(saved);
    }

    @Transactional
    public HotelResponse desasignarServicio(Long hotelId, Long servicioId) {
        Hotel hotel = findHotelOrThrow(hotelId);
        Servicio servicio = servicioRepository.findById(servicioId)
                .orElseThrow(() -> new ResourceNotFoundException(RECURSO_SERVICIO, servicioId));
        hotel.getServicios().remove(servicio);
        Hotel saved = hotelRepository.save(hotel);
        return mapper.toResponse(saved);
    }

    private Hotel findHotelOrThrow(Long id) {
        return hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RECURSO, id));
    }

    private Set<Servicio> resolveServicios(Set<Long> servicioIds) {
        Set<Servicio> servicios = new HashSet<>();
        if (servicioIds == null || servicioIds.isEmpty()) {
            return servicios;
        }
        for (Long servicioId : servicioIds) {
            Servicio servicio = servicioRepository.findById(servicioId)
                    .orElseThrow(() -> new ResourceNotFoundException(RECURSO_SERVICIO, servicioId));
            servicios.add(servicio);
        }
        return servicios;
    }
}
