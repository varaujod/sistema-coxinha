package com.coxinha.patterns.command;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class CommandInvoker {

    private final Map<Long, OrderCommand> history = new HashMap<>();

    public void executeCommand(OrderCommand command) {
        command.execute();
        if (command.getPedidoId() != null) {
            history.put(command.getPedidoId(), command);
        }
    }

    public void undoCommand(Long pedidoId) {
        OrderCommand command = history.get(pedidoId);
        if (command != null) {
            command.undo();
            history.remove(pedidoId); // Remove da lista ativa de comandos na memória
        } else {
            throw new IllegalArgumentException("Nenhum comando em memória correspondente ao ID do pedido " + pedidoId);
        }
    }

    // Registra comandos que são carregados do banco de dados (se necessário recriar)
    public void registerCommand(Long pedidoId, OrderCommand command) {
        history.put(pedidoId, command);
    }
}
