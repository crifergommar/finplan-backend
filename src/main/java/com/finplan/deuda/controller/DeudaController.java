package com.finplan.deuda.controller;

import com.finplan.deuda.model.*;
import com.finplan.deuda.service.DeudaService;
import com.finplan.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deudas")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Deudas", description = "Gestión de deudas con cuotas y pagos")
public class DeudaController {

    private final DeudaService deudaService;

    public DeudaController(DeudaService deudaService) {
        this.deudaService = deudaService;
    }

    @PostMapping
    @Operation(summary = "Crear nueva deuda (genera cuotas automáticamente)")
    public ResponseEntity<ApiResponse<DeudaResponse>> crear(
            @Valid @RequestBody CrearDeudaRequest request,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.status(201).body(
                ApiResponse.created(
                        deudaService.crearDeuda(request, user.getUsername())));
    }

    @GetMapping
    @Operation(summary = "Listar deudas activas del usuario")
    public ResponseEntity<ApiResponse<List<DeudaResponse>>> listar(
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        deudaService.listarDeudas(user.getUsername())));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener deuda por ID")
    public ResponseEntity<ApiResponse<DeudaResponse>> obtener(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        deudaService.obtenerDeuda(id, user.getUsername())));
    }

    @GetMapping("/{id}/cuotas")
    @Operation(summary = "Obtener cuotas de una deuda")
    public ResponseEntity<ApiResponse<List<DeudaResponse.CuotaResponse>>> obtenerCuotas(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        deudaService.obtenerCuotas(id, user.getUsername())));
    }

    @GetMapping("/calendario")
    @Operation(summary = "Obtener calendario de cuotas por mes y año")
    public ResponseEntity<ApiResponse<List<CalendarioCuotaResponse>>> calendario(
            @RequestParam(required = false) Short mes,
            @RequestParam(required = false) Short anio,
            @AuthenticationPrincipal UserDetails user) {
        // Si no se proporcionan, usar mes y año actual
        java.time.LocalDate hoy = java.time.LocalDate.now();
        if (mes == null) mes = (short) hoy.getMonthValue();
        if (anio == null) anio = (short) hoy.getYear();

        return ResponseEntity.ok(
                ApiResponse.ok(
                        deudaService.obtenerCalendario(mes, anio, user.getUsername())));
    }

    @PostMapping("/{id}/pagos")
    @Operation(summary = "Registrar pago de cuota")
    public ResponseEntity<ApiResponse<PagoResponse>> registrarPago(
            @PathVariable Long id,
            @Valid @RequestBody RegistrarPagoRequest request,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.status(201).body(
                ApiResponse.created(
                        deudaService.registrarPago(id, request, user.getUsername())));
    }

    @GetMapping("/{id}/pagos")
    @Operation(summary = "Obtener historial de pagos de una cuota")
    public ResponseEntity<ApiResponse<List<PagoResponse>>> obtenerPagos(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        deudaService.obtenerPagos(id, user.getUsername())));
    }
}



