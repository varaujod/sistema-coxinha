package com.coxinha.model;

import jakarta.persistence.*;

@Entity
@Table(name = "salgados")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_salgado", discriminatorType = DiscriminatorType.STRING)
public abstract class Salgado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sabor;

    @Column(nullable = false)
    private Double precoBase;

    @Column(nullable = false)
    private Integer estoque;

    public Salgado() {}

    public Salgado(String sabor, Double precoBase, Integer estoque) {
        this.sabor = sabor;
        this.precoBase = precoBase;
        this.estoque = estoque;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSabor() {
        return sabor;
    }

    public void setSabor(String sabor) {
        this.sabor = sabor;
    }

    public Double getPrecoBase() {
        return precoBase;
    }

    public void setPrecoBase(Double precoBase) {
        this.precoBase = precoBase;
    }

    public Integer getEstoque() {
        return estoque;
    }

    public void setEstoque(Integer estoque) {
        this.estoque = estoque;
    }
}
