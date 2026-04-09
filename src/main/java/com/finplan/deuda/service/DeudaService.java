package com.finplan.deuda.service;

import com.finplan.deuda.model.*;

import java.util.List;

public interface DeudaService {

    DeudaResponse crearDeuda(CrearDeudaRequest request, String email);

    List<DeudaResponse> listarDeudas(String email);

    DeudaResponse obtenerDeuda(Long deudaId, String email);

    List<DeudaResponse.CuotaResponse> obtenerCuotas(Long deudaId, String email);

    List<CalendarioCuotaResponse> obtenerCalendario(
            Short mes, Short anio, String email);

    PagoResponse registrarPago(Long cuotaId, RegistrarPagoRequest request, String email);

    List<PagoResponse> obtenerPagos(Long cuotaId, String email);
}

