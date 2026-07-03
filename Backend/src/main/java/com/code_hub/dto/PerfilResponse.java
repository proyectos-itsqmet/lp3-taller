package com.code_hub.dto;

import java.math.BigDecimal;

public record PerfilResponse(
        Long id,
        String cargo,
        String especialidad,
        String telefono,
        String bio,
        BigDecimal salario
) {
}
