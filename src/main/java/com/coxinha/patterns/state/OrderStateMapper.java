package com.coxinha.patterns.state;

public class OrderStateMapper {
    
    public static OrderState getState(String status) {
        if (status == null) {
            return new PendingState();
        }
        switch (status.toUpperCase()) {
            case "PENDING":
                return new PendingState();
            case "COMPLETED":
                return new CompletedState();
            case "CANCELLED":
                return new CancelledState();
            default:
                throw new IllegalArgumentException("Estado desconhecido: " + status);
        }
    }
}
