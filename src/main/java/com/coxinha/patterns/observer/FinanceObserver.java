package com.coxinha.patterns.observer;

import com.coxinha.model.MovimentacaoFinanceira;
import com.coxinha.model.Pedido;
import com.coxinha.repository.MovimentacaoFinanceiraRepository;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class FinanceObserver implements OrderObserver {

    private final MovimentacaoFinanceiraRepository movimentacaoFinanceiraRepository;

    FinanceObserver(MovimentacaoFinanceiraRepository movimentacaoFinanceiraRepository) {
        this.movimentacaoFinanceiraRepository = movimentacaoFinanceiraRepository;
    }

    @Override
    public void onOrderPlaced(Pedido pedido) {
        MovimentacaoFinanceira mov = new MovimentacaoFinanceira(
            pedido.getCliente(),
            -pedido.getValorTotal(), // Débito por compra
            "COMPRA",
            LocalDateTime.now()
        );
        movimentacaoFinanceiraRepository.save(mov);
    }

    @Override
    public void onOrderCancelled(Pedido pedido) {
        MovimentacaoFinanceira mov = new MovimentacaoFinanceira(
            pedido.getCliente(),
            pedido.getValorTotal(), // Crédito por reembolso/estorno
            "ESTORNO",
            LocalDateTime.now()
        );
        movimentacaoFinanceiraRepository.save(mov);
    }
}
