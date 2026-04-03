package com.finplan.presupuesto.controller;

import com.finplan.presupuesto.model.*;
import com.finplan.presupuesto.service.PresupuestoService;
import com.finplan.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Presupuesto")
public class PresupuestoController {

    private final PresupuestoService presupuestoService;

    public PresupuestoController(PresupuestoService presupuestoService) {
        this.presupuestoService = presupuestoService;
    }

    @PostMapping("/presupuestos")
    public ResponseEntity<ApiResponse<PresupuestoResponse>> crear(
            @Valid @RequestBody CrearPresupuestoRequest request,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.status(201).body(
                ApiResponse.created(
                        presupuestoService.crearPresupuesto(
                                request, user.getUsername())));
    }

    @GetMapping("/presupuestos/{anio}")
    public ResponseEntity<ApiResponse<PresupuestoResponse>> get(
            @PathVariable Short anio,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        presupuestoService.getPresupuesto(
                                anio, user.getUsername())));
    }

    @PutMapping("/presupuestos/mensual/{id}")
    public ResponseEntity<ApiResponse<PresupuestoResponse.MensualResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarMensualRequest request,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        presupuestoService.actualizarMensual(
                                id, request, user.getUsername())));
    }

    @GetMapping("/categorias")
    public ResponseEntity<ApiResponse<List<CategoriaResponse>>> listarCategorias(
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        presupuestoService.getCategorias(user.getUsername())));
    }

    @PostMapping("/categorias")
    public ResponseEntity<ApiResponse<CategoriaResponse>> crearCategoria(
            @Valid @RequestBody CrearCategoriaRequest request,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.status(201).body(
                ApiResponse.created(
                        presupuestoService.crearCategoria(
                                request, user.getUsername())));
    }

    @PatchMapping("/categorias/{id}/desactivar")
    public ResponseEntity<ApiResponse<Void>> desactivar(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user) {
        presupuestoService.desactivarCategoria(id, user.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}