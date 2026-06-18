// ESTADO GLOBAL DO TOTEM
let clienteAtivoId = null;
let activeKioskSection = 'menu';
let activeKioskAuditTab = 'financeiro';

function formatCurrency(value) {
    return Number(value || 0).toLocaleString('pt-BR', {
        style: 'currency',
        currency: 'BRL'
    });
}

function formatCurrencyValue(value) {
    return formatCurrency(value).replace('R$', '').trim();
}

function setLoadingState(elementId, isLoading, label = 'Carregando...') {
    const element = document.getElementById(elementId);
    if (!element) return;

    if (isLoading) {
        element.dataset.previousText = element.textContent;
        element.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i> ${label}`;
        element.disabled = true;
        return;
    }

    element.disabled = false;
    const previousText = element.dataset.previousText || element.textContent;
    if (previousText) element.innerHTML = previousText;
}

function setApiStatus(message, tone = 'online') {
    const chip = document.getElementById('api-status-chip');
    if (!chip) return;

    chip.className = `api-status-chip ${tone}`;
    chip.innerHTML = `<i class="fa-solid ${tone === 'offline' ? 'fa-wifi-slash' : 'fa-wifi'}"></i> ${message}`;
}

function handleApiError(error, fallbackMessage = 'Falha ao comunicar com a API.') {
    console.error(error);
    const message = error?.message || fallbackMessage;
    showToast(message, 'error');
    setApiStatus('API indisponível no momento', 'offline');
    return message;
}

function safeText(elementId, value) {
    const element = document.getElementById(elementId);
    if (!element) return;
    element.textContent = value;
}

// INICIALIZAÇÃO
document.addEventListener('DOMContentLoaded', () => {
    inicializarTotem();
    
    // Fechar modal ao clicar fora dele
    window.onclick = (event) => {
        const modal = document.getElementById('recarga-modal');
        if (event.target === modal) {
            fecharRecargaModal();
        }
    };
});

// BUSCAR E POPULAR TELA DE BOAS VINDAS
async function inicializarTotem() {
    setApiStatus('Pronto para iniciar atendimento', 'online');
    
    try {
        const response = await fetch('/api/clientes');
        const clientes = await response.json();
        
        const select = document.getElementById('select-cliente');
        if (select) {
            select.innerHTML = '<option value="">Selecione quem é você...</option>';
            clientes.forEach(c => {
                select.innerHTML += `<option value="${c.id}">${c.nome} (Saldo: R$ ${c.saldo.toFixed(2)})</option>`;
            });
        }
    } catch (error) {
        console.error('Erro ao carregar clientes da API', error);
    }
}

// FUNÇÃO PARA ENTRAR COM CONTA JÁ EXISTENTE
function entrarContaExistente() {
    const select = document.getElementById('select-cliente');
    
    if (select && select.value !== "") {
        loginCliente(select.value); 
    } else {
        showToast('Por favor, selecione o seu nome na lista.', 'error');
    }
}

// LOGAR CLIENTE PELO TOQUE
async function cadastrarCliente() {
    const nomeInput = document.getElementById('cliente-nome');
    const saldoInput = document.getElementById('cliente-saldo-inicial');
    const nome = (nomeInput?.value || '').trim();
    const saldoInicial = Number(saldoInput?.value || 0);

    if (!nome) {
        showToast('Digite um nome para criar a sua conta.', 'error');
        return;
    }

    if (!Number.isFinite(saldoInicial) || saldoInicial < 0) {
        showToast('O saldo inicial não pode ser negativo.', 'error');
        return;
    }

    try {
        const response = await fetch('/api/clientes', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ nome, saldoInicial })
        });

        const data = await response.json();
        
        if (!response.ok) {
            throw new Error(data.erro || 'Não foi possível criar a conta.');
        }

        if (nomeInput) nomeInput.value = '';
        if (saldoInput) saldoInput.value = '0';

        clienteAtivoId = data.id;
        const welcomeScreen = document.getElementById('screen-welcome');
        const menuScreen = document.getElementById('screen-menu');
        if (welcomeScreen) welcomeScreen.classList.remove('active');
        if (menuScreen) menuScreen.classList.add('active');

        switchKioskSection('menu');
        carregarDadosGerais();
        showToast(`Conta criada com sucesso! Seu saldo atual é ${formatCurrency(data.saldo)}.`, 'success');
        await inicializarTotem();
    } catch (error) {
        handleApiError(error, 'Não foi possível criar a conta.');
    }
}

function loginCliente(id) {
    const clientId = Number(id);
    if (!Number.isFinite(clientId) || clientId <= 0) {
        showToast('Cliente inválido. Selecione alguém da lista.', 'error');
        return;
    }

    clienteAtivoId = clientId;

    const select = document.getElementById('select-cliente');
    if (select) {
        select.value = String(clientId);
    }

    const welcomeScreen = document.getElementById('screen-welcome');
    const menuScreen = document.getElementById('screen-menu');

    if (welcomeScreen) welcomeScreen.classList.remove('active');
    if (menuScreen) menuScreen.classList.add('active');

    switchKioskSection('menu');
    carregarDadosGerais();
    showToast('Acesso liberado. Escolha seus salgados!', 'success');
}

// DESLOGAR CLIENTE (SAIR)
function logoutCliente() {
    clienteAtivoId = null;
    
    document.getElementById('screen-menu').classList.remove('active');
    document.getElementById('screen-welcome').classList.add('active');
    
    inicializarTotem();
}

// ATUALIZAR CLIENTE PELO SELECT INTERNO DO PERFIL
function alterarCliente() {
    const select = document.getElementById('select-cliente');
    if (!select || !select.value) {
        showToast('Selecione um cliente antes de continuar.', 'error');
        return;
    }

    clienteAtivoId = parseInt(select.value, 10);
    carregarDadosGerais();
}

// ALTERNAR ENTRE ABAS DO CARDÁPIO / PEDIDOS / AUDITORIA
function switchKioskSection(sectionName) {
    activeKioskSection = sectionName;
    
    document.querySelectorAll('.menu-tab-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    
    let iconClass = 'fa-utensils';
    if (sectionName === 'history') iconClass = 'fa-receipt';
    if (sectionName === 'audit') iconClass = 'fa-chart-line';
    
    const activeBtn = Array.from(document.querySelectorAll('.menu-tab-btn')).find(btn => 
        btn.querySelector(`.${iconClass}`) !== null
    );
    if (activeBtn) activeBtn.classList.add('active');

    document.querySelectorAll('.kiosk-section').forEach(sec => {
        sec.classList.remove('active');
    });
    document.getElementById(`kiosk-section-${sectionName}`).classList.add('active');

    if (sectionName === 'menu') carregarCardapio();
    if (sectionName === 'history') carregarHistórico();
    if (sectionName === 'audit') carregarAuditorias();
}

// ALTERNAR ENTRE SUB-ABAS DE AUDITORIA (FINANCEIRO / ESTOQUE)
function switchAuditSubtab(tabName) {
    activeKioskAuditTab = tabName;
    
    document.querySelectorAll('.audit-subtab-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    document.getElementById(`subtab-${tabName}`).classList.add('active');

    document.querySelectorAll('.audit-timeline').forEach(timeline => {
        timeline.classList.remove('active');
    });
    document.getElementById(`kiosk-audit-${tabName}`).classList.add('active');
}

// CARREGAR TODOS OS DADOS DE SUPORTE
function carregarDadosGerais() {
    if (!clienteAtivoId) return;
    carregarPerfilCliente();
    carregarCardapio();
    carregarHistórico();
    carregarAuditorias();
}

// CARREGAR DADOS DO CONSUMIDOR ATIVO E EXIBIR SALDO GIGANTE
async function carregarPerfilCliente() {
    try {
        const response = await fetch(`/api/clientes/${clienteAtivoId}`);
        if (!response.ok) throw new Error('Cliente não encontrado.');
        const cliente = await response.json();

        // ADICIONADO O "OLÁ, " E EXIBIÇÃO COMPLETA DO NOME DO USUÁRIO
        safeText('totem-client-name', "Olá, " + cliente.nome);
        safeText('totem-client-balance', formatCurrencyValue(cliente.saldo));
    } catch (error) {
        handleApiError(error, 'Não foi possível carregar o perfil do cliente.');
    }
}

// AJUSTAR QUANTIDADE DE SALGADOS NO CARD (BOTÕES + E -)
function adjustQty(sabor, delta) {
    const input = document.getElementById(`qty-${sabor}`);
    if (!input) return;
    
    let val = parseInt(input.value) + delta;
    const min = parseInt(input.min) || 1;
    const max = parseInt(input.max) || 99;
    
    if (val < min) val = min;
    if (val > max) val = max;
    
    input.value = val;
}

// CARREGAR CARDÁPIO E VITRINE COM QUANTIDADE DO AUTO-ATENDIMENTO
async function carregarCardapio() {
    try {
        const response = await fetch('/api/relatorios/cardapio');
        if (!response.ok) throw new Error('Erro ao carregar cardápio.');
        const coxinhas = await response.json();
        
        const listContainer = document.getElementById('coxinhas-list');
        listContainer.innerHTML = '';

        if (!coxinhas || coxinhas.length === 0) {
            listContainer.innerHTML = '<div class="empty-state"><i class="fa-solid fa-cookie-bite"></i><p>Não há coxinhas disponíveis no cardápio no momento.</p></div>';
            return;
        }

        const strategySelect = document.getElementById('select-strategy');
        const isHappyHour = strategySelect?.value === 'HAPPY_HOUR';

        coxinhas.forEach(c => {
            const card = document.createElement('div');
            card.className = 'coxinha-totem-card';
            
            const estoqueClasse = c.estoque <= 2 ? 'low' : '';
            const estoqueTexto = c.estoque === 0 ? 'Esgotado' : `${c.estoque} un`;
            const precoUnitario = isHappyHour ? Number(c.precoBase || 0) * 0.9 : Number(c.precoBase || 0);
            const precoFormatado = formatCurrency(precoUnitario);
            const descontoLabel = isHappyHour ? 'Promo Happy Hour (-10%)' : 'Preço regular';
            
            let iconClass = 'fa-cookie-bite';
            if (c.sabor === 'FRANGO') iconClass = 'fa-drumstick-bite';
            if (c.sabor === 'CARNE') iconClass = 'fa-cow';
            if (c.sabor === 'QUEIJO') iconClass = 'fa-cheese';
            if (c.sabor === 'CATUPIRY') iconClass = 'fa-bowl-food';
            
            card.innerHTML = `
                <div class="coxinha-card-left">
                    <div class="coxinha-img-circle">
                        <i class="fa-solid ${iconClass}"></i>
                        <span class="totem-stock-badge ${estoqueClasse}">${estoqueTexto}</span>
                    </div>
                    <div class="coxinha-card-details">
                        <h3>Coxinha de ${c.sabor.toLowerCase()}</h3>
                        <div class="price">${precoFormatado}</div>
                        <small class="muted-label">${descontoLabel}</small>
                    </div>
                </div>
                
                <div class="coxinha-card-right">
                    <div class="quantity-control">
                        <button class="btn-qty" onclick="adjustQty('${c.sabor}', -1)" ${c.estoque === 0 ? 'disabled' : ''}>
                            <i class="fa-solid fa-minus"></i>
                        </button>
                        <input type="number" id="qty-${c.sabor}" class="qty-input" value="1" min="1" max="${c.estoque}" readonly>
                        <button class="btn-qty" onclick="adjustQty('${c.sabor}', 1)" ${c.estoque === 0 ? 'disabled' : ''}>
                            <i class="fa-solid fa-plus"></i>
                        </button>
                    </div>
                    <button class="btn-buy-totem" onclick="comprarCoxinha('${c.sabor}')" ${c.estoque === 0 ? 'disabled' : ''}>
                        <i class="fa-solid fa-cart-shopping"></i>
                    </button>
                </div>
            `;
            listContainer.appendChild(card);
        });
    } catch (error) {
        handleApiError(error, 'Não foi possível carregar o cardápio.');
        const listContainer = document.getElementById('coxinhas-list');
        if (listContainer) {
            listContainer.innerHTML = '<div class="empty-state"><i class="fa-solid fa-triangle-exclamation"></i><p>O cardápio não pôde ser carregado. Verifique a API.</p></div>';
        }
    }
}

// EFETUAR PEDIDO
async function comprarCoxinha(sabor) {
    const qtyInput = document.getElementById(`qty-${sabor}`);
    const quantidade = parseInt(qtyInput.value);
    const strategy = document.getElementById('select-strategy').value;

    if (isNaN(quantidade) || quantidade <= 0) {
        showToast('Quantidade inválida', 'error');
        return;
    }

    try {
        const response = await fetch('/api/pedidos', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                clienteId: clienteAtivoId,
                sabor: sabor,
                quantidade: quantidade,
                strategy: strategy
            })
        });

        const data = await response.json();
        
        if (!response.ok) {
            throw new Error(data.erro || 'Falha ao realizar compra.');
        }

        showToast(`Coxinha adicionada e paga com sucesso!`, 'success');
        carregarDadosGerais();
    } catch (error) {
        showToast(error.message, 'error');
    }
}

// HISTÓRICO DE CONSUMO ATIVO
async function carregarHistórico() {
    try {
        const container = document.getElementById('historico-pedidos-list');
        const emptyState = document.getElementById('empty-history');

        if (!clienteAtivoId) {
            if (container) container.innerHTML = '';
            if (emptyState) emptyState.style.display = 'flex';
            return;
        }

        if (!container || !emptyState) return;

        const response = await fetch(`/api/relatorios/pedidos/${clienteAtivoId}`);
        if (!response.ok) throw new Error('Falha ao obter histórico.');
        const pedidos = await response.json();
        
        container.innerHTML = '';
        const pedidosAtivos = pedidos.filter(p => p.status === 'COMPLETED');
        
        if (pedidosAtivos.length === 0) {
            emptyState.style.display = 'flex';
        } else {
            emptyState.style.display = 'none';
            pedidosAtivos.forEach(p => {
                const item = document.createElement('div');
                item.className = 'kiosk-order-card';
                const dataFormatada = new Date(p.dataHora).toLocaleString('pt-BR');
                
                const precoUnitarioCalculado = p.valorTotal / p.quantidade;
                const temDesconto = precoUnitarioCalculado < p.salgado.precoBase;
                const estrategiaTexto = temDesconto ? 'Happy Hour (10% OFF)' : 'Preço Normal';

                item.innerHTML = `
                    <div class="kiosk-order-info">
                        <span class="kiosk-order-title">Coxinha de ${p.salgado.sabor.toLowerCase()}</span>
                        <span class="kiosk-order-meta">${p.quantidade} un &bull; ${estrategiaTexto}</span>
                        <span class="kiosk-order-meta" style="font-size:0.6rem;"><i class="fa-regular fa-clock"></i> ${dataFormatada}</span>
                    </div>
                    <div class="kiosk-order-right">
                        <span class="kiosk-order-value">R$ ${p.valorTotal.toFixed(2)}</span>
                        <button class="btn-estorno-totem" onclick="estornarPedido(${p.id})">
                            <i class="fa-solid fa-rotate-left"></i> Estornar
                        </button>
                    </div>
                `;
                container.appendChild(item);
            });
        }
    } catch (error) {
        handleApiError(error, 'Não foi possível carregar o histórico de pedidos.');
    }
}

// ESTORNAR PEDIDO
async function estornarPedido(pedidoId) {
    if (!confirm('Deseja estornar esta coxinha? O crédito será devolvido.')) {
        return;
    }
    
    try {
        const response = await fetch(`/api/pedidos/${pedidoId}/estornar`, {
            method: 'POST'
        });
        
        const data = await response.json();
        if (!response.ok) {
            throw new Error(data.erro || 'Falha no estorno.');
        }
        
        showToast('Estornado! O saldo foi reajustado.', 'success');
        carregarDadosGerais();
    } catch (error) {
        showToast('Erro ao estornar: ' + error.message, 'error');
    }
}

// RECARREGAR SALDO (ACEITADOR DE CÉDULAS)
async function inserirNota(valor) {
    try {
        const response = await fetch('/api/clientes/recarregar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                clienteId: clienteAtivoId,
                valor: valor
            })
        });
        
        const data = await response.json();
        if (!response.ok) {
            throw new Error(data.erro || 'Falha ao recarregar.');
        }
        
        fecharRecargaModal();
        showToast(`Cédula de R$ ${valor} depositada com sucesso!`, 'success');
        carregarDadosGerais();
    } catch (error) {
        showToast('Erro ao aceitar nota: ' + error.message, 'error');
    }
}

// CARREGAR TIMELINES DE AUDITORIA
async function carregarAuditorias() {
    setApiStatus('Atualizando auditoria e movimentações...', 'online');
    
    // Auditoria Financeira
    try {
        const response = await fetch('/api/relatorios/movimentacoes-financeiras');
        if (!response.ok) throw new Error('Falha ao buscar auditoria financeira.');
        const logs = await response.json();
        
        const container = document.getElementById('kiosk-audit-financeiro');
        if (!container) return;
        container.innerHTML = '';
        
        logs.forEach(l => {
            const item = document.createElement('div');
            item.className = 'timeline-item';
            const dataFormatada = new Date(l.dataHora).toLocaleString('pt-BR');
            const sinal = l.valor >= 0 ? '+' : '';
            const corClasse = l.valor >= 0 ? 'text-success' : 'text-danger';
            
            item.innerHTML = `
                <div class="timeline-left">
                    <span class="timeline-title">${l.cliente.nome.split(' ')[0]} - ${l.tipo}</span>
                    <span class="timeline-time"><i class="fa-regular fa-clock"></i> ${dataFormatada}</span>
                </div>
                <span class="timeline-value ${corClasse}">${sinal} R$ ${l.valor.toFixed(2)}</span>
            `;
            container.appendChild(item);
        });
    } catch (error) {
        handleApiError(error, 'Não foi possível carregar a auditoria financeira.');
    }

    // Auditoria de Estoque
    try {
        const response = await fetch('/api/relatorios/movimentacoes-estoque');
        if (!response.ok) throw new Error('Falha ao buscar auditoria de estoque.');
        const logs = await response.json();
        
        const container = document.getElementById('kiosk-audit-estoque');
        if (!container) return;
        container.innerHTML = '';
        
        logs.forEach(l => {
            const item = document.createElement('div');
            item.className = 'timeline-item';
            const dataFormatada = new Date(l.dataHora).toLocaleString('pt-BR');
            const sinal = l.quantidade >= 0 ? '+' : '';
            const corClasse = l.quantidade >= 0 ? 'text-success' : 'text-danger';
            
            item.innerHTML = `
                <div class="timeline-left">
                    <span class="timeline-title">Coxinha de ${l.salgado.sabor.toLowerCase()}</span>
                    <span class="timeline-time"><i class="fa-regular fa-clock"></i> ${dataFormatada} (${l.tipo.toLowerCase().replace('_', ' ')})</span>
                </div>
                <span class="timeline-value ${corClasse}">${sinal}${l.quantidade} un</span>
            `;
            container.appendChild(item);
        });
    } catch (error) {
        handleApiError(error, 'Não foi possível carregar a movimentação de estoque.');
    }
}

// MODAL DE DEPOSITADOR DE NOTAS
function abrirRecargaModal() {
    document.getElementById('recarga-modal').style.display = 'flex';
}
function fecharRecargaModal() {
    document.getElementById('recarga-modal').style.display = 'none';
}

// TOASTS
function showToast(message, type = 'info') {
    const toast = document.getElementById('toast');
    if (!toast) return;

    toast.className = `toast show ${type}`;

    const icon = type === 'success' ? 'fa-circle-check' : (type === 'error' ? 'fa-triangle-exclamation' : 'fa-circle-info');
    toast.innerHTML = `<i class="fa-solid ${icon}"></i> <span>${message}</span>`;

    setTimeout(() => {
        toast.className = toast.className.replace('show', '');
    }, 3500);
}