# 🥟 Coxinhas Prime - Totem de Autoatendimento

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk" alt="Java 17">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=for-the-badge&logo=springboot" alt="Spring Boot">
  <img src="https://img.shields.io/badge/JPA%20/%20Hibernate-Data-blue?style=for-the-badge" alt="JPA">
  <img src="https://img.shields.io/badge/Architecture-MVC-red?style=for-the-badge" alt="MVC">
</p>

Este projeto consiste num sistema completo de autoatendimento para um totem de venda de salgados (coxinhas), desenvolvido como critério de avaliação académica. A aplicação simula um terminal físico moderno, integrando um back-end robusto focado em regras de negócio desacopladas e persistência de dados em memória.

O grande diferencial técnico deste projeto é a implementação rigorosa de **5 Padrões de Projeto (Design Patterns) do GoF**, demonstrando maturidade em Programação Orientada a Objetos Avançada e arquitetura limpa.

---

## 🏗️ Padrões de Projeto Implementados

Os padrões encontram-se isolados no pacote `com.coxinha.patterns` e estruturam o ciclo de vida do sistema:

### 1. **Command Pattern** (`/patterns/command`)
* **Responsabilidade:** Encapsula operações de compra como objetos independentes.
* **Mecanismo de Desfazer (Undo):** A classe `CreateOrderCommand` executa a validação e efetivação do pedido. O histórico das transações é mantido na memória pelo `CommandInvoker`, permitindo a reversão cirúrgica de uma compra (estorno), devolvendo o saldo exato ao cliente e reabastecendo o estoque físico de salgados sem corromper o histórico.

### 2. **Strategy Pattern** (`/patterns/strategy`)
* **Responsabilidade:** Alterna algoritmos de precificação dinamicamente.
* **Aplicação:** Elimina blocos condicionais complexos (`if/else`) para regras de negócio mutáveis. Através da interface `PricingStrategy`, o sistema aplica de forma isolada e limpa as regras de `RegularPricing` (preço cheio) e `HappyHourPricing` (aplicação automática de 10% de desconto sobre o total).

### 3. **State Pattern** (`/patterns/state`)
* **Responsabilidade:** Modifica o comportamento do objeto de acordo com o seu estado interno.
* **Aplicação:** Controla de forma rígida e segura o ciclo de vida e a transição de status do `Pedido` utilizando as classes abstratas e concretas `PendingState`, `CompletedState` e `CancelledState`. Garante que operações inválidas sejam barradas pelo próprio objeto (ex: impedir o cancelamento ou conclusão de um pedido já cancelado).

### 4. **Observer Pattern** (`/patterns/observer`)
* **Responsabilidade:** Define uma dependência um-para-múltiplos para notificações orientadas a eventos.
* **Aplicação:** Desacopla as rotinas de auditoria e relatórios do fluxo principal de compras. Sempre que um comando altera um pedido, o `OrderSubject` dispara notificações para os ouvintes assinados. O `StockObserver` gera logs automáticos de movimentação física, enquanto o `FinanceObserver` alimenta a base de dados de auditoria financeira com registros de crédito/débito.

### 5. **Factory Pattern** (`/patterns/factory`)
* **Responsabilidade:** Centraliza a lógica de instanciação de subclasses polimórficas.
* **Aplicação:** A classe `SalgadoFactory` intercepta a string enviada pela interface visual e encapsula a criação de instâncias herdeiras da entidade `Salgado` (`CoxinhaFrango`, `CoxinhaCarne`, `CoxinhaQueijo`, etc.), protegendo o restante do sistema do acoplamento direto com os construtores da hierarquia.

---

## ⚙️ Arquitetura e Estrutura do Código

A aplicação adota o padrão arquitetural **MVC (Model-View-Controller)**:

* **`model`**: Contém as entidades mapeadas via JPA e os relacionamentos de banco de dados (ex: `@ManyToOne` entre Pedido e Cliente). Destaque para a estratégia de herança `@Inheritance(strategy = InheritanceType.SINGLE_TABLE)` aplicada à classe `Salgado`.
* **`repository`**: Interfaces que estendem `JpaRepository`, responsáveis pelas consultas e persistência das entidades.
* **`service`**: Camada que atua como maestrina do sistema, recuperando os dados necessários e acionando as fábricas, comandos e estratégias adequadas.
* **`controller`**: Camada de exposição da API REST, manipulando payloads JSON via anotações do Spring (ex: `@PostMapping`, `@GetMapping`).
* **`view` (Front-End)**: Interface rica construída com HTML5, CSS3 (*Glassmorphism effects*) e Vanilla JavaScript assíncrono (Fetch API) localizada na pasta `src/main/resources/static`.

## 📋 Principais Endpoints da API REST

| Categoria | Método | Endpoint | Descrição |
| :--- | :---: | :--- | :--- |
| **Clientes** | `GET` | `/api/clientes` | Recupera a lista de clientes registados. |
| **Clientes** | `POST` | `/api/clientes` | Cria um novo usuário com saldo inicial zero. |
| **Clientes** | `POST` | `/api/clientes/recarregar` | Simula a inserção de cédulas para recarga de saldo. |
| **Pedidos** | `POST` | `/api/pedidos` | Cria uma nova transação usando *Strategy* e *Command*. |
| **Pedidos** | `POST` | `/api/pedidos/{id}/estornar` | Aciona o método `undo()` do *Command* via ID do pedido. |
| **Relatórios**| `GET` | `/api/relatorios/cardapio` | Retorna a vitrine e a quantidade disponível em estoque. |
| **Auditoria** | `GET` | `/api/relatorios/movimentacoes-financeiras` | Histórico assíncrono gerado pelo `FinanceObserver`. |
| **Auditoria** | `GET` | `/api/relatorios/movimentacoes-estoque` | Histórico assíncrono gerado pelo `StockObserver`. |

