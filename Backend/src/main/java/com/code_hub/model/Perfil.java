package com.code_hub.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "perfil")
@Getter
@Setter
@NoArgsConstructor
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String cargo;

    @Column(length = 100)
    private String especialidad;

    @Column(length = 30)
    private String telefono;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(precision = 12, scale = 2)
    private BigDecimal salario;
}
