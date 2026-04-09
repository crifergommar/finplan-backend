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
public class CalendarioCuotaResponse {

    @JsonProperty("deudaId")
    private Long deudaId;

    private String deuda;

    @JsonProperty("cuotaId")
    private Long cuotaId;

    @JsonProperty("numeroCuota")
    private Short numeroCuota;

    private BigDecimal monto;

    @JsonProperty("fechaVcto")
    private LocalDate fechaVcto;

    private boolean pagada;
}

