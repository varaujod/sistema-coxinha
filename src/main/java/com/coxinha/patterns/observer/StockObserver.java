package com.coxinha.patterns.observer;

import com.coxinha.model.MovimentacaoEstoque;
import com.coxinha.model.Pedido;
import com.coxinha.repository.MovimentacaoEstoqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class StockObserver implements OrderObserver {

    @Autowired
    private MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    @Override
    public void onOrderPlaced(Pedido pedido) {
        MovimentacaoEstoque mov = new MovimentacaoEstoque(
            pedido.getSalgado(),
            -pedido.getQuantidade(), // Saída de estoque
            "SAIDA_VENDA",
            LocalDateTime.now()
        );
        movimentacaoEstoqueRepository.save(mov);
    }

    @Override
    public void onOrderCancelled(Pedido pedido) {
        MovimentacaoEstoque mov = new MovimentacaoEstoque(
            pedido.getSalgado(),
            pedido.getQuantidade(), // Devolução para o estoque
            "ENTRADA_ESTORNO",
            LocalDateTime.now()
        );
        movimentacaoEstoqueRepository.save(mov);
    }
}
