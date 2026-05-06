package com.finplan.presupuesto.service;

import com.finplan.auth.model.Usuario;
import com.finplan.auth.repository.UsuarioRepository;
import com.finplan.presupuesto.model.*;
import com.finplan.presupuesto.repository.*;
import com.finplan.shared.exception.BusinessException;
import com.finplan.shared.exception.ResourceNotFoundException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PresupuestoService {

    private final PresupuestoRepository presupuestoRepository;
    private final PresupuestoMensualRepository mensualRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;

    // ── Presupuesto ──────────────────────────────────────
    public PresupuestoResponse crearPresupuesto(
            CrearPresupuestoRequest request, String email) {

        Usuario usuario = obtenerUsuario(email);

        if (presupuestoRepository.existsByUsuarioIdAndAnio(
                usuario.getId(), request.getAnio())) {
            throw new BusinessException(
                    "Ya existe un presupuesto para el año "
                    + request.getAnio());
        }

        Presupuesto presupuesto = Presupuesto.builder()
                .usuario(usuario)
                .anio(request.getAnio())
                .descripcion(request.getDescripcion())
                .build();

        Presupuesto guardado = presupuestoRepository.save(presupuesto);

        // Auto-sembrar entradas mensuales para todas las categorías activas × 12 meses
        List<Categoria> categorias = categoriaRepository.findByUsuarioIdAndActivaTrue(usuario.getId());
        List<PresupuestoMensual> mensuales = new ArrayList<>();
        for (Categoria categoria : categorias) {
            for (short mes = 1; mes <= 12; mes++) {
                mensuales.add(PresupuestoMensual.builder()
                        .presupuesto(guardado)
                        .categoria(categoria)
                        .mes(mes)
                        .montoPlaneado(java.math.BigDecimal.ZERO)
                        .build());
            }
        }
        mensualRepository.saveAll(mensuales);

        return mapToResponse(presupuestoRepository.findByUsuarioIdAndAnio(
                usuario.getId(), request.getAnio()).orElse(guardado));
    }

    public PresupuestoResponse.MensualResponse crearMensual(
            CrearMensualRequest request, String email) {

        Usuario usuario = obtenerUsuario(email);

        Presupuesto presupuesto = presupuestoRepository
                .findById(request.getPresupuestoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                "Presupuesto", request.getPresupuestoId()));

        if (!presupuesto.getUsuario().getId().equals(usuario.getId())) {
            throw new BusinessException("No tiene permisos para modificar este presupuesto");
        }

        Categoria categoria = categoriaRepository
                .findByIdAndUsuarioId(request.getCategoriaId(), usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                "Categoría", request.getCategoriaId()));

        // Upsert: si ya existe la entrada, actualizarla; si no, crearla
        PresupuestoMensual mensual = mensualRepository
                .findByUsuarioCategoriaMes(usuario.getId(), categoria.getId(),
                        presupuesto.getAnio(), request.getMes())
                .orElse(PresupuestoMensual.builder()
                        .presupuesto(presupuesto)
                        .categoria(categoria)
                        .mes(request.getMes())
                        .build());

        mensual.setMontoPlaneado(request.getMontoPlaneado());
        return mapMensualToResponse(mensualRepository.save(mensual));
    }

    @Transactional(readOnly = true)
    public PresupuestoResponse getPresupuesto(
            Short anio, String email) {

        Usuario usuario = obtenerUsuario(email);

        Presupuesto presupuesto = presupuestoRepository
                .findByUsuarioIdAndAnio(usuario.getId(), anio)
                .orElseThrow(() -> new ResourceNotFoundException(
                "Presupuesto no encontrado para el año " + anio));

        return mapToResponse(presupuesto);
    }

    public PresupuestoResponse.MensualResponse actualizarMensual(
            Long mensualId,
            ActualizarMensualRequest request,
            String email) {

        Usuario usuario = obtenerUsuario(email);

        PresupuestoMensual mensual = mensualRepository
                .findById(mensualId)
                .orElseThrow(() -> new ResourceNotFoundException(
                "Detalle mensual", mensualId));

        if (!mensual.getPresupuesto().getUsuario()
                .getId().equals(usuario.getId())) {
            throw new BusinessException(
                    "No tiene permisos para modificar este presupuesto");
        }

        mensual.setMontoPlaneado(request.getMontoPlaneado());
        return mapMensualToResponse(mensualRepository.save(mensual));
    }

    // ── Categorías ───────────────────────────────────────
    @Transactional(readOnly = true)
    public List<CategoriaResponse> getCategorias(String email) {
        Usuario usuario = obtenerUsuario(email);
        return categoriaRepository
                .findByUsuarioIdAndActivaTrue(usuario.getId())
                .stream()
                .map(this::mapCategoriaToResponse)
                .toList();
    }

    public CategoriaResponse crearCategoria(
            CrearCategoriaRequest request, String email) {

        Usuario usuario = obtenerUsuario(email);

        Categoria categoria = Categoria.builder()
                .usuario(usuario)
                .nombre(request.getNombre())
                .tipo(request.getTipo())
                .activa(true)
                .build();

        return mapCategoriaToResponse(
                categoriaRepository.save(categoria));
    }

    public void desactivarCategoria(Long id, String email) {
        Usuario usuario = obtenerUsuario(email);

        Categoria categoria = categoriaRepository
                .findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                "Categoría", id));

        categoria.setActiva(false);
        categoriaRepository.save(categoria);
    }

    // ── Helpers ──────────────────────────────────────────
    private Usuario obtenerUsuario(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                "Usuario no encontrado"));
    }

    private PresupuestoResponse mapToResponse(Presupuesto p) {
        return PresupuestoResponse.builder()
                .id(p.getId())
                .anio(p.getAnio())
                .descripcion(p.getDescripcion())
                .meses(p.getMeses().stream()
                        .map(this::mapMensualToResponse)
                        .toList())
                .build();
    }

    private PresupuestoResponse.MensualResponse mapMensualToResponse(
            PresupuestoMensual pm) {
        return PresupuestoResponse.MensualResponse.builder()
                .id(pm.getId())
                .mes(pm.getMes())
                .categoriaId(pm.getCategoria().getId())
                .categoriaNombre(pm.getCategoria().getNombre())
                .categoriaTipo(pm.getCategoria().getTipo().name())
                .montoPlaneado(pm.getMontoPlaneado())
                .build();
    }

    private CategoriaResponse mapCategoriaToResponse(Categoria c) {
        return CategoriaResponse.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .tipo(c.getTipo().name())
                .activa(c.isActiva())
                .build();
    }
}
