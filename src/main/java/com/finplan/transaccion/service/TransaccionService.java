package com.finplan.transaccion.service;

import com.finplan.transaccion.dto.ResumenTransaccionesResponse;
import com.finplan.transaccion.dto.TransaccionRequest;
import com.finplan.transaccion.dto.TransaccionResponse;

import java.util.List;

public interface TransaccionService {

    TransaccionResponse registrar(TransaccionRequest request, String email);

    List<TransaccionResponse> listar(String email, Short mes, Short anio, String tipo);

    ResumenTransaccionesResponse obtenerResumen(String email, Short mes, Short anio);

    void eliminar(Long id, String email);
}

