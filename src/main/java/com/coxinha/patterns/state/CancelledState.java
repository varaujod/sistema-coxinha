package com.coxinha.patterns.state;

import com.coxinha.model.Pedido;

public class CancelledState implements OrderState {

    @Override
    public void complete(Pedido pedido) {
        throw new IllegalStateException("Um pedido cancelado não pode ser concluído");
    }

    @Override
    public void cancel(Pedido pedido) {
        throw new IllegalStateException("O pedido já está cancelado");
    }

    @Override
    public String getStatusName() {
        return "CANCELLED";
    }
}
