package com.finplan.auth.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String accessToken;
    private String tipo;
    private String email;
    private String nombre;
    private String rol;
}