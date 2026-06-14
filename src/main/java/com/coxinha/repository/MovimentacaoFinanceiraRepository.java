package com.coxinha.repository;

import com.coxinha.model.MovimentacaoFinanceira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MovimentacaoFinanceiraRepository extends JpaRepository<MovimentacaoFinanceira, Long> {
    List<MovimentacaoFinanceira> findByClienteIdOrderByIdDesc(Long clienteId);
    List<MovimentacaoFinanceira> findAllByOrderByIdDesc();
}
