package com.finplan.admin.controller;

import com.finplan.admin.model.ActualizarUsuarioAdminRequest;
import com.finplan.admin.model.CrearUsuarioAdminRequest;
import com.finplan.admin.model.InfoSistemaResponse;
import com.finplan.admin.model.UsuarioAdminResponse;
import com.finplan.admin.service.AdminService;
import com.finplan.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin", description = "Administración del sistema (solo ADMIN)")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/usuarios")
    @Operation(summary = "Listar todos los usuarios")
    public ResponseEntity<ApiResponse<List<UsuarioAdminResponse>>> listarUsuarios() {
        return ResponseEntity.ok(
                ApiResponse.ok(adminService.listarUsuarios())
        );
    }

    @PostMapping("/usuarios")
    @Operation(summary = "Crear nuevo usuario")
    public ResponseEntity<ApiResponse<UsuarioAdminResponse>> crearUsuario(
            @Valid @RequestBody CrearUsuarioAdminRequest request) {
        return ResponseEntity.status(201).body(
                ApiResponse.created(adminService.crearUsuario(request))
        );
    }

    @PutMapping("/usuarios/{id}")
    @Operation(summary = "Actualizar datos de usuario")
    public ResponseEntity<ApiResponse<UsuarioAdminResponse>> actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarUsuarioAdminRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok(adminService.actualizarUsuario(id, request))
        );
    }

    @PatchMapping("/usuarios/{id}")
    @Operation(summary = "Activar o desactivar usuario")
    public ResponseEntity<ApiResponse<Void>> activarDesactivarUsuario(
            @PathVariable Long id,
            @RequestParam boolean activo) {
        adminService.activarDesactivarUsuario(id, activo);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @GetMapping("/sistema/info")
    @Operation(summary = "Información del sistema")
    public ResponseEntity<ApiResponse<InfoSistemaResponse>> obtenerInfoSistema() {
        return ResponseEntity.ok(
                ApiResponse.ok(adminService.obtenerInfoSistema())
        );
    }
}

