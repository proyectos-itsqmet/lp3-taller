package com.lp3_taller.controller;

import com.lp3_taller.model.Hotel;
import com.lp3_taller.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/hoteles")
public class HotelController {

    @Autowired
    private HotelService service;

    @GetMapping
    public List<Hotel> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hotel> obtener(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
