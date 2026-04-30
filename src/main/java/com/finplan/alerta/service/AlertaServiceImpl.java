package com.finplan.alerta.service;

import com.finplan.alerta.model.Alerta;
import com.finplan.alerta.model.AlertaResponse;
import com.finplan.alerta.model.ContadorAlertasResponse;
import com.finplan.alerta.repository.AlertaRepository;
import com.finplan.auth.model.Usuario;
import com.finplan.auth.repository.UsuarioRepository;
import com.finplan.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AlertaServiceImpl implements AlertaService {

    private final AlertaRepository alertaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AlertaResponse> listarAlertas(Boolean activa, String email) {
        Usuario usuario = obtenerUsuario(email);

        List<Alerta> alertas;

        if (activa != null && activa) {
            alertas = alertaRepository.findByUsuarioIdAndLeidaFalseOrderByCreatedAtDesc(usuario.getId());
        } else {
            alertas = alertaRepository.findByUsuarioIdOrderByCreatedAtDesc(usuario.getId());
        }

        return alertas.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void marcarComoLeida(Long alertaId, String email) {
        Usuario usuario = obtenerUsuario(email);

        Alerta alerta = alertaRepository.findByIdAndUsuarioId(alertaId, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Alerta", alertaId));

        alerta.setLeida(true);
        alertaRepository.save(alerta);
    }

    @Override
    @Transactional(readOnly = true)
    public ContadorAlertasResponse contarNoLeidas(String email) {
        Usuario usuario = obtenerUsuario(email);

        long count = alertaRepository.countByUsuarioIdAndLeidaFalse(usuario.getId());

        return ContadorAlertasResponse.builder()
                .noLeidas(count)
                .build();
    }

    // ── Helpers ──────────────────────────────────────────

    private AlertaResponse mapToResponse(Alerta alerta) {
        return AlertaResponse.builder()
                .id(alerta.getId())
                .tipo(alerta.getTipo().name())
                .mensaje(alerta.getMensaje())
                .leida(alerta.isLeida())
                .createdAt(alerta.getCreatedAt())
                .build();
    }

    private Usuario obtenerUsuario(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }
}

