package com.finplan.presupuesto.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ActualizarMensualRequest {

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "El monto debe ser mayor a cero")
    private BigDecimal montoPlaneado;
}