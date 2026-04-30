package com.finplan.admin.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CrearUsuarioAdminRequest(
    @NotBlank(message = "nombre es requerido")
    @Size(max = 100, message = "nombre máximo 100 caracteres")
    String nombre,

    @NotBlank(message = "email es requerido")
    @Email(message = "email debe ser válido")
    String email,

    @NotBlank(message = "password es requerido")
    @Size(min = 8, message = "password mínimo 8 caracteres")
    String password,

    @NotBlank(message = "rol es requerido")
    String rol
) {}

