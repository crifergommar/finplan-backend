package com.finplan.presupuesto.repository;

import com.finplan.presupuesto.model.PresupuestoMensual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.util.Optional;

public interface PresupuestoMensualRepository
        extends JpaRepository<PresupuestoMensual, Long> {

    Optional<PresupuestoMensual> findByPresupuestoUsuarioIdAndCategoriaIdAndPresupuestoAnioAndMes(
            Long usuarioId, Long categoriaId, Short anio, Short mes);

    @Query("SELECT pm FROM PresupuestoMensual pm " +
           "WHERE pm.presupuesto.usuario.id = :usuarioId " +
           "AND pm.categoria.id = :categoriaId " +
           "AND pm.presupuesto.anio = :anio " +
           "AND pm.mes = :mes")
    Optional<PresupuestoMensual> findByUsuarioCategoriaMes(
            Long usuarioId, Long categoriaId,
            Short anio, Short mes);

    @Query("SELECT COALESCE(pm.montoPlaneado, 0) " +
           "FROM PresupuestoMensual pm " +
           "WHERE pm.presupuesto.usuario.id = :usuarioId " +
           "AND pm.categoria.id = :categoriaId " +
           "AND pm.presupuesto.anio = :anio " +
           "AND pm.mes = :mes")
    Optional<BigDecimal> findMontoPlaneado(
            Long usuarioId, Long categoriaId,
            Short anio, Short mes);
}