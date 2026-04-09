package com.finplan.reporte.controller;

import com.finplan.reporte.dto.BalanceMensualResponse;
import com.finplan.reporte.dto.ComparativoResponse;
import com.finplan.reporte.service.ReporteService;
import com.finplan.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reportes")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Reportes", description = "Reportes financieros y análisis")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/comparativo")
    @Operation(summary = "Obtener comparativo planeado vs ejecutado por mes y año")
    public ResponseEntity<ApiResponse<ComparativoResponse>> obtenerComparativo(
            @RequestParam(required = false) Short anio,
            @RequestParam(required = false) Short mes,
            @AuthenticationPrincipal UserDetails user) {
        ComparativoResponse response = reporteService.obtenerComparativo(user.getUsername(), anio, mes);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/balance-mensual")
    @Operation(summary = "Obtener balance mensual: ingresos menos gastos")
    public ResponseEntity<ApiResponse<BalanceMensualResponse>> obtenerBalanceMensual(
            @RequestParam(required = false) Short anio,
            @RequestParam(required = false) Short mes,
            @AuthenticationPrincipal UserDetails user) {
        BalanceMensualResponse response = reporteService.obtenerBalanceMensual(user.getUsername(), anio, mes);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}

