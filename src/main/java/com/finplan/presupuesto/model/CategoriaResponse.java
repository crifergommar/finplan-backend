package com.finplan.presupuesto.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoriaResponse {
    private Long id;
    private String nombre;
    private String tipo;
    private boolean activa;
}