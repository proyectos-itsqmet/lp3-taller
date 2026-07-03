package com.code_hub.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.code_hub.service.ReporteService;

@RestController
@RequestMapping("/api/reportes")
@PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/proyectos")
    public ResponseEntity<byte[]> proyectos() {
        byte[] pdf = reporteService.generarReporteProyectos();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"reporte-proyectos.pdf\"")
                .body(pdf);
    }

    @GetMapping("/empleados")
    public ResponseEntity<byte[]> empleados() {
        byte[] pdf = reporteService.generarReporteEmpleados();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"reporte-empleados.pdf\"")
                .body(pdf);
    }

}
