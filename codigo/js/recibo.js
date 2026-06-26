const API_URL = 'http://localhost:8080';

const urlParams = new URLSearchParams(window.location.search);
const aluguelId = urlParams.get('aluguelId');

function formatarData(dataStr) {
    const data = new Date(dataStr);
    return data.toLocaleDateString('pt-BR') + ' às ' + data.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
}

function getTipoQuarto(quarto) {
    if (!quarto) return '---';
    if (quarto.quantidadeCamas !== undefined) return `Quarto Individual (${quarto.quantidadeCamas} cama(s))`;
    if (quarto.tipoCama !== undefined) return `Quarto Duplo (${quarto.tipoCama})`;
    if (quarto.capacidadeMaxima !== undefined) return `Quarto Família (até ${quarto.capacidadeMaxima} hóspedes)`;
    return 'Quarto';
}

async function carregarRecibo() {
    try {
        const res = await fetch(`${API_URL}/alugueis/${aluguelId}`);
        const aluguel = await res.json();

        const agora = new Date();
        document.getElementById('codigoReserva').innerText = `#FID-${agora.getFullYear()}-${String(aluguel.id).padStart(4, '0')}`;
        document.getElementById('dataEmissao').innerText = `Emitido em: ${formatarData(agora.toISOString())}`;

        document.getElementById('enderecoResidencia').innerText = aluguel.quarto?.residencia?.endereco || '---';
        document.getElementById('tipoQuarto').innerText = getTipoQuarto(aluguel.quarto);
        document.getElementById('valorDiaria').innerText = `R$ ${aluguel.quarto?.valorBase?.toFixed(2) || '---'}`;

        document.getElementById('nomeCliente').innerText = aluguel.cliente?.nome || '---';
        document.getElementById('telefoneCliente').innerText = aluguel.cliente?.telefone || '---';
        document.getElementById('emailCliente').innerText = aluguel.cliente?.email || '---';

        document.getElementById('dataEntrada').innerText = formatarData(aluguel.dataEntrada);
        document.getElementById('dataSaida').innerText = formatarData(aluguel.dataSaida);
        document.getElementById('qtdDiarias').innerText = aluguel.qtdDiarias;

        const total = aluguel.valorFinal?.toFixed(2) || '0.00';
        document.getElementById('descricaoDiaria').innerText = `R$ ${aluguel.quarto?.valorBase?.toFixed(2)} × ${aluguel.qtdDiarias} diária(s)`;
        document.getElementById('subtotal').innerText = `R$ ${total}`;
        document.getElementById('totalPagar').innerText = `R$ ${total}`;
        document.getElementById('totalDestaque').innerText = `R$ ${total}`;

    } catch (error) {
        console.error('Erro ao carregar recibo:', error);
    }
}

document.addEventListener('DOMContentLoaded', carregarRecibo);