package com.finplan.transaccion.service;

import com.finplan.auth.model.Usuario;
import com.finplan.auth.repository.UsuarioRepository;
import com.finplan.presupuesto.model.Categoria;
import com.finplan.presupuesto.model.TipoCategoria;
import com.finplan.presupuesto.repository.CategoriaRepository;
import com.finplan.shared.exception.BusinessException;
import com.finplan.shared.exception.ResourceNotFoundException;
import com.finplan.transaccion.dto.ResumenTransaccionesResponse;
import com.finplan.transaccion.dto.TransaccionRequest;
import com.finplan.transaccion.dto.TransaccionResponse;
import com.finplan.transaccion.model.Transaccion;
import com.finplan.transaccion.repository.TransaccionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TransaccionServiceImpl implements TransaccionService {

    private final TransaccionRepository transaccionRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;

    @Override
    public TransaccionResponse registrar(TransaccionRequest request, String email) {
        // Obtener usuario del contexto
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Validar monto > 0
        if (request.monto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El monto debe ser mayor a 0");
        }

        // Obtener y validar categoría
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", request.categoriaId()));

        // Validar que la categoría pertenece al usuario
        if (!categoria.getUsuario().getId().equals(usuario.getId())) {
            throw new BusinessException("La categoría no pertenece al usuario autenticado");
        }

        // Validar que la categoría esté activa
        if (!categoria.isActiva()) {
            throw new BusinessException("La categoría está desactivada");
        }

        // Validar tipo
        TipoCategoria tipo;
        try {
            tipo = TipoCategoria.valueOf(request.tipo().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Tipo inválido. Use INGRESO o GASTO");
        }

        // Crear y guardar transacción
        Transaccion transaccion = Transaccion.builder()
                .usuario(usuario)
                .categoria(categoria)
                .monto(request.monto())
                .tipo(tipo)
                .descripcion(request.descripcion())
                .fecha(request.fecha())
                .build();

        Transaccion guardada = transaccionRepository.save(transaccion);
        return mapToResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransaccionResponse> listar(String email, Short mes, Short anio, String tipo) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        List<Transaccion> transacciones;

        if (mes != null && anio != null && tipo != null) {
            TipoCategoria tipoCategoria = TipoCategoria.valueOf(tipo.toUpperCase());
            transacciones = transaccionRepository.findByUsuarioMesAnioTipo(usuario, mes, anio, tipoCategoria);
        } else if (mes != null && anio != null) {
            transacciones = transaccionRepository.findByUsuarioAndMesAndAnio(usuario, mes, anio);
        } else if (tipo != null) {
            TipoCategoria tipoCategoria = TipoCategoria.valueOf(tipo.toUpperCase());
            transacciones = transaccionRepository.findByUsuarioAndTipo(usuario, tipoCategoria);
        } else {
            transacciones = transaccionRepository.findByUsuario(usuario);
        }

        return transacciones.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ResumenTransaccionesResponse obtenerResumen(String email, Short mes, Short anio) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        List<Object[]> resultados = transaccionRepository.sumByCategoriaMesAnio(usuario, mes, anio);

        List<ResumenTransaccionesResponse.ResumenPorCategoria> categorias = new ArrayList<>();
        BigDecimal totalIngresos = BigDecimal.ZERO;
        BigDecimal totalGastos = BigDecimal.ZERO;

        for (Object[] resultado : resultados) {
            Categoria categoria = (Categoria) resultado[0];
            BigDecimal total = (BigDecimal) resultado[1];

            ResumenTransaccionesResponse.ResumenPorCategoria resumen =
                ResumenTransaccionesResponse.ResumenPorCategoria.builder()
                    .categoriaId(categoria.getId())
                    .categoriaNombre(categoria.getNombre())
                    .categoriaTipo(categoria.getTipo().name())
                    .total(total)
                    .build();

            categorias.add(resumen);

            if (categoria.getTipo() == TipoCategoria.INGRESO) {
                totalIngresos = totalIngresos.add(total);
            } else {
                totalGastos = totalGastos.add(total);
            }
        }

        BigDecimal balance = totalIngresos.subtract(totalGastos);

        return ResumenTransaccionesResponse.builder()
                .mes(mes)
                .anio(anio)
                .categorias(categorias)
                .totalIngresos(totalIngresos)
                .totalGastos(totalGastos)
                .balance(balance)
                .build();
    }

    @Override
    public void eliminar(Long id, String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Transaccion transaccion = transaccionRepository.findByIdAndUsuario(id, usuario)
                .orElseThrow(() -> new ResourceNotFoundException("Transacción", id));

        transaccionRepository.delete(transaccion);
    }

    private TransaccionResponse mapToResponse(Transaccion transaccion) {
        return TransaccionResponse.builder()
                .id(transaccion.getId())
                .categoriaId(transaccion.getCategoria().getId())
                .categoriaNombre(transaccion.getCategoria().getNombre())
                .categoriaTipo(transaccion.getCategoria().getTipo().name())
                .monto(transaccion.getMonto())
                .tipo(transaccion.getTipo().name())
                .descripcion(transaccion.getDescripcion())
                .fecha(transaccion.getFecha())
                .createdAt(transaccion.getCreatedAt().toString())
                .build();
    }
}

