package com.coxinha.patterns.observer;

import com.coxinha.model.Pedido;

public interface OrderObserver {
    void onOrderPlaced(Pedido pedido);
    void onOrderCancelled(Pedido pedido);
}
