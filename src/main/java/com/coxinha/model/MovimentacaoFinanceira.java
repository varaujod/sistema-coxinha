package com.coxinha.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimentacoes_financeiras")
public class MovimentacaoFinanceira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(nullable = false)
    private Double valor; // Negativo para débitos (compras), positivo para créditos (recarga/estorno)

    @Column(nullable = false)
    private String tipo; // Ex: "COMPRA", "ESTORNO", "CREDITO"

    @Column(nullable = false)
    private LocalDateTime dataHora;

    public MovimentacaoFinanceira() {}

    public MovimentacaoFinanceira(Cliente cliente, Double valor, String tipo, LocalDateTime dataHora) {
        this.cliente = cliente;
        this.valor = valor;
        this.tipo = tipo;
        this.dataHora = dataHora;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}
