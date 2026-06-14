package com.coxinha.patterns.strategy;

public class RegularPricing implements PricingStrategy {
    
    @Override
    public double calculatePrice(double basePrice, int quantity) {
        return basePrice * quantity;
    }

    @Override
    public String getStrategyName() {
        return "REGULAR";
    }
}
