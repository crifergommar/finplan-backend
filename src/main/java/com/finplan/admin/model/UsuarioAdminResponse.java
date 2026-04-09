package com.finplan.admin.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioAdminResponse {

    private Long id;
    private String nombre;
    private String email;
    private String rol;
    private boolean activo;
}

