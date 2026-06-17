package com.coxinha.service;

import com.coxinha.model.Cliente;
import com.coxinha.model.Pedido;
import com.coxinha.model.Salgado;
import com.coxinha.patterns.command.CommandInvoker;
import com.coxinha.patterns.command.CreateOrderCommand;
import com.coxinha.patterns.observer.OrderSubject;
import com.coxinha.patterns.strategy.HappyHourPricing;
import com.coxinha.patterns.strategy.PricingStrategy;
import com.coxinha.patterns.strategy.RegularPricing;
import com.coxinha.repository.ClienteRepository;
import com.coxinha.repository.PedidoRepository;
import com.coxinha.repository.SalgadoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PedidoService {

    private final ClienteRepository clienteRepository;

    private final SalgadoRepository salgadoRepository;

    private final PedidoRepository pedidoRepository;

    private final OrderSubject orderSubject;

    private final CommandInvoker commandInvoker;

    PedidoService(ClienteRepository clienteRepository, SalgadoRepository salgadoRepository, PedidoRepository pedidoRepository, OrderSubject orderSubject, CommandInvoker commandInvoker) {
        this.clienteRepository = clienteRepository;
        this.salgadoRepository = salgadoRepository;
        this.pedidoRepository = pedidoRepository;
        this.orderSubject = orderSubject;
        this.commandInvoker = commandInvoker;
    }

    @Transactional
    public Pedido criarPedido(Long clienteId, String sabor, Integer quantidade, String strategyName) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com ID " + clienteId));

        Salgado salgado = salgadoRepository.findBySabor(sabor.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Coxinha sabor " + sabor + " não encontrada no cardápio"));

        // Selecionar estratégia de precificação (Strategy Pattern)
        PricingStrategy strategy;
        if ("HAPPY_HOUR".equalsIgnoreCase(strategyName)) {
            strategy = new HappyHourPricing();
        } else {
            strategy = new RegularPricing();
        }

        double valorTotal = strategy.calculatePrice(salgado.getPrecoBase(), quantidade);

        // Criar o Command (Command Pattern)
        CreateOrderCommand command = new CreateOrderCommand(
                cliente,
                salgado,
                quantidade,
                valorTotal,
                clienteRepository,
                salgadoRepository,
                pedidoRepository,
                orderSubject
        );

        // Executar via Invoker
        commandInvoker.executeCommand(command);

        // Retornar o pedido gerado
        return pedidoRepository.findById(command.getPedidoId()).orElse(null);
    }

    @Transactional
    public void estornarPedido(Long pedidoId) {
        try {
            // Tenta desfazer a partir do histórico em memória
            commandInvoker.undoCommand(pedidoId);
        } catch (IllegalArgumentException e) {
            // Se não estiver na memória (ex: reiniciou o servidor), recria a partir do banco de dados
            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado com o ID " + pedidoId));

            CreateOrderCommand command = new CreateOrderCommand(
                    pedido,
                    clienteRepository,
                    salgadoRepository,
                    pedidoRepository,
                    orderSubject
            );

            // Executa o desfazer e registra no invoker
            command.undo();
            commandInvoker.registerCommand(pedidoId, command);
            commandInvoker.undoCommand(pedidoId); // Limpa do histórico
        }
    }
}
