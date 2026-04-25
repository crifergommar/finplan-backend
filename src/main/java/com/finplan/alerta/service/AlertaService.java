package com.finplan.alerta.service;

import com.finplan.alerta.model.AlertaResponse;
import com.finplan.alerta.model.ContadorAlertasResponse;

import java.util.List;

public interface AlertaService {

    List<AlertaResponse> listarAlertas(Boolean activa, String email);

    void marcarComoLeida(Long alertaId, String email);

    ContadorAlertasResponse contarNoLeidas(String email);
}

