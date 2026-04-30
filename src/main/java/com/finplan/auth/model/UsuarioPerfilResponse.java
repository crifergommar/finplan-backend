package com.finplan.auth.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioPerfilResponse {
    private Long id;
    private String nombre;
    private String email;
    private String rol;
}