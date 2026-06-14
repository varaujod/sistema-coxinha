package com.coxinha.patterns.command;

public interface OrderCommand {
    void execute();
    void undo();
    Long getPedidoId();
}
