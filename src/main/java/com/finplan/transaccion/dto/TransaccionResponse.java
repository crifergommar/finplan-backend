package com.finplan.transaccion.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class TransaccionResponse {
    private Long id;
    private Long categoriaId;
    private String categoriaNombre;
    private String categoriaTipo;
    private BigDecimal monto;
    private String tipo;
    private String descripcion;
    private LocalDate fecha;
    private String createdAt;
}

