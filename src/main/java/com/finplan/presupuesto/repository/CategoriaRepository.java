package com.finplan.presupuesto.repository;

import com.finplan.presupuesto.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CategoriaRepository
        extends JpaRepository<Categoria, Long> {

    List<Categoria> findByUsuarioIdAndActivaTrue(Long usuarioId);

    Optional<Categoria> findByIdAndUsuarioId(Long id, Long usuarioId);

    boolean existsByNombreAndUsuarioId(String nombre, Long usuarioId);
}