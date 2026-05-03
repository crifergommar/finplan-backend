package com.finplan.presupuesto.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.finplan.presupuesto.model.Categoria;
import com.finplan.presupuesto.model.TipoCategoria;

public interface CategoriaRepository
        extends JpaRepository<Categoria, Long> {

    List<Categoria> findByUsuarioIdAndActivaTrue(Long usuarioId);

    Optional<Categoria> findByIdAndUsuarioId(Long id, Long usuarioId);

    boolean existsByNombreAndUsuarioId(String nombre, Long usuarioId);

    boolean existsByNombreAndTipoAndUsuarioId(String nombre, TipoCategoria tipo, Long usuarioId);
}