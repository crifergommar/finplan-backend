package com.finplan.transaccion.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ResumenTransaccionesResponse {
    private Short mes;
    private Short anio;
    private List<ResumenPorCategoria> categorias;
    private BigDecimal totalIngresos;
    private BigDecimal totalGastos;
    private BigDecimal balance;

    @Data
    @Builder
    public static class ResumenPorCategoria {
        private Long categoriaId;
        private String categoriaNombre;
        private String categoriaTipo;
        private BigDecimal total;
    }
}

