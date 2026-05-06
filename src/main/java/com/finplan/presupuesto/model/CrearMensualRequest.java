package com.finplan.presupuesto.model;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CrearMensualRequest {

    @NotNull(message = "El id del presupuesto es obligatorio")
    private Long presupuestoId;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;

    @NotNull(message = "El mes es obligatorio")
    @Min(value = 1, message = "El mes debe ser entre 1 y 12")
    @Max(value = 12, message = "El mes debe ser entre 1 y 12")
    private Short mes;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "El monto debe ser mayor a cero")
    private BigDecimal montoPlaneado;
}
