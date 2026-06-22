package com.lp3_taller.service;

import com.lp3_taller.model.Usuario;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final List<Usuario> usuarios = List.of(
            new Usuario(1L, "Ana", "Perez", "ana.perez@mail.com", "0991112233", "0102030405"),
            new Usuario(2L, "Luis", "Gomez", "luis.gomez@mail.com", "0994445566", "0203040506"),
            new Usuario(3L, "Maria", "Torres", "maria.torres@mail.com", "0997778899", "0304050607")
    );

    public List<Usuario> listar() {
        return usuarios;
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarios.stream()
                .filter(usuario -> usuario.getId().equals(id))
                .findFirst();
    }
}
