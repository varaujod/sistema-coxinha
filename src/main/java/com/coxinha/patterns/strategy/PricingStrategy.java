package com.coxinha.patterns.strategy;

public interface PricingStrategy {
    double calculatePrice(double basePrice, int quantity);
    String getStrategyName();
}
