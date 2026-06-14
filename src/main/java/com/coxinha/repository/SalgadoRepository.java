package com.coxinha.repository;

import com.coxinha.model.Salgado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SalgadoRepository extends JpaRepository<Salgado, Long> {
    Optional<Salgado> findBySabor(String sabor);
}
