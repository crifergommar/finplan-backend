package com.finplan.reporte.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ComparativoResponse {
    private Short anio;
    private Short mes;
    private List<CategoriaComparativoResponse> categorias;
    private BigDecimal totalPlaneado;
    private BigDecimal totalEjecutado;
    private BigDecimal diferencia;

    @Data
    @Builder
    public static class CategoriaComparativoResponse {
        private Long categoriaId;
        private String nombre;
        private String tipo;
        private BigDecimal planeado;
        private BigDecimal ejecutado;
        private BigDecimal diferencia;
        private boolean excedido;
    }
}

