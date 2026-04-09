package com.finplan.alerta.controller;

import com.finplan.alerta.model.AlertaResponse;
import com.finplan.alerta.model.ContadorAlertasResponse;
import com.finplan.alerta.service.AlertaService;
import com.finplan.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alertas")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Alertas")
public class AlertaController {

    private final AlertaService alertaService;

    public AlertaController(AlertaService alertaService) {
        this.alertaService = alertaService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AlertaResponse>>> listar(
            @RequestParam(required = false) Boolean activa,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        alertaService.listarAlertas(activa, user.getUsername())));
    }

    @PatchMapping("/{id}/leer")
    public ResponseEntity<ApiResponse<Void>> marcarComoLeida(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user) {
        alertaService.marcarComoLeida(id, user.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @GetMapping("/contador")
    public ResponseEntity<ApiResponse<ContadorAlertasResponse>> contador(
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        alertaService.contarNoLeidas(user.getUsername())));
    }
}

