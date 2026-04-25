package com.finplan.deuda.repository;

import com.finplan.deuda.model.PagoDeuda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoDeudaRepository extends JpaRepository<PagoDeuda, Long> {

    List<PagoDeuda> findByCuotaId(Long cuotaId);
}

