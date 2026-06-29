package com.lp3_taller.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lp3_taller.dto.UsuarioRequest;
import com.lp3_taller.dto.UsuarioResponse;
import com.lp3_taller.exception.DuplicateResourceException;
import com.lp3_taller.exception.ResourceInUseException;
import com.lp3_taller.exception.ResourceNotFoundException;
import com.lp3_taller.mapper.EntityMapper;
import com.lp3_taller.model.Usuario;
import com.lp3_taller.repository.ReservaRepository;
import com.lp3_taller.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ReservaRepository reservaRepository;
    private final EntityMapper mapper;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          ReservaRepository reservaRepository,
                          EntityMapper mapper) {
        this.usuarioRepository = usuarioRepository;
        this.reservaRepository = reservaRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtener(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
        return mapper.toResponse(usuario);
    }

    @Transactional
    public UsuarioResponse crear(UsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Usuario", "email", request.email());
        }
        if (usuarioRepository.existsByDocumento(request.documento())) {
            throw new DuplicateResourceException("Usuario", "documento", request.documento());
        }

        Usuario usuario = mapper.toEntity(request);
        Usuario guardado = usuarioRepository.save(usuario);
        return mapper.toResponse(guardado);
    }

    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));

        if (usuarioRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new DuplicateResourceException("Usuario", "email", request.email());
        }
        if (usuarioRepository.existsByDocumentoAndIdNot(request.documento(), id)) {
            throw new DuplicateResourceException("Usuario", "documento", request.documento());
        }

        mapper.applyToEntity(request, usuario);
        Usuario actualizado = usuarioRepository.save(usuario);
        return mapper.toResponse(actualizado);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario", id);
        }
        long n = reservaRepository.countByUsuario_Id(id);
        if (n > 0) {
            throw new ResourceInUseException("No se puede eliminar el usuario porque tiene " + n
                    + (n == 1 ? " reserva asociada." : " reservas asociadas.")
                    + " Elimine primero las reservas.");
        }
        usuarioRepository.deleteById(id);
    }
}
