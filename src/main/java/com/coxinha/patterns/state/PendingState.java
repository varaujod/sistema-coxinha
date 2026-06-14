package com.coxinha.patterns.state;

import com.coxinha.model.Pedido;

public class PendingState implements OrderState {

    @Override
    public void complete(Pedido pedido) {
        pedido.setStatus("COMPLETED");
    }

    @Override
    public void cancel(Pedido pedido) {
        pedido.setStatus("CANCELLED");
    }

    @Override
    public String getStatusName() {
        return "PENDING";
    }
}
