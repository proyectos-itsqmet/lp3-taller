package com.code_hub.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.code_hub.dto.PerfilRequest;
import com.code_hub.dto.PerfilResponse;
import com.code_hub.service.PerfilService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/perfiles")
public class PerfilController {

    private final PerfilService perfilService;

    public PerfilController(PerfilService perfilService) {
        this.perfilService = perfilService;
    }

    @GetMapping
    public ResponseEntity<List<PerfilResponse>> listar() {
        return ResponseEntity.ok(perfilService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerfilResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(perfilService.obtener(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    public ResponseEntity<PerfilResponse> actualizar(@PathVariable Long id,
            @Valid @RequestBody PerfilRequest request) {
        return ResponseEntity.ok(perfilService.actualizar(id, request));
    }

}
