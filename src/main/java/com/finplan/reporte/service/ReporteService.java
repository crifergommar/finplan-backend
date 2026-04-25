package com.finplan.reporte.service;

import com.finplan.reporte.dto.BalanceMensualResponse;
import com.finplan.reporte.dto.ComparativoResponse;

public interface ReporteService {

    ComparativoResponse obtenerComparativo(String email, Short anio, Short mes);

    BalanceMensualResponse obtenerBalanceMensual(String email, Short anio, Short mes);
}

