package com.coxinha.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimentacoes_estoque")
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "salgado_id")
    private Salgado salgado;

    @Column(nullable = false)
    private Integer quantidade; // Negativo para saídas, positivo para entradas

    @Column(nullable = false)
    private String tipo; // Ex: "SAIDA_VENDA", "ENTRADA_ESTORNO", "REPOSICAO"

    @Column(nullable = false)
    private LocalDateTime dataHora;

    public MovimentacaoEstoque() {}

    public MovimentacaoEstoque(Salgado salgado, Integer quantidade, String tipo, LocalDateTime dataHora) {
        this.salgado = salgado;
        this.quantidade = quantidade;
        this.tipo = tipo;
        this.dataHora = dataHora;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Salgado getSalgado() {
        return salgado;
    }

    public void setSalgado(Salgado salgado) {
        this.salgado = salgado;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
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
