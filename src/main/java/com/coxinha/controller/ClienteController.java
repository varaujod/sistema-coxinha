package com.coxinha.controller;

import com.coxinha.model.Cliente;
import com.coxinha.model.MovimentacaoFinanceira;
import com.coxinha.repository.ClienteRepository;
import com.coxinha.repository.MovimentacaoFinanceiraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private MovimentacaoFinanceiraRepository movimentacaoFinanceiraRepository;

    @GetMapping
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> criarCliente(@RequestBody Map<String, Object> payload) {
        try {
            String nome = payload.get("nome") != null ? payload.get("nome").toString().trim() : "";
            if (nome.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("erro", "Informe um nome para o cliente."));
            }

            Double saldoInicial = payload.containsKey("saldoInicial")
                    ? Double.valueOf(payload.get("saldoInicial").toString())
                    : 0.0;

            if (saldoInicial < 0) {
                return ResponseEntity.badRequest().body(Map.of("erro", "O saldo inicial não pode ser negativo."));
            }

            Cliente cliente = new Cliente(nome, saldoInicial);
            Cliente salvo = clienteRepository.save(cliente);

            if (saldoInicial > 0) {
                MovimentacaoFinanceira mov = new MovimentacaoFinanceira(
                        salvo,
                        saldoInicial,
                        "CREDITO",
                        LocalDateTime.now());
                movimentacaoFinanceiraRepository.save(mov);
            }

            return ResponseEntity.ok(salvo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obterCliente(@PathVariable Long id) {
        return clienteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/recarregar")
    public ResponseEntity<?> recarregarSaldo(@RequestBody Map<String, Object> payload) {
        try {
            Long clienteId = Long.valueOf(payload.get("clienteId").toString());
            Double valor = Double.valueOf(payload.get("valor").toString());

            if (valor <= 0) {
                return ResponseEntity.badRequest().body(Map.of("erro", "O valor da recarga deve ser maior que zero"));
            }

            Cliente cliente = clienteRepository.findById(clienteId)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

            cliente.setSaldo(cliente.getSaldo() + valor);
            clienteRepository.save(cliente);

            // Registrar a movimentação financeira
            MovimentacaoFinanceira mov = new MovimentacaoFinanceira(
                    cliente,
                    valor,
                    "CREDITO",
                    LocalDateTime.now());
            movimentacaoFinanceiraRepository.save(mov);

            return ResponseEntity.ok(cliente);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }
}
