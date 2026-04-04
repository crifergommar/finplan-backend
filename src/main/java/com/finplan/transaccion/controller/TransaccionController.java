package com.finplan.transaccion.controller;

import com.finplan.shared.dto.ApiResponse;
import com.finplan.transaccion.dto.ResumenTransaccionesResponse;
import com.finplan.transaccion.dto.TransaccionRequest;
import com.finplan.transaccion.dto.TransaccionResponse;
import com.finplan.transaccion.service.TransaccionService;
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
@RequestMapping("/api/transacciones")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Transacciones", description = "Gestión de transacciones (ingresos y gastos)")
public class TransaccionController {

    private final TransaccionService transaccionService;

    public TransaccionController(TransaccionService transaccionService) {
        this.transaccionService = transaccionService;
    }

    @PostMapping
    @Operation(summary = "Registrar nueva transacción")
    public ResponseEntity<ApiResponse<TransaccionResponse>> registrar(
            @Valid @RequestBody TransaccionRequest request,
            @AuthenticationPrincipal UserDetails user) {
        TransaccionResponse response = transaccionService.registrar(request, user.getUsername());
        return ResponseEntity.status(201).body(ApiResponse.created(response));
    }

    @GetMapping
    @Operation(summary = "Listar transacciones con filtros opcionales")
    public ResponseEntity<ApiResponse<List<TransaccionResponse>>> listar(
            @RequestParam(required = false) Short mes,
            @RequestParam(required = false) Short anio,
            @RequestParam(required = false) String tipo,
            @AuthenticationPrincipal UserDetails user) {
        List<TransaccionResponse> response = transaccionService.listar(user.getUsername(), mes, anio, tipo);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/resumen")
    @Operation(summary = "Obtener resumen de transacciones por categoría")
    public ResponseEntity<ApiResponse<ResumenTransaccionesResponse>> obtenerResumen(
            @RequestParam(required = false) Short mes,
            @RequestParam(required = false) Short anio,
            @AuthenticationPrincipal UserDetails user) {
        ResumenTransaccionesResponse response = transaccionService.obtenerResumen(user.getUsername(), mes, anio);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar transacción")
    public ResponseEntity<ApiResponse<Void>> eliminar(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user) {
        transaccionService.eliminar(id, user.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}

