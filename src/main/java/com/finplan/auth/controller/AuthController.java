package com.finplan.auth.controller;

import com.finplan.auth.model.*;
import com.finplan.auth.service.AuthService;
import com.finplan.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Endpoints de login y registro")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registro")
    public ResponseEntity<ApiResponse<AuthResponse>> registro(
            @Valid @RequestBody RegistroRequest request,
            HttpServletResponse response) {
        AuthResponse auth = authService.registro(request, response);
        return ResponseEntity.status(201).body(ApiResponse.created(auth));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        AuthResponse auth = authService.login(request, response);
        return ResponseEntity.ok(ApiResponse.ok(auth));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {
        AuthResponse auth = authService.refresh(refreshToken, response);
        return ResponseEntity.ok(ApiResponse.ok(auth));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {
        authService.logout(refreshToken, response);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<UsuarioPerfilResponse>> me(
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            ApiResponse<UsuarioPerfilResponse> response = new ApiResponse<>();
            response.setMensaje("Sesión inválida o expirada");
            response.setStatus(401);
            return ResponseEntity.status(401).body(response);
        }

        UsuarioPerfilResponse perfil = authService.obtenerPerfil(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(perfil));
    }
}