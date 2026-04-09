package com.finplan.admin.service;

import com.finplan.admin.model.ActualizarUsuarioAdminRequest;
import com.finplan.admin.model.CrearUsuarioAdminRequest;
import com.finplan.admin.model.InfoSistemaResponse;
import com.finplan.admin.model.UsuarioAdminResponse;

import java.util.List;

public interface AdminService {

    List<UsuarioAdminResponse> listarUsuarios();

    UsuarioAdminResponse crearUsuario(CrearUsuarioAdminRequest request);

    UsuarioAdminResponse actualizarUsuario(Long id, ActualizarUsuarioAdminRequest request);

    void activarDesactivarUsuario(Long id, boolean activo);

    InfoSistemaResponse obtenerInfoSistema();
}

