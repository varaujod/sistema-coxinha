package com.coxinha.patterns.observer;

import com.coxinha.model.Pedido;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class OrderSubject {

    private final List<OrderObserver> observers;

    OrderSubject(List<OrderObserver> observers) {
        this.observers = observers;
    }

    public void notifyOrderPlaced(Pedido pedido) {
        if (observers != null) {
            for (OrderObserver observer : observers) {
                observer.onOrderPlaced(pedido);
            }
        }
    }

    public void notifyOrderCancelled(Pedido pedido) {
        if (observers != null) {
            for (OrderObserver observer : observers) {
                observer.onOrderCancelled(pedido);
            }
        }
    }
}
