package com.finplan.deuda.service;

import com.finplan.auth.model.Usuario;
import com.finplan.auth.repository.UsuarioRepository;
import com.finplan.deuda.model.*;
import com.finplan.deuda.repository.CuotaDeudaRepository;
import com.finplan.deuda.repository.DeudaRepository;
import com.finplan.deuda.repository.PagoDeudaRepository;
import com.finplan.shared.exception.BusinessException;
import com.finplan.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DeudaServiceImpl implements DeudaService {

    private final DeudaRepository deudaRepository;
    private final CuotaDeudaRepository cuotaRepository;
    private final PagoDeudaRepository pagoRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public DeudaResponse crearDeuda(CrearDeudaRequest request, String email) {
        Usuario usuario = obtenerUsuario(email);

        if (request.getMontoTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El monto debe ser mayor a 0");
        }

        if (request.getNumCuotas() <= 0) {
            throw new BusinessException("El número de cuotas debe ser mayor a 0");
        }

        if (request.getFechaInicio().isAfter(LocalDate.now())) {
            throw new BusinessException("La fecha de inicio no puede ser futura");
        }

        Deuda deuda = Deuda.builder()
                .usuario(usuario)
                .nombre(request.getNombre())
                .montoTotal(request.getMontoTotal())
                .numCuotas(request.getNumCuotas())
                .tasaInteres(request.getTasaInteres() != null ?
                        request.getTasaInteres() : BigDecimal.ZERO)
                .fechaInicio(request.getFechaInicio())
                .activa(true)
                .build();

        deuda = deudaRepository.save(deuda);
        generarCuotas(deuda);

        return mapToResponse(deuda);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeudaResponse> listarDeudas(String email) {
        Usuario usuario = obtenerUsuario(email);
        return deudaRepository.findByUsuarioIdAndActivaTrue(usuario.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DeudaResponse obtenerDeuda(Long deudaId, String email) {
        Usuario usuario = obtenerUsuario(email);
        Deuda deuda = deudaRepository.findByIdAndUsuarioId(deudaId, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Deuda", deudaId));
        return mapToResponse(deuda);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeudaResponse.CuotaResponse> obtenerCuotas(Long deudaId, String email) {
        Usuario usuario = obtenerUsuario(email);
        deudaRepository.findByIdAndUsuarioId(deudaId, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Deuda", deudaId));

        return cuotaRepository.findByDeudaId(deudaId)
                .stream()
                .map(this::mapCuotaToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalendarioCuotaResponse> obtenerCalendario(
            Short mes, Short anio, String email) {
        Usuario usuario = obtenerUsuario(email);

        if (mes < 1 || mes > 12) {
            throw new BusinessException("Mes inválido (1-12)");
        }

        List<CuotaDeuda> cuotas = cuotaRepository
                .findByUsuarioIdAndMesAnio(usuario.getId(), anio, mes);

        return cuotas.stream()
                .map(c -> CalendarioCuotaResponse.builder()
                        .deudaId(c.getDeuda().getId())
                        .deuda(c.getDeuda().getNombre())
                        .cuotaId(c.getId())
                        .numeroCuota(c.getNumero())
                        .monto(c.getMonto())
                        .fechaVcto(c.getFechaVcto())
                        .pagada(c.isPagada())
                        .build())
                .toList();
    }

    @Override
    public PagoResponse registrarPago(Long cuotaId, RegistrarPagoRequest request,
                                      String email) {
        Usuario usuario = obtenerUsuario(email);

        CuotaDeuda cuota = cuotaRepository.findByIdAndDeudaUsuarioId(
                        cuotaId, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cuota", cuotaId));

        if (cuota.isPagada()) {
            throw new BusinessException("La cuota ya ha sido pagada");
        }

        if (request.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El monto debe ser mayor a 0");
        }

        LocalDate fechaPago = LocalDate.parse(request.getFechaPago());

        if (fechaPago.isAfter(LocalDate.now())) {
            throw new BusinessException("La fecha de pago no puede ser futura");
        }

        PagoDeuda pago = PagoDeuda.builder()
                .cuota(cuota)
                .monto(request.getMonto())
                .fechaPago(fechaPago)
                .build();

        pago = pagoRepository.save(pago);

        // Marcar cuota como pagada si se pagó completamente
        BigDecimal totalPagado = pagoRepository.findByCuotaId(cuotaId)
                .stream()
                .map(PagoDeuda::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPagado.compareTo(cuota.getMonto()) >= 0) {
            cuota.setPagada(true);
            cuotaRepository.save(cuota);
        }

        return mapPagoToResponse(pago);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoResponse> obtenerPagos(Long cuotaId, String email) {
        Usuario usuario = obtenerUsuario(email);

        CuotaDeuda cuota = cuotaRepository.findByIdAndDeudaUsuarioId(
                        cuotaId, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cuota", cuotaId));

        return pagoRepository.findByCuotaId(cuotaId)
                .stream()
                .map(this::mapPagoToResponse)
                .toList();
    }

    // ── Helpers ──────────────────────────────────────────

    private void generarCuotas(Deuda deuda) {
        BigDecimal montoCuota = deuda.getMontoTotal()
                .divide(new BigDecimal(deuda.getNumCuotas()), 2, RoundingMode.HALF_UP);

        LocalDate fechaActual = deuda.getFechaInicio();

        for (short i = 1; i <= deuda.getNumCuotas(); i++) {
            CuotaDeuda cuota = CuotaDeuda.builder()
                    .deuda(deuda)
                    .numero(i)
                    .monto(montoCuota)
                    .fechaVcto(fechaActual.plusMonths(i))
                    .pagada(false)
                    .build();

            cuotaRepository.save(cuota);
        }
    }

    private DeudaResponse mapToResponse(Deuda deuda) {
        return DeudaResponse.builder()
                .id(deuda.getId())
                .nombre(deuda.getNombre())
                .montoTotal(deuda.getMontoTotal())
                .numCuotas(deuda.getNumCuotas())
                .tasaInteres(deuda.getTasaInteres())
                .fechaInicio(deuda.getFechaInicio())
                .activa(deuda.isActiva())
                .cuotas(deuda.getCuotas() != null ?
                        deuda.getCuotas().stream()
                                .map(this::mapCuotaToResponse)
                                .toList() : null)
                .build();
    }

    private DeudaResponse.CuotaResponse mapCuotaToResponse(CuotaDeuda cuota) {
        return DeudaResponse.CuotaResponse.builder()
                .id(cuota.getId())
                .numero(cuota.getNumero())
                .monto(cuota.getMonto())
                .fechaVcto(cuota.getFechaVcto())
                .pagada(cuota.isPagada())
                .pagos(cuota.getPagos() != null ?
                        cuota.getPagos().stream()
                                .map(this::mapDeudaPagoToResponse)
                                .toList() : null)
                .build();
    }

    private DeudaResponse.PagoResponse mapDeudaPagoToResponse(PagoDeuda pago) {
        return DeudaResponse.PagoResponse.builder()
                .id(pago.getId())
                .monto(pago.getMonto())
                .fechaPago(pago.getFechaPago())
                .build();
    }

    private Usuario obtenerUsuario(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    private PagoResponse mapPagoToResponse(PagoDeuda pago) {
        return PagoResponse.builder()
                .id(pago.getId())
                .monto(pago.getMonto())
                .fechaPago(pago.getFechaPago())
                .build();
    }
}





