package com.finplan.deuda.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pagos_deuda")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoDeuda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuota_id", nullable = false)
    private CuotaDeuda cuota;

    @Column(nullable = false)
    private BigDecimal monto;

    @Column(nullable = false)
    private LocalDate fechaPago;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PagoDeuda)) return false;
        return id != null && id.equals(((PagoDeuda) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

