package com.finplan.transaccion.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TransaccionRequest(
    @NotNull(message = "categoriaId es requerido")
    Long categoriaId,

    @NotNull(message = "monto es requerido")
    @DecimalMin(value = "0.01", message = "monto debe ser mayor a 0")
    BigDecimal monto,

    @NotNull(message = "tipo es requerido")
    String tipo,

    @Size(max = 255, message = "descripción máximo 255 caracteres")
    String descripcion,

    @NotNull(message = "fecha es requerida")
    @PastOrPresent(message = "fecha no puede ser futura")
    LocalDate fecha
) {}


