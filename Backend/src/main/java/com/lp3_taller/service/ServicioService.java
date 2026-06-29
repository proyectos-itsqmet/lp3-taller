package com.lp3_taller.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lp3_taller.dto.ServicioRequest;
import com.lp3_taller.dto.ServicioResponse;
import com.lp3_taller.exception.ResourceInUseException;
import com.lp3_taller.exception.ResourceNotFoundException;
import com.lp3_taller.mapper.EntityMapper;
import com.lp3_taller.model.Servicio;
import com.lp3_taller.repository.HotelRepository;
import com.lp3_taller.repository.ServicioRepository;

@Service
public class ServicioService {

    private final ServicioRepository servicioRepository;
    private final HotelRepository hotelRepository;
    private final EntityMapper mapper;

    public ServicioService(ServicioRepository servicioRepository,
                           HotelRepository hotelRepository,
                           EntityMapper mapper) {
        this.servicioRepository = servicioRepository;
        this.hotelRepository = hotelRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<ServicioResponse> listar() {
        return servicioRepository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ServicioResponse obtener(Long id) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio", id));
        return mapper.toResponse(servicio);
    }

    @Transactional
    public ServicioResponse crear(ServicioRequest request) {
        Servicio servicio = mapper.toEntity(request);
        Servicio saved = servicioRepository.save(servicio);
        return mapper.toResponse(saved);
    }

    @Transactional
    public ServicioResponse actualizar(Long id, ServicioRequest request) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio", id));
        mapper.applyToEntity(request, servicio);
        Servicio saved = servicioRepository.save(servicio);
        return mapper.toResponse(saved);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!servicioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Servicio", id);
        }
        long n = hotelRepository.contarHotelesPorServicio(id);
        if (n > 0) {
            throw new ResourceInUseException("No se puede eliminar el servicio porque esta asignado a " + n
                    + (n == 1 ? " hotel." : " hoteles.")
                    + " Quitelo primero de esos hoteles.");
        }
        servicioRepository.deleteById(id);
    }
}
