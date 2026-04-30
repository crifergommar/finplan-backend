package com.finplan.deuda.repository;

import com.finplan.deuda.model.CuotaDeuda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CuotaDeudaRepository extends JpaRepository<CuotaDeuda, Long> {

    List<CuotaDeuda> findByDeudaId(Long deudaId);

    Optional<CuotaDeuda> findByIdAndDeudaUsuarioId(Long id, Long usuarioId);

    @Query("SELECT c FROM CuotaDeuda c " +
           "WHERE c.deuda.usuario.id = :usuarioId " +
           "AND YEAR(c.fechaVcto) = :anio " +
           "AND MONTH(c.fechaVcto) = :mes")
    List<CuotaDeuda> findByUsuarioIdAndMesAnio(
            @Param("usuarioId") Long usuarioId,
            @Param("anio") int anio,
            @Param("mes") int mes
    );

    @Query("SELECT c FROM CuotaDeuda c " +
           "WHERE c.deuda.usuario.id = :usuarioId " +
           "AND c.pagada = false " +
           "AND c.fechaVcto BETWEEN :inicio AND :fin")
    List<CuotaDeuda> findCuotasPendientesPorRango(
            @Param("usuarioId") Long usuarioId,
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin
    );
}

