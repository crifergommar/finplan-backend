package com.finplan.presupuesto.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CrearPresupuestoRequest {

    @NotNull(message = "El año es obligatorio")
    @Min(value = 2020, message = "El año debe ser mayor a 2020")
    @Max(value = 2100, message = "El año debe ser menor a 2100")
    private Short anio;

    private String descripcion;
}