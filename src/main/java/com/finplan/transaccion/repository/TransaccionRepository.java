package com.finplan.transaccion.repository;

import com.finplan.auth.model.Usuario;
import com.finplan.presupuesto.model.Categoria;
import com.finplan.presupuesto.model.TipoCategoria;
import com.finplan.transaccion.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    List<Transaccion> findByUsuario(Usuario usuario);

    List<Transaccion> findByUsuarioAndFechaBetween(Usuario usuario, LocalDate fechaInicio, LocalDate fechaFin);

    List<Transaccion> findByUsuarioAndTipo(Usuario usuario, TipoCategoria tipo);

    List<Transaccion> findByUsuarioAndTipoAndFechaBetween(Usuario usuario, TipoCategoria tipo,
                                                           LocalDate fechaInicio, LocalDate fechaFin);

    List<Transaccion> findByUsuarioAndCategoria(Usuario usuario, Categoria categoria);

    @Query("SELECT t FROM Transaccion t WHERE t.usuario = :usuario " +
           "AND YEAR(t.fecha) = :anio AND MONTH(t.fecha) = :mes")
    List<Transaccion> findByUsuarioAndMesAndAnio(@Param("usuario") Usuario usuario,
                                                  @Param("mes") Short mes,
                                                  @Param("anio") Short anio);

    @Query("SELECT t FROM Transaccion t WHERE t.usuario = :usuario " +
           "AND YEAR(t.fecha) = :anio AND MONTH(t.fecha) = :mes AND t.tipo = :tipo")
    List<Transaccion> findByUsuarioMesAnioTipo(@Param("usuario") Usuario usuario,
                                               @Param("mes") Short mes,
                                               @Param("anio") Short anio,
                                               @Param("tipo") TipoCategoria tipo);

    @Query("SELECT t FROM Transaccion t WHERE t.usuario = :usuario " +
           "AND YEAR(t.fecha) = :anio AND MONTH(t.fecha) = :mes AND t.tipo = :tipo")
    List<Transaccion> findByAnioMesTipo(@Param("usuario") Usuario usuario,
                                        @Param("anio") Short anio,
                                        @Param("mes") Short mes,
                                        @Param("tipo") TipoCategoria tipo);

    @Query("SELECT SUM(t.monto) FROM Transaccion t WHERE t.usuario = :usuario " +
           "AND t.categoria = :categoria AND YEAR(t.fecha) = :anio AND MONTH(t.fecha) = :mes")
    BigDecimal sumMontoByUsuarioCategoriaAndMesAndAnio(@Param("usuario") Usuario usuario,
                                                        @Param("categoria") Categoria categoria,
                                                        @Param("mes") Short mes,
                                                        @Param("anio") Short anio);

    @Query("SELECT t.categoria, SUM(t.monto) as total FROM Transaccion t " +
           "WHERE t.usuario = :usuario AND YEAR(t.fecha) = :anio AND MONTH(t.fecha) = :mes " +
           "GROUP BY t.categoria")
    List<Object[]> sumByCategoriaMesAnio(@Param("usuario") Usuario usuario,
                                         @Param("mes") Short mes,
                                         @Param("anio") Short anio);

    Optional<Transaccion> findByIdAndUsuario(Long id, Usuario usuario);

    void deleteByIdAndUsuario(Long id, Usuario usuario);
}

