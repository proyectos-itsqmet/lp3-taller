package com.code_hub.controller;

import com.code_hub.dto.AuthResponse;
import com.code_hub.dto.LoginRequest;
import com.code_hub.model.Usuario;
import com.code_hub.repository.UsuarioRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final UsuarioRepository usuarioRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          SecurityContextRepository securityContextRepository,
                          UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req,
                                              HttpServletRequest request,
                                              HttpServletResponse response) {
        // Si las credenciales son malas, authenticate lanza AuthenticationException (la maneja el GlobalExceptionHandler => 401)
        var token = new UsernamePasswordAuthenticationToken(req.username(), req.password());
        Authentication auth = authenticationManager.authenticate(token);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        // Guarda el contexto en la sesion HTTP (crea el JSESSIONID)
        securityContextRepository.saveContext(context, request, response);

        Usuario u = usuarioRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        AuthResponse body = new AuthResponse(u.getId(), u.getUsername(), u.getNombre(), u.getEmail(), u.getRol().name());
        return ResponseEntity.ok(body);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return ResponseEntity.status(401).build();
        }

        Usuario u = usuarioRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        AuthResponse body = new AuthResponse(u.getId(), u.getUsername(), u.getNombre(), u.getEmail(), u.getRol().name());
        return ResponseEntity.ok(body);
    }
}
