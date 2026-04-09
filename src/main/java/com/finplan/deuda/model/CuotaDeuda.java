package com.finplan.deuda.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "cuotas_deuda")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuotaDeuda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deuda_id", nullable = false)
    private Deuda deuda;

    @Column(nullable = false)
    private Short numero;

    @Column(nullable = false)
    private BigDecimal monto;

    @Column(nullable = false)
    private LocalDate fechaVcto;

    @Column(nullable = false)
    private boolean pagada;

    @OneToMany(mappedBy = "cuota", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PagoDeuda> pagos;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CuotaDeuda)) return false;
        return id != null && id.equals(((CuotaDeuda) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

