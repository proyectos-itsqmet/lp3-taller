package com.code_hub.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.code_hub.dto.PerfilRequest;
import com.code_hub.dto.PerfilResponse;
import com.code_hub.exception.ResourceNotFoundException;
import com.code_hub.mapper.EntityMapper;
import com.code_hub.model.Perfil;
import com.code_hub.repository.PerfilRepository;

@Service
@Transactional
public class PerfilService {

    private static final String RECURSO = "Perfil";

    private final PerfilRepository perfilRepository;
    private final EntityMapper mapper;

    public PerfilService(PerfilRepository perfilRepository, EntityMapper mapper) {
        this.perfilRepository = perfilRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<PerfilResponse> listar() {
        return perfilRepository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PerfilResponse obtener(Long id) {
        Perfil p = findPerfilOrThrow(id);
        return mapper.toResponse(p);
    }

    public PerfilResponse actualizar(Long id, PerfilRequest r) {
        Perfil p = findPerfilOrThrow(id);
        mapper.applyToEntity(r, p);
        perfilRepository.save(p);
        return mapper.toResponse(p);
    }

    private Perfil findPerfilOrThrow(Long id) {
        return perfilRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RECURSO, id));
    }
}
