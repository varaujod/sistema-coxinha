package com.coxinha.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("QUEIJO")
public class CoxinhaQueijo extends Salgado {
    public CoxinhaQueijo() {
        super("QUEIJO", 8.5, 10);
    }

    public CoxinhaQueijo(Integer estoque) {
        super("QUEIJO", 8.5, estoque);
    }
}
