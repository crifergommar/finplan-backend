package com.finplan.presupuesto.repository;

import com.finplan.presupuesto.model.Presupuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PresupuestoRepository
        extends JpaRepository<Presupuesto, Long> {

    Optional<Presupuesto> findByUsuarioIdAndAnio(
            Long usuarioId, Short anio);

    boolean existsByUsuarioIdAndAnio(Long usuarioId, Short anio);
}