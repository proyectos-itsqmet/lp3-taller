package com.code_hub.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.code_hub.dto.UsuarioRequest;
import com.code_hub.dto.UsuarioResponse;
import com.code_hub.exception.DuplicateResourceException;
import com.code_hub.exception.ResourceNotFoundException;
import com.code_hub.mapper.EntityMapper;
import com.code_hub.model.Usuario;
import com.code_hub.repository.UsuarioRepository;

@Service
@Transactional
public class UsuarioService {

    private static final String RECURSO = "Usuario";

    private final UsuarioRepository usuarioRepository;
    private final EntityMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, EntityMapper mapper, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtener(Long id) {
        Usuario u = findUsuarioOrThrow(id);
        return mapper.toResponse(u);
    }

    public UsuarioResponse crear(UsuarioRequest r) {
        if (usuarioRepository.existsByUsername(r.username())) {
            throw new DuplicateResourceException("El username ya esta registrado");
        }
        if (usuarioRepository.existsByEmail(r.email())) {
            throw new DuplicateResourceException("El email ya esta registrado");
        }
        Usuario u = mapper.toEntity(r);
        u.setPassword(passwordEncoder.encode(r.password()));
        usuarioRepository.save(u);
        return mapper.toResponse(u);
    }

    public UsuarioResponse actualizar(Long id, UsuarioRequest r) {
        Usuario u = findUsuarioOrThrow(id);
        if (!u.getUsername().equals(r.username()) && usuarioRepository.existsByUsername(r.username())) {
            throw new DuplicateResourceException("El username ya esta registrado");
        }
        if (!u.getEmail().equals(r.email()) && usuarioRepository.existsByEmail(r.email())) {
            throw new DuplicateResourceException("El email ya esta registrado");
        }
        mapper.applyToEntity(r, u);
        u.setPassword(passwordEncoder.encode(r.password()));
        usuarioRepository.save(u);
        return mapper.toResponse(u);
    }

    public void eliminar(Long id) {
        Usuario u = findUsuarioOrThrow(id);
        usuarioRepository.delete(u);
    }

    private Usuario findUsuarioOrThrow(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RECURSO, id));
    }
}
