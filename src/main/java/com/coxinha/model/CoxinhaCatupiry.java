package com.coxinha.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CATUPIRY")
public class CoxinhaCatupiry extends Salgado {
    public CoxinhaCatupiry() {
        super("CATUPIRY", 9.5, 10);
    }

    public CoxinhaCatupiry(Integer estoque) {
        super("CATUPIRY", 9.5, estoque);
    }
}
