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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.lp3_taller.dto.HotelRequest;
import com.lp3_taller.dto.HotelResponse;
import com.lp3_taller.service.HotelService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/hoteles")
public class HotelController {

    private final HotelService service;

    public HotelController(HotelService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<HotelResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @PostMapping
    public ResponseEntity<HotelResponse> crear(@Valid @RequestBody HotelRequest request) {
        HotelResponse created = service.crear(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HotelResponse> actualizar(@PathVariable Long id,
                                                    @Valid @RequestBody HotelRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/servicios/{servicioId}")
    public ResponseEntity<HotelResponse> asignarServicio(@PathVariable Long id,
                                                         @PathVariable Long servicioId) {
        return ResponseEntity.ok(service.asignarServicio(id, servicioId));
    }

    @DeleteMapping("/{id}/servicios/{servicioId}")
    public ResponseEntity<HotelResponse> desasignarServicio(@PathVariable Long id,
                                                            @PathVariable Long servicioId) {
        return ResponseEntity.ok(service.desasignarServicio(id, servicioId));
    }
}
