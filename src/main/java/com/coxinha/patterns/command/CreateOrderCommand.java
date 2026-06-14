package com.coxinha.patterns.command;

import com.coxinha.model.Cliente;
import com.coxinha.model.Pedido;
import com.coxinha.model.Salgado;
import com.coxinha.patterns.observer.OrderSubject;
import com.coxinha.patterns.state.OrderState;
import com.coxinha.patterns.state.OrderStateMapper;
import com.coxinha.repository.ClienteRepository;
import com.coxinha.repository.PedidoRepository;
import com.coxinha.repository.SalgadoRepository;
import java.time.LocalDateTime;

public class CreateOrderCommand implements OrderCommand {

    private final ClienteRepository clienteRepository;
    private final SalgadoRepository salgadoRepository;
    private final PedidoRepository pedidoRepository;
    private final OrderSubject orderSubject;

    // Estado interno para execução
    private Cliente cliente;
    private Salgado salgado;
    private Integer quantidade;
    private Double valorTotal;
    private Pedido pedido;

    // Construtor para executar um novo pedido
    public CreateOrderCommand(
            Cliente cliente,
            Salgado salgado,
            Integer quantidade,
            Double valorTotal,
            ClienteRepository clienteRepository,
            SalgadoRepository salgadoRepository,
            PedidoRepository pedidoRepository,
            OrderSubject orderSubject) {
        this.cliente = cliente;
        this.salgado = salgado;
        this.quantidade = quantidade;
        this.valorTotal = valorTotal;
        this.clienteRepository = clienteRepository;
        this.salgadoRepository = salgadoRepository;
        this.pedidoRepository = pedidoRepository;
        this.orderSubject = orderSubject;
    }

    // Construtor para desfazer (estornar) um pedido existente
    public CreateOrderCommand(
            Pedido pedido,
            ClienteRepository clienteRepository,
            SalgadoRepository salgadoRepository,
            PedidoRepository pedidoRepository,
            OrderSubject orderSubject) {
        this.pedido = pedido;
        this.cliente = pedido.getCliente();
        this.salgado = pedido.getSalgado();
        this.quantidade = pedido.getQuantidade();
        this.valorTotal = pedido.getValorTotal();
        this.clienteRepository = clienteRepository;
        this.salgadoRepository = salgadoRepository;
        this.pedidoRepository = pedidoRepository;
        this.orderSubject = orderSubject;
    }

    @Override
    public void execute() {
        // 1. Validações
        if (salgado.getEstoque() < quantidade) {
            throw new IllegalArgumentException("Estoque insuficiente para a coxinha de " + salgado.getSabor());
        }
        if (cliente.getSaldo() < valorTotal) {
            throw new IllegalArgumentException("Saldo insuficiente! Você precisa de R$ " + String.format("%.2f", valorTotal) + " mas tem apenas R$ " + String.format("%.2f", cliente.getSaldo()));
        }

        // 2. Transacionar saldo do cliente
        cliente.setSaldo(cliente.getSaldo() - valorTotal);
        clienteRepository.save(cliente);

        // 3. Transacionar estoque do salgado
        salgado.setEstoque(salgado.getEstoque() - quantidade);
        salgadoRepository.save(salgado);

        // 4. Criar o pedido (inicialmente PENDING)
        pedido = new Pedido(cliente, salgado, quantidade, valorTotal, LocalDateTime.now(), "PENDING");
        
        // 5. Aplicar transição de estado usando State Pattern
        OrderState state = OrderStateMapper.getState(pedido.getStatus());
        state.complete(pedido); // Transiciona de PENDING para COMPLETED
        
        // Salva pedido concluído
        pedido = pedidoRepository.save(pedido);

        // 6. Notificar observadores (atualização de estoque/financeiro logs no BD)
        orderSubject.notifyOrderPlaced(pedido);
    }

    @Override
    public void undo() {
        if (pedido == null) {
            throw new IllegalStateException("Nenhum pedido associado a este comando para estorno");
        }

        // 1. Obter estado atual usando State Pattern e validar estorno
        OrderState state = OrderStateMapper.getState(pedido.getStatus());
        
        // Transiciona de COMPLETED para CANCELLED (ou lança exceção se já cancelado)
        state.cancel(pedido);
        
        // 2. Reverter saldo do cliente
        cliente.setSaldo(cliente.getSaldo() + valorTotal);
        clienteRepository.save(cliente);

        // 3. Reverter estoque do salgado
        salgado.setEstoque(salgado.getEstoque() + quantidade);
        salgadoRepository.save(salgado);

        // 4. Salvar alteração do pedido
        pedidoRepository.save(pedido);

        // 5. Notificar observadores sobre o estorno
        orderSubject.notifyOrderCancelled(pedido);
    }

    @Override
    public Long getPedidoId() {
        return pedido != null ? pedido.getId() : null;
    }
}
