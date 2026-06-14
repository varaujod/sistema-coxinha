package com.coxinha.controller;

import com.coxinha.model.Pedido;
import com.coxinha.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<?> realizarPedido(@RequestBody Map<String, Object> payload) {
        try {
            Long clienteId = Long.valueOf(payload.get("clienteId").toString());
            String sabor = payload.get("sabor").toString();
            Integer quantidade = Integer.valueOf(payload.get("quantidade").toString());
            String strategy = payload.get("strategy") != null ? payload.get("strategy").toString() : "REGULAR";

            if (quantidade <= 0) {
                return ResponseEntity.badRequest().body(Map.of("erro", "A quantidade deve ser maior que zero"));
            }

            Pedido pedido = pedidoService.criarPedido(clienteId, sabor, quantidade, strategy);
            return ResponseEntity.ok(pedido);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("erro", "Erro interno: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/estornar")
    public ResponseEntity<?> estornarPedido(@PathVariable Long id) {
        try {
            pedidoService.estornarPedido(id);
            return ResponseEntity.ok(Map.of("status", "sucesso", "mensagem", "Pedido estornado com sucesso"));
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("erro", "Erro interno: " + e.getMessage()));
        }
    }
}
