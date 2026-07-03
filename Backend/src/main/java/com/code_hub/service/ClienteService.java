package com.code_hub.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.code_hub.dto.ClienteRequest;
import com.code_hub.dto.ClienteResponse;
import com.code_hub.exception.DuplicateResourceException;
import com.code_hub.exception.ResourceInUseException;
import com.code_hub.exception.ResourceNotFoundException;
import com.code_hub.mapper.EntityMapper;
import com.code_hub.model.Cliente;
import com.code_hub.repository.ClienteRepository;
import com.code_hub.repository.ProyectoRepository;

@Service
@Transactional
public class ClienteService {

    private static final String RECURSO = "Cliente";

    private final ClienteRepository clienteRepository;
    private final ProyectoRepository proyectoRepository;
    private final EntityMapper mapper;

    public ClienteService(ClienteRepository clienteRepository,
                          ProyectoRepository proyectoRepository,
                          EntityMapper mapper) {
        this.clienteRepository = clienteRepository;
        this.proyectoRepository = proyectoRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        List<Cliente> clientes = clienteRepository.findAll();
        for (Cliente c : clientes) {
            c.getProyectos().size();
        }
        return clientes.stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse obtener(Long id) {
        Cliente c = findClienteOrThrow(id);
        c.getProyectos().size();
        return mapper.toResponse(c);
    }

    public ClienteResponse crear(ClienteRequest r) {
        if (clienteRepository.existsByRuc(r.ruc())) {
            throw new DuplicateResourceException("El ruc ya esta registrado");
        }
        Cliente c = mapper.toEntity(r);
        clienteRepository.save(c);
        return mapper.toResponse(c);
    }

    public ClienteResponse actualizar(Long id, ClienteRequest r) {
        Cliente c = findClienteOrThrow(id);
        if (!c.getRuc().equals(r.ruc()) && clienteRepository.existsByRuc(r.ruc())) {
            throw new DuplicateResourceException("El ruc ya esta registrado");
        }
        mapper.applyToEntity(r, c);
        clienteRepository.save(c);
        return mapper.toResponse(c);
    }

    public void eliminar(Long id) {
        Cliente c = findClienteOrThrow(id);
        long proyectos = proyectoRepository.countByCliente_Id(id);
        if (proyectos > 0) {
            throw new ResourceInUseException(
                    "No se puede eliminar el cliente porque tiene " + proyectos
                            + " proyecto(s) asociado(s). Elimine o reasigne los proyectos primero.");
        }
        clienteRepository.delete(c);
    }

    private Cliente findClienteOrThrow(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RECURSO, id));
    }
}
