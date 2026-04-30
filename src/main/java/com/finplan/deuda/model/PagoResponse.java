package com.finplan.deuda.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class PagoResponse {

    private Long id;
    private BigDecimal monto;

    @JsonProperty("fechaPago")
    private LocalDate fechaPago;
}

