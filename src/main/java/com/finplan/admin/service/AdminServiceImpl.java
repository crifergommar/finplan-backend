package com.finplan.admin.service;

import com.finplan.admin.model.ActualizarUsuarioAdminRequest;
import com.finplan.admin.model.CrearUsuarioAdminRequest;
import com.finplan.admin.model.InfoSistemaResponse;
import com.finplan.admin.model.UsuarioAdminResponse;
import com.finplan.auth.model.Rol;
import com.finplan.auth.model.Usuario;
import com.finplan.auth.repository.UsuarioRepository;
import com.finplan.shared.exception.BusinessException;
import com.finplan.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioAdminResponse> listarUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public UsuarioAdminResponse crearUsuario(CrearUsuarioAdminRequest request) {
        // Validar que email no exista
        if (usuarioRepository.findByEmail(request.email()).isPresent()) {
            throw new BusinessException("El email ya está registrado");
        }

        // Validar rol
        Rol rol;
        try {
            rol = Rol.valueOf(request.rol().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Rol inválido. Use USUARIO o ADMIN");
        }

        // Crear usuario
        Usuario usuario = Usuario.builder()
                .nombre(request.nombre())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .rol(rol)
                .activo(true)
                .build();

        Usuario guardado = usuarioRepository.save(usuario);
        return toResponse(guardado);
    }

    @Override
    public UsuarioAdminResponse actualizarUsuario(Long id, ActualizarUsuarioAdminRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Validar que email no esté en uso por otro usuario
        usuarioRepository.findByEmail(request.email())
                .ifPresent(u -> {
                    if (!u.getId().equals(id)) {
                        throw new BusinessException("El email ya está en uso");
                    }
                });

        // Validar rol
        Rol rol;
        try {
            rol = Rol.valueOf(request.rol().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Rol inválido. Use USUARIO o ADMIN");
        }

        // Actualizar
        usuario.setNombre(request.nombre());
        usuario.setEmail(request.email());
        usuario.setRol(rol);

        Usuario actualizado = usuarioRepository.save(usuario);
        return toResponse(actualizado);
    }

    @Override
    public void activarDesactivarUsuario(Long id, boolean activo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        usuario.setActivo(activo);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public InfoSistemaResponse obtenerInfoSistema() {
        String version = "0.0.1-SNAPSHOT";
        long uptime = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        return InfoSistemaResponse.builder()
                .version(version)
                .uptime(uptime + " bytes")
                .timestamp(Instant.now().toString())
                .estado("OPERACIONAL")
                .build();
    }

    // ── Helpers ──────────────────────────────────────────

    private UsuarioAdminResponse toResponse(Usuario usuario) {
        return UsuarioAdminResponse.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol().name())
                .activo(usuario.isActivo())
                .build();
    }
}

