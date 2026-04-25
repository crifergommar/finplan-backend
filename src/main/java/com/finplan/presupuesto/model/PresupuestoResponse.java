package com.finplan.presupuesto.model;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class PresupuestoResponse {
    private Long id;
    private Short anio;
    private String descripcion;
    private List<MensualResponse> meses;

    @Data
    @Builder
    public static class MensualResponse {
        private Long id;
        private Short mes;
        private Long categoriaId;
        private String categoriaNombre;
        private String categoriaTipo;
        private BigDecimal montoPlaneado;
    }
}