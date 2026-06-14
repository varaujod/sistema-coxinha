package com.coxinha.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CARNE")
public class CoxinhaCarne extends Salgado {
    public CoxinhaCarne() {
        super("CARNE", 9.0, 10);
    }

    public CoxinhaCarne(Integer estoque) {
        super("CARNE", 9.0, estoque);
    }
}
