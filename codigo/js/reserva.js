const API_URL = 'http://localhost:8080';

const urlParams = new URLSearchParams(window.location.search);
const residenciaId = urlParams.get('residenciaId');
const quartoId = urlParams.get('quartoId');
const precoDiaria = parseFloat(urlParams.get('preco')) || 0;
const tipoQuarto = urlParams.get('tipo') || 'individual';

async function carregarResumo() {
    try {
        const [residenciaRes, quartoRes] = await Promise.all([
            fetch(`${API_URL}/residencias/${residenciaId}`),
            fetch(`${API_URL}/quartos/${quartoId}`)
        ]);

        const residencia = await residenciaRes.json();
        const quarto = await quartoRes.json();

        document.getElementById('resumoResidencia').innerText = residencia.endereco;
        document.getElementById('resumoQuarto').innerText = getTipoQuarto(quarto);
        document.getElementById('resumoPreco').innerText = `R$ ${precoDiaria.toFixed(2)}`;

    } catch (error) {
        console.error('Erro ao carregar resumo:', error);
    }
}

function getTipoQuarto(quarto) {
    if (quarto.quantidadeCamas !== undefined) return `Quarto Individual (${quarto.quantidadeCamas} cama(s))`;
    if (quarto.tipoCama !== undefined) return `Quarto Duplo (${quarto.tipoCama})`;
    if (quarto.capacidadeMaxima !== undefined) return `Quarto Família (até ${quarto.capacidadeMaxima} hóspedes)`;
    return 'Quarto';
}

function calcularTotal() {
    const dataEntrada = document.getElementById('dataEntrada').value;
    const horaEntrada = document.getElementById('horaEntrada').value;
    const dataSaida = document.getElementById('dataSaida').value;
    const horaSaida = document.getElementById('horaSaida').value;

    if (dataEntrada && dataSaida) {
        const entrada = new Date(`${dataEntrada}T${horaEntrada || '12:00'}`);
        const saida = new Date(`${dataSaida}T${horaSaida || '12:00'}`);
        const diferencaDias = (saida - entrada) / (1000 * 3600 * 24);

        if (diferencaDias > 0) {
            const total = diferencaDias * precoDiaria;
            document.getElementById('totalExibido').innerText = `R$ ${total.toFixed(2)}`;
            document.getElementById('legendaDias').innerText = `Cálculo para ${Math.ceil(diferencaDias)} diária(s)`;
        } else {
            document.getElementById('totalExibido').innerText = 'R$ 0,00';
            document.getElementById('legendaDias').innerText = 'A data de saída deve ser maior que a de entrada';
        }
    }
}

async function confirmarAluguel() {
    const dataEntrada = document.getElementById('dataEntrada').value;
    const horaEntrada = document.getElementById('horaEntrada').value || '12:00';
    const dataSaida = document.getElementById('dataSaida').value;
    const horaSaida = document.getElementById('horaSaida').value || '12:00';

    if (!dataEntrada || !dataSaida) {
        alert('Por favor, preencha as datas de entrada e saída!');
        return;
    }

    const clienteId = sessionStorage.getItem('clienteId');
    if (!clienteId) {
        alert('Você precisa estar logado para fazer uma reserva!');
        window.location.href = 'login.html';
        return;
    }

    const aluguel = {
        dataEntrada: `${dataEntrada}T${horaEntrada}:00`,
        dataSaida: `${dataSaida}T${horaSaida}:00`,
        cliente: { id: parseInt(clienteId) },
        quarto: { id: parseInt(quartoId), tipo: tipoQuarto }
    };

    try {
        const response = await fetch(`${API_URL}/alugueis`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify(aluguel)
        });

        if (response.ok) {
            alert('Reserva confirmada com sucesso!');
            window.location.href = 'index.html';
        } else {
            const erro = await response.json();
            alert(`Erro: ${erro.message || 'Não foi possível confirmar a reserva'}`);
        }
    } catch (error) {
        console.error('Erro ao confirmar aluguel:', error);
        alert('Erro ao conectar com o servidor!');
    }
}

document.getElementById('dataEntrada').addEventListener('change', calcularTotal);
document.getElementById('dataSaida').addEventListener('change', calcularTotal);
document.addEventListener('DOMContentLoaded', carregarResumo);