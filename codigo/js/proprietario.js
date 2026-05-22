const API_URL = 'http://localhost:8080';

let residencias = [];
let quartos = [];
let residenciaEditandoId = null;

async function carregarResidencias() {
    try {
        const res = await fetch(`${API_URL}/residencias`);
        residencias = await res.json();
        popularSelectResidencias();
        renderTabelaResidencias();
    } catch (error) {
        console.error('Erro ao carregar residências:', error);
    }
}

async function carregarQuartos() {
    try {
        const res = await fetch(`${API_URL}/quartos`);
        quartos = await res.json();
        renderTabelaQuartos();
    } catch (error) {
        console.error('Erro ao carregar quartos:', error);
    }
}

function popularSelectResidencias() {
    const select = document.getElementById('fResidenciaId');
    select.innerHTML = '<option value="">Selecione a residência...</option>';
    residencias.forEach(r => {
        select.innerHTML += `<option value="${r.id}">${r.endereco} - ${r.bairro || ''}</option>`;
    });
}


function renderTabelaResidencias() {
    const tbody = document.getElementById('corpoTabelaResidencias');
    tbody.innerHTML = '';

    if (residencias.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">Nenhuma residência cadastrada.</td></tr>';
        return;
    }

    residencias.forEach(r => {
        tbody.innerHTML += `
            <tr>
                <td>${r.id}</td>
                <td>${r.endereco}, ${r.numero || ''}</td>
                <td>${r.bairro || '---'}</td>
                <td>${r.telefone || '---'}</td>
                <td>
                    <button class="btn-acao btn-editar" onclick="abrirEdicaoResidencia(${r.id})">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button class="btn-acao btn-del" onclick="deletarResidencia(${r.id})">
                        <i class="bi bi-trash3"></i>
                    </button>
                </td>
            </tr>`;
    });
}

function abrirEdicaoResidencia(id) {
    const r = residencias.find(r => r.id === id);
    if (!r) return;

    residenciaEditandoId = id;
    document.getElementById('modalResidenciaTitulo').innerText = 'Editar Residência';
    document.getElementById('rEndereco').value = r.endereco || '';
    document.getElementById('rNumero').value = r.numero || '';
    document.getElementById('rBairro').value = r.bairro || '';
    document.getElementById('rCep').value = r.cep || '';
    document.getElementById('rTelefone').value = r.telefone || '';
    document.getElementById('rEmail').value = r.email || '';
    document.getElementById('rFotoUrl').value = r.fotoUrl || '';
    document.getElementById('modalResidencia').classList.add('ativo');
}

