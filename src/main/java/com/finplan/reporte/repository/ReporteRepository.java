package com.finplan.reporte.repository;

import com.finplan.presupuesto.model.PresupuestoMensual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ReporteRepository extends JpaRepository<PresupuestoMensual, Long> {

    @Query("SELECT SUM(pm.montoPlaneado) FROM PresupuestoMensual pm " +
           "WHERE pm.presupuesto.usuario.id = :usuarioId " +
           "AND pm.presupuesto.anio = :anio " +
           "AND pm.mes = :mes")
    Optional<BigDecimal> obtenerTotalPlaneado(@Param("usuarioId") Long usuarioId,
                                              @Param("anio") Short anio,
                                              @Param("mes") Short mes);
}


