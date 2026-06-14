package com.coxinha.controller;

import com.coxinha.model.MovimentacaoEstoque;
import com.coxinha.model.MovimentacaoFinanceira;
import com.coxinha.model.Pedido;
import com.coxinha.model.Salgado;
import com.coxinha.repository.MovimentacaoEstoqueRepository;
import com.coxinha.repository.MovimentacaoFinanceiraRepository;
import com.coxinha.repository.PedidoRepository;
import com.coxinha.repository.SalgadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private SalgadoRepository salgadoRepository;

    @Autowired
    private MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

    @Autowired
    private MovimentacaoFinanceiraRepository movimentacaoFinanceiraRepository;

    @GetMapping("/pedidos/{clienteId}")
    public List<Pedido> obterHistoricoCliente(@PathVariable Long clienteId) {
        return pedidoRepository.findByClienteIdOrderByIdDesc(clienteId);
    }

    @GetMapping("/cardapio")
    public List<Salgado> obterCardapio() {
        return salgadoRepository.findAll();
    }

    @GetMapping("/movimentacoes-estoque")
    public List<MovimentacaoEstoque> obterMovimentacoesEstoque() {
        return movimentacaoEstoqueRepository.findAllByOrderByIdDesc();
    }

    @GetMapping("/movimentacoes-financeiras")
    public List<MovimentacaoFinanceira> obterMovimentacoesFinanceiras() {
        return movimentacaoFinanceiraRepository.findAllByOrderByIdDesc();
    }
}