async function salvarResidencia() {
    const body = {
        endereco: document.getElementById('rEndereco').value,
        numero: document.getElementById('rNumero').value,
        bairro: document.getElementById('rBairro').value,
        cep: document.getElementById('rCep').value,
        telefone: document.getElementById('rTelefone').value,
        email: document.getElementById('rEmail').value,
        fotoUrl: document.getElementById('rFotoUrl').value
    };

    if (!body.endereco || !body.bairro) {
        alert('Preencha pelo menos o endereço e o bairro!');
        return;
    }

    try {
        let res;
        if (residenciaEditandoId) {
            res = await fetch(`${API_URL}/residencias/${residenciaEditandoId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(body)
            });
        } else {
            res = await fetch(`${API_URL}/residencias`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(body)
            });
        }

        if (res.ok) {
            mostrarToast(residenciaEditandoId ? 'Residência atualizada!' : 'Residência cadastrada!');
            fecharModalResidencia();
            await carregarResidencias();
        } else {
            alert('Erro ao salvar residência!');
        }
    } catch (error) {
        console.error('Erro:', error);
    }
}

async function deletarResidencia(id) {
    if (!confirm('Deseja excluir esta residência?')) return;
    await fetch(`${API_URL}/residencias/${id}`, { method: 'DELETE' });
    mostrarToast('Residência removida!', 'erro');
    await carregarResidencias();
}


function renderTabelaQuartos() {
    const tbody = document.getElementById('corpoTabelaQuartos');
    tbody.innerHTML = '';

    if (quartos.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted">Nenhum quarto cadastrado.</td></tr>';
        return;
    }

    quartos.forEach(q => {
        const tipo = getTipoQuarto(q);
        const residencia = residencias.find(r => r.id === q.residencia?.id);
        tbody.innerHTML += `
            <tr>
                <td>${q.id}</td>
                <td>${tipo}</td>
                <td>${residencia ? residencia.endereco : '---'}</td>
                <td>R$ ${q.valorBase?.toFixed(2)}</td>
                <td>
                    ${q.possuiAR ? '<span class="tag-item">AR</span>' : ''}
                    ${q.possuiHidro ? '<span class="tag-item">Hidro</span>' : ''}
                </td>
                <td>
                    <button class="btn-acao btn-del" onclick="deletarQuarto(${q.id})">
                        <i class="bi bi-trash3"></i>
                    </button>
                </td>
            </tr>`;
    });
}

function getTipoQuarto(quarto) {
    if (quarto.quantidadeCamas !== undefined) return `Individual (${quarto.quantidadeCamas} cama(s))`;
    if (quarto.tipoCama !== undefined) return `Duplo (${quarto.tipoCama})`;
    if (quarto.capacidadeMaxima !== undefined) return `Família (até ${quarto.capacidadeMaxima} hóspedes)`;
    return 'Quarto';
}

function mostrarCamposTipo() {
    const tipo = document.getElementById('fTipo').value;
    document.getElementById('camposIndividual').style.display = tipo === 'individual' ? 'block' : 'none';
    document.getElementById('camposDuplo').style.display = tipo === 'duplo' ? 'block' : 'none';
    document.getElementById('camposFamilia').style.display = tipo === 'familia' ? 'block' : 'none';
}

async function salvarQuarto() {
    const tipo = document.getElementById('fTipo').value;
    const residenciaId = document.getElementById('fResidenciaId').value;
    const valorBase = document.getElementById('fValorBase').value;
    const possuiAR = document.getElementById('fAR').checked;
    const possuiHidro = document.getElementById('fHidro').checked;

    if (!tipo || !residenciaId || !valorBase) {
        alert('Preencha todos os campos obrigatórios!');
        return;
    }

    let endpoint = '';
    let body = { residenciaId, valorBase: parseFloat(valorBase), possuiAR, possuiHidro };

    if (tipo === 'individual') {
        endpoint = '/quartos/individual';
        body.quantidadeCamas = parseInt(document.getElementById('fQuantidadeCamas').value);
        body.valorAdicionalPorCama = parseFloat(document.getElementById('fValorAdicionalCama').value) || 0;
    } else if (tipo === 'duplo') {
        endpoint = '/quartos/duplo';
        body.tipoCama = document.getElementById('fTipoCama').value;
        body.possuiBerco = document.getElementById('fBerco').checked;
        body.taxaBerco = parseFloat(document.getElementById('fTaxaBerco').value) || 0;
        body.adicionalConforto = parseFloat(document.getElementById('fAdicionalConforto').value) || 0;
    } else if (tipo === 'familia') {
        endpoint = '/quartos/familia';
        body.capacidadeMaxima = parseInt(document.getElementById('fCapacidadeMaxima').value);
        body.quantidadeAmbientes = parseInt(document.getElementById('fQuantidadeAmbientes').value);
        body.valorPorHospede = parseFloat(document.getElementById('fValorPorHospede').value) || 0;
        body.percentualAdicional = parseFloat(document.getElementById('fPercentualAdicional').value) || 0;
    }

    try {
        const res = await fetch(`${API_URL}${endpoint}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });

        if (res.ok) {
            mostrarToast('Quarto cadastrado com sucesso!');
            fecharModalQuarto();
            await carregarQuartos();
        } else {
            const erro = await res.json();
            alert(`Erro: ${erro.erro || 'Não foi possível cadastrar o quarto'}`);
        }
    } catch (error) {
        console.error('Erro:', error);
    }
}

async function deletarQuarto(id) {
    if (!confirm('Deseja excluir este quarto?')) return;
    await fetch(`${API_URL}/quartos/${id}`, { method: 'DELETE' });
    mostrarToast('Quarto removido!', 'erro');
    await carregarQuartos();
}

// ========== MODAIS ==========

function abrirModalResidencia() {
    residenciaEditandoId = null;
    document.getElementById('modalResidenciaTitulo').innerText = 'Nova Residência';
    document.getElementById('rEndereco').value = '';
    document.getElementById('rNumero').value = '';
    document.getElementById('rBairro').value = '';
    document.getElementById('rCep').value = '';
    document.getElementById('rTelefone').value = '';
    document.getElementById('rEmail').value = '';
    document.getElementById('rFotoUrl').value = '';
    document.getElementById('modalResidencia').classList.add('ativo');
}

function fecharModalResidencia() {
    document.getElementById('modalResidencia').classList.remove('ativo');
    residenciaEditandoId = null;
}

function abrirModalQuarto() {
    document.getElementById('modalQuarto').classList.add('ativo');
}

function fecharModalQuarto() {
    document.getElementById('modalQuarto').classList.remove('ativo');
}

function mostrarToast(msg, tipo = 'ok') {
    const t = document.getElementById('toast');
    t.textContent = msg;
    t.className = 'toast-custom ativo ' + (tipo === 'erro' ? 'toast-erro' : 'toast-ok');
    setTimeout(() => t.className = 'toast-custom', 3000);
}

document.addEventListener('DOMContentLoaded', () => {
    carregarResidencias();
    carregarQuartos();
});