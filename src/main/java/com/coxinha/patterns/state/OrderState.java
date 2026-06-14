package com.coxinha.patterns.state;

import com.coxinha.model.Pedido;

public interface OrderState {
    void complete(Pedido pedido);
    void cancel(Pedido pedido);
    String getStatusName();
}
