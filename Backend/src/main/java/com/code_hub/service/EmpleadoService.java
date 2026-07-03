package com.code_hub.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.code_hub.dto.EmpleadoRequest;
import com.code_hub.dto.EmpleadoResponse;
import com.code_hub.exception.DuplicateResourceException;
import com.code_hub.exception.ResourceInUseException;
import com.code_hub.exception.ResourceNotFoundException;
import com.code_hub.mapper.EntityMapper;
import com.code_hub.model.Empleado;
import com.code_hub.repository.EmpleadoRepository;

@Service
@Transactional
public class EmpleadoService {

    private static final String RECURSO = "Empleado";

    private final EmpleadoRepository empleadoRepository;
    private final EntityMapper mapper;

    public EmpleadoService(EmpleadoRepository empleadoRepository, EntityMapper mapper) {
        this.empleadoRepository = empleadoRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<EmpleadoResponse> listar() {
        return empleadoRepository.findAll().stream()
                .map(e -> {
                    e.getProyectos().size();
                    e.getPerfil();
                    return mapper.toResponse(e);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public EmpleadoResponse obtener(Long id) {
        Empleado e = findEmpleadoOrThrow(id);
        e.getProyectos().size();
        e.getPerfil();
        return mapper.toResponse(e);
    }

    public EmpleadoResponse crear(EmpleadoRequest r) {
        if (empleadoRepository.existsByEmail(r.email())) {
            throw new DuplicateResourceException("El email ya esta registrado");
        }
        if (empleadoRepository.existsByDocumento(r.documento())) {
            throw new DuplicateResourceException("El documento ya esta registrado");
        }
        Empleado e = mapper.toEntity(r);
        empleadoRepository.save(e);
        return mapper.toResponse(e);
    }

    public EmpleadoResponse actualizar(Long id, EmpleadoRequest r) {
        Empleado e = findEmpleadoOrThrow(id);
        mapper.applyToEntity(r, e);
        empleadoRepository.save(e);
        return mapper.toResponse(e);
    }

    public void eliminar(Long id) {
        Empleado e = findEmpleadoOrThrow(id);
        long proyectos = empleadoRepository.contarProyectosPorEmpleado(id);
        if (proyectos > 0) {
            throw new ResourceInUseException("No se puede eliminar el empleado porque esta asignado a " + proyectos + " proyecto(s). Desasignelo primero.");
        }
        empleadoRepository.delete(e);
    }

    private Empleado findEmpleadoOrThrow(Long id) {
        return empleadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RECURSO, id));
    }
}
