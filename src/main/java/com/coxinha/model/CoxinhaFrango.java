package com.coxinha.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("FRANGO")
public class CoxinhaFrango extends Salgado {
    public CoxinhaFrango() {
        super("FRANGO", 8.0, 10);
    }

    public CoxinhaFrango(Integer estoque) {
        super("FRANGO", 8.0, estoque);
    }
}
