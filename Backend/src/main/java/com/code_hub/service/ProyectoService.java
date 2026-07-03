package com.code_hub.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.code_hub.dto.ProyectoRequest;
import com.code_hub.dto.ProyectoResponse;
import com.code_hub.exception.ResourceNotFoundException;
import com.code_hub.mapper.EntityMapper;
import com.code_hub.model.Cliente;
import com.code_hub.model.Empleado;
import com.code_hub.model.Proyecto;
import com.code_hub.repository.ClienteRepository;
import com.code_hub.repository.EmpleadoRepository;
import com.code_hub.repository.ProyectoRepository;

@Service
@Transactional
public class ProyectoService {

    private static final String RECURSO = "Proyecto";

    private final ProyectoRepository proyectoRepository;
    private final ClienteRepository clienteRepository;
    private final EmpleadoRepository empleadoRepository;
    private final EntityMapper mapper;

    public ProyectoService(ProyectoRepository proyectoRepository,
                           ClienteRepository clienteRepository,
                           EmpleadoRepository empleadoRepository,
                           EntityMapper mapper) {
        this.proyectoRepository = proyectoRepository;
        this.clienteRepository = clienteRepository;
        this.empleadoRepository = empleadoRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<ProyectoResponse> listar() {
        List<Proyecto> proyectos = proyectoRepository.findAll();
        for (Proyecto p : proyectos) {
            p.getEmpleados().size();
            p.getCliente();
        }
        return proyectos.stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProyectoResponse obtener(Long id) {
        Proyecto p = findProyectoOrThrow(id);
        p.getEmpleados().size();
        p.getCliente();
        return mapper.toResponse(p);
    }

    public ProyectoResponse crear(ProyectoRequest r) {
        Proyecto p = mapper.toEntity(r);
        p.setCliente(clienteOrThrow(r.clienteId()));
        p.setEmpleados(resolveEmpleados(r.empleadoIds()));
        proyectoRepository.save(p);
        return mapper.toResponse(p);
    }

    public ProyectoResponse actualizar(Long id, ProyectoRequest r) {
        Proyecto p = findProyectoOrThrow(id);
        mapper.applyToEntity(r, p);
        p.setCliente(clienteOrThrow(r.clienteId()));
        if (r.empleadoIds() != null) {
            p.setEmpleados(resolveEmpleados(r.empleadoIds()));
        }
        proyectoRepository.save(p);
        return mapper.toResponse(p);
    }

    public void eliminar(Long id) {
        Proyecto p = findProyectoOrThrow(id);
        proyectoRepository.delete(p);
    }

    public ProyectoResponse asignarEmpleado(Long proyectoId, Long empleadoId) {
        Proyecto p = findProyectoOrThrow(proyectoId);
        Empleado emp = empleadoOrThrow(empleadoId);
        p.getEmpleados().add(emp);
        proyectoRepository.save(p);
        return mapper.toResponse(p);
    }

    public ProyectoResponse desasignarEmpleado(Long proyectoId, Long empleadoId) {
        Proyecto p = findProyectoOrThrow(proyectoId);
        Empleado emp = empleadoOrThrow(empleadoId);
        p.getEmpleados().remove(emp);
        proyectoRepository.save(p);
        return mapper.toResponse(p);
    }

    private Set<Empleado> resolveEmpleados(Set<Long> empleadoIds) {
        Set<Empleado> empleados = new HashSet<>();
        if (empleadoIds == null) {
            return empleados;
        }
        for (Long id : empleadoIds) {
            empleados.add(empleadoOrThrow(id));
        }
        return empleados;
    }

    private Cliente clienteOrThrow(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
    }

    private Empleado empleadoOrThrow(Long id) {
        return empleadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado", id));
    }

    private Proyecto findProyectoOrThrow(Long id) {
        return proyectoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RECURSO, id));
    }
}
