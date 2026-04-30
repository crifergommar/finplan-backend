package com.finplan.deuda.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CrearDeudaRequest {

    @NotBlank(message = "El nombre de la deuda es requerido")
    private String nombre;

    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal montoTotal;

    @Min(value = 1, message = "El número de cuotas debe ser mayor a 0")
    private Short numCuotas;

    @JsonProperty("tasaInteres")
    private BigDecimal tasaInteres;

    @NotBlank(message = "La fecha de inicio es requerida")
    private LocalDate fechaInicio;
}

