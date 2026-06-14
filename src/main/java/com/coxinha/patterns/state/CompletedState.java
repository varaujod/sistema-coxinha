package com.coxinha.patterns.state;

import com.coxinha.model.Pedido;

public class CompletedState implements OrderState {

    @Override
    public void complete(Pedido pedido) {
        throw new IllegalStateException("O pedido já está concluído");
    }

    @Override
    public void cancel(Pedido pedido) {
        pedido.setStatus("CANCELLED");
    }

    @Override
    public String getStatusName() {
        return "COMPLETED";
    }
}
