package com.coxinha.patterns.observer;

import com.coxinha.model.Pedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class OrderSubject {

    @Autowired
    private List<OrderObserver> observers;

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
