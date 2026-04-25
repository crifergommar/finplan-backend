package com.finplan.deuda.model;

import com.finplan.auth.model.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "deudas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Deuda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false)
    private BigDecimal montoTotal;

    @Column(nullable = false)
    private Short numCuotas;

    @Column(nullable = false)
    private BigDecimal tasaInteres;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private boolean activa;

    @OneToMany(mappedBy = "deuda", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CuotaDeuda> cuotas;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Deuda)) return false;
        return id != null && id.equals(((Deuda) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

