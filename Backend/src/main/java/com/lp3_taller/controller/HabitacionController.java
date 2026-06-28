package com.lp3_taller.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.lp3_taller.dto.HabitacionRequest;
import com.lp3_taller.dto.HabitacionResponse;
import com.lp3_taller.service.HabitacionService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/habitaciones")
public class HabitacionController {

    private final HabitacionService service;

    public HabitacionController(HabitacionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<HabitacionResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<HabitacionResponse>> listarPorHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(service.listarPorHotel(hotelId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HabitacionResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @PostMapping
    public ResponseEntity<HabitacionResponse> crear(@Valid @RequestBody HabitacionRequest request,
                                                    UriComponentsBuilder uriBuilder) {
        HabitacionResponse creada = service.crear(request);
        URI location = uriBuilder.path("/api/habitaciones/{id}")
                .buildAndExpand(creada.id())
                .toUri();
        return ResponseEntity.created(location).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HabitacionResponse> actualizar(@PathVariable Long id,
                                                         @Valid @RequestBody HabitacionRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
