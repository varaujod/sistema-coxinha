package com.coxinha.patterns.strategy;

public class HappyHourPricing implements PricingStrategy {

    @Override
    public double calculatePrice(double basePrice, int quantity) {
        return (basePrice * quantity) * 0.9; // 10% de desconto
    }

    @Override
    public String getStrategyName() {
        return "HAPPY_HOUR";
    }
}
