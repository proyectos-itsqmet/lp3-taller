package com.lp3_taller.model;

import java.time.LocalDate;

public class Pago {

    private Long id;
    private Long reservaId;
    private double monto;
    private LocalDate fecha;
    private String metodo;

    public Pago() {
    }

    public Pago(Long id, Long reservaId, double monto, LocalDate fecha, String metodo) {
        this.id = id;
        this.reservaId = reservaId;
        this.monto = monto;
        this.fecha = fecha;
        this.metodo = metodo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReservaId() {
        return reservaId;
    }

    public void setReservaId(Long reservaId) {
        this.reservaId = reservaId;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }
}
