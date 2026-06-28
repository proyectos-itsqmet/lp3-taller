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

import com.lp3_taller.dto.PagoRequest;
import com.lp3_taller.dto.PagoResponse;
import com.lp3_taller.service.PagoService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoService service;

    public PagoController(PagoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<PagoResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/reserva/{reservaId}")
    public ResponseEntity<List<PagoResponse>> listarPorReserva(@PathVariable Long reservaId) {
        return ResponseEntity.ok(service.listarPorReserva(reservaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @PostMapping
    public ResponseEntity<PagoResponse> crear(@Valid @RequestBody PagoRequest request,
                                              UriComponentsBuilder uriBuilder) {
        PagoResponse created = service.crear(request);
        URI location = uriBuilder.path("/api/pagos/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagoResponse> actualizar(@PathVariable Long id,
                                                   @Valid @RequestBody PagoRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
