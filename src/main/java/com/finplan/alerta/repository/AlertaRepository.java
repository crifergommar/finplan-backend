package com.finplan.alerta.repository;

import com.finplan.alerta.model.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    List<Alerta> findByUsuarioIdAndLeidaFalseOrderByCreatedAtDesc(Long usuarioId);

    List<Alerta> findByUsuarioIdOrderByCreatedAtDesc(Long usuarioId);

    Optional<Alerta> findByIdAndUsuarioId(Long id, Long usuarioId);

    long countByUsuarioIdAndLeidaFalse(Long usuarioId);
}

