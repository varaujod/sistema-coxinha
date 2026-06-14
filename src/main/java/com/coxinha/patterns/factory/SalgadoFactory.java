package com.coxinha.patterns.factory;

import com.coxinha.model.*;

public class SalgadoFactory {
    
    public static Salgado createSalgado(String sabor, Integer estoque) {
        if (sabor == null) {
            throw new IllegalArgumentException("O sabor não pode ser nulo");
        }
        
        switch (sabor.toUpperCase()) {
            case "FRANGO":
                return new CoxinhaFrango(estoque);
            case "CARNE":
                return new CoxinhaCarne(estoque);
            case "QUEIJO":
                return new CoxinhaQueijo(estoque);
            case "CATUPIRY":
                return new CoxinhaCatupiry(estoque);
            default:
                throw new IllegalArgumentException("Sabor de coxinha desconhecido: " + sabor);
        }
    }
}
