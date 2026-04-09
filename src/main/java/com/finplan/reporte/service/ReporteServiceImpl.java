package com.finplan.reporte.service;

import com.finplan.auth.model.Usuario;
import com.finplan.auth.repository.UsuarioRepository;
import com.finplan.presupuesto.model.Categoria;
import com.finplan.presupuesto.model.TipoCategoria;
import com.finplan.presupuesto.repository.CategoriaRepository;
import com.finplan.presupuesto.repository.PresupuestoMensualRepository;
import com.finplan.shared.exception.ResourceNotFoundException;
import com.finplan.transaccion.repository.TransaccionRepository;
import com.finplan.reporte.dto.BalanceMensualResponse;
import com.finplan.reporte.dto.ComparativoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteServiceImpl implements ReporteService {

    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final PresupuestoMensualRepository presupuestoMensualRepository;
    private final TransaccionRepository transaccionRepository;

    @Override
    public ComparativoResponse obtenerComparativo(String email, Short anio, Short mes) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Si no se proporcionan mes/año, usar el actual
        LocalDate hoy = LocalDate.now();
        if (anio == null) anio = (short) hoy.getYear();
        if (mes == null) mes = (short) hoy.getMonthValue();

        // Obtener todas las categorías activas del usuario
        List<Categoria> categorias = categoriaRepository.findByUsuarioIdAndActivaTrue(usuario.getId());

        List<ComparativoResponse.CategoriaComparativoResponse> categoriasComparativo = new ArrayList<>();
        BigDecimal totalPlaneado = BigDecimal.ZERO;
        BigDecimal totalEjecutado = BigDecimal.ZERO;

        for (Categoria categoria : categorias) {
            // Obtener monto planeado
            BigDecimal montoPlaneado = presupuestoMensualRepository
                    .findMontoPlaneado(usuario.getId(), categoria.getId(), anio, mes)
                    .orElse(BigDecimal.ZERO);

            // Obtener monto ejecutado
            BigDecimal montoEjecutado = transaccionRepository
                    .sumMontoByUsuarioCategoriaAndMesAndAnio(usuario, categoria, mes, anio);
            if (montoEjecutado == null) {
                montoEjecutado = BigDecimal.ZERO;
            }

            // Calcular diferencia
            BigDecimal diferencia = montoEjecutado.subtract(montoPlaneado);
            boolean excedido = diferencia.compareTo(BigDecimal.ZERO) > 0;

            ComparativoResponse.CategoriaComparativoResponse categoriaComparativo =
                ComparativoResponse.CategoriaComparativoResponse.builder()
                    .categoriaId(categoria.getId())
                    .nombre(categoria.getNombre())
                    .tipo(categoria.getTipo().name())
                    .planeado(montoPlaneado)
                    .ejecutado(montoEjecutado)
                    .diferencia(diferencia)
                    .excedido(excedido)
                    .build();

            categoriasComparativo.add(categoriaComparativo);

            totalPlaneado = totalPlaneado.add(montoPlaneado);
            totalEjecutado = totalEjecutado.add(montoEjecutado);
        }

        BigDecimal diferenciaTotalComparativo = totalEjecutado.subtract(totalPlaneado);

        return ComparativoResponse.builder()
                .anio(anio)
                .mes(mes)
                .categorias(categoriasComparativo)
                .totalPlaneado(totalPlaneado)
                .totalEjecutado(totalEjecutado)
                .diferencia(diferenciaTotalComparativo)
                .build();
    }

    @Override
    public BalanceMensualResponse obtenerBalanceMensual(String email, Short anio, Short mes) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Si no se proporcionan mes/año, usar el actual
        LocalDate hoy = LocalDate.now();
        if (anio == null) anio = (short) hoy.getYear();
        if (mes == null) mes = (short) hoy.getMonthValue();

        // Obtener todas las transacciones del mes
        List<Categoria> categoriasIngresos = categoriaRepository.findByUsuarioIdAndActivaTrue(usuario.getId())
                .stream()
                .filter(c -> c.getTipo() == TipoCategoria.INGRESO)
                .toList();

        List<Categoria> categoriasGastos = categoriaRepository.findByUsuarioIdAndActivaTrue(usuario.getId())
                .stream()
                .filter(c -> c.getTipo() == TipoCategoria.GASTO)
                .toList();

        BigDecimal totalIngresos = BigDecimal.ZERO;
        BigDecimal totalGastos = BigDecimal.ZERO;

        // Sumar ingresos
        for (Categoria categoria : categoriasIngresos) {
            BigDecimal monto = transaccionRepository
                    .sumMontoByUsuarioCategoriaAndMesAndAnio(usuario, categoria, mes, anio);
            if (monto != null) {
                totalIngresos = totalIngresos.add(monto);
            }
        }

        // Sumar gastos
        for (Categoria categoria : categoriasGastos) {
            BigDecimal monto = transaccionRepository
                    .sumMontoByUsuarioCategoriaAndMesAndAnio(usuario, categoria, mes, anio);
            if (monto != null) {
                totalGastos = totalGastos.add(monto);
            }
        }

        BigDecimal balance = totalIngresos.subtract(totalGastos);

        return BalanceMensualResponse.builder()
                .anio(anio)
                .mes(mes)
                .totalIngresos(totalIngresos)
                .totalGastos(totalGastos)
                .balance(balance)
                .build();
    }
}



