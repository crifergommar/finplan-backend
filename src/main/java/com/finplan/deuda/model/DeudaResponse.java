package com.finplan.deuda.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeudaResponse {

    private Long id;
    private String nombre;

    @JsonProperty("montoTotal")
    private BigDecimal montoTotal;

    @JsonProperty("numCuotas")
    private Short numCuotas;

    @JsonProperty("tasaInteres")
    private BigDecimal tasaInteres;

    @JsonProperty("fechaInicio")
    private LocalDate fechaInicio;

    private boolean activa;

    @JsonProperty("cuotas")
    private List<CuotaResponse> cuotas;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CuotaResponse {
        private Long id;
        private Short numero;
        private BigDecimal monto;

        @JsonProperty("fechaVcto")
        private LocalDate fechaVcto;

        private boolean pagada;

        @JsonProperty("pagos")
        private List<PagoResponse> pagos;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PagoResponse {
        private Long id;
        private BigDecimal monto;

        @JsonProperty("fechaPago")
        private LocalDate fechaPago;
    }
}

