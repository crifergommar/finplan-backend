package com.finplan.deuda.repository;

import com.finplan.deuda.model.Deuda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeudaRepository extends JpaRepository<Deuda, Long> {

    List<Deuda> findByUsuarioIdAndActivaTrue(Long usuarioId);

    Optional<Deuda> findByIdAndUsuarioId(Long id, Long usuarioId);

    List<Deuda> findByUsuarioId(Long usuarioId);
}

