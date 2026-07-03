package com.code_hub.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.code_hub.dto.ProyectoRequest;
import com.code_hub.dto.ProyectoResponse;
import com.code_hub.service.ProyectoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/proyectos")
public class ProyectoController {

    private final ProyectoService proyectoService;

    public ProyectoController(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
    }

    @GetMapping
    public ResponseEntity<List<ProyectoResponse>> listar() {
        return ResponseEntity.ok(proyectoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProyectoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(proyectoService.obtener(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    public ResponseEntity<ProyectoResponse> crear(@Valid @RequestBody ProyectoRequest request) {
        ProyectoResponse creado = proyectoService.crear(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.id())
                .toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    public ResponseEntity<ProyectoResponse> actualizar(@PathVariable Long id,
            @Valid @RequestBody ProyectoRequest request) {
        return ResponseEntity.ok(proyectoService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        proyectoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/empleados/{empleadoId}")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    public ResponseEntity<ProyectoResponse> asignarEmpleado(@PathVariable Long id,
            @PathVariable Long empleadoId) {
        return ResponseEntity.ok(proyectoService.asignarEmpleado(id, empleadoId));
    }

    @DeleteMapping("/{id}/empleados/{empleadoId}")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    public ResponseEntity<ProyectoResponse> desasignarEmpleado(@PathVariable Long id,
            @PathVariable Long empleadoId) {
        return ResponseEntity.ok(proyectoService.desasignarEmpleado(id, empleadoId));
    }

}
