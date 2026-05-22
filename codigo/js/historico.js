const API_URL = 'http://localhost:8080';

const clienteId = sessionStorage.getItem('clienteId');
const clienteNome = sessionStorage.getItem('clienteNome');

if (!clienteId) {
    alert('Você precisa estar logado!');
    window.location.href = 'login.html';
}

document.getElementById('nomeCliente').innerText = `Olá, ${clienteNome || 'Cliente'}!`;

async function carregarReservas() {
    try {
        const response = await fetch(`${API_URL}/alugueis?clienteId=${clienteId}`);
        const alugueis = await response.json();

        const tbody = document.getElementById('corpoTabela');
        tbody.innerHTML = '';

        if (alugueis.length === 0) {
            document.getElementById('semDados').style.display = 'block';
            return;
        }

        alugueis.forEach(aluguel => {
            const entrada = new Date(aluguel.dataEntrada);
            const saida = new Date(aluguel.dataSaida);

            const formatarData = (data) => data.toLocaleDateString('pt-BR') + ' ' + data.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });

            const tipoQuarto = getTipoQuarto(aluguel.quarto);
            const residencia = aluguel.quarto?.residencia?.endereco || '---';

            tbody.innerHTML += `
                <tr>
                    <td>${aluguel.id}</td>
                    <td>${residencia}</td>
                    <td>${tipoQuarto}</td>
                    <td>${formatarData(entrada)}</td>
                    <td>${formatarData(saida)}</td>
                    <td>${aluguel.qtdDiarias}</td>
                    <td>R$ ${aluguel.valorFinal?.toFixed(2) || '---'}</td>
                </tr>
            `;
        });

    } catch (error) {
        console.error('Erro ao carregar reservas:', error);
    }
}

function getTipoQuarto(quarto) {
    if (!quarto) return '---';
    if (quarto.quantidadeCamas !== undefined) return `Individual (${quarto.quantidadeCamas} cama(s))`;
    if (quarto.tipoCama !== undefined) return `Duplo (${quarto.tipoCama})`;
    if (quarto.capacidadeMaxima !== undefined) return `Família (até ${quarto.capacidadeMaxima} hóspedes)`;
    return 'Quarto';
}

document.addEventListener('DOMContentLoaded', carregarReservas);