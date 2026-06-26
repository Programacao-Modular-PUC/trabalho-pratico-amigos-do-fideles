const API_URL = 'http://localhost:8080';

const urlParams = new URLSearchParams(window.location.search);
const residenciaId = urlParams.get('residenciaId');
const quartoId = urlParams.get('quartoId');
const precoDiaria = parseFloat(urlParams.get('preco')) || 0;
const tipoQuarto = urlParams.get('tipo') || 'individual';

let quartoAtual = null;

async function carregarResumo() {
    try {
        const [residenciaRes, quartoRes] = await Promise.all([
            fetch(`${API_URL}/residencias/${residenciaId}`),
            fetch(`${API_URL}/quartos/${quartoId}`)
        ]);

        const residencia = await residenciaRes.json();
        quartoAtual = await quartoRes.json();

        document.getElementById('resumoResidencia').innerText = residencia.endereco;
        document.getElementById('resumoQuarto').innerText = getTipoQuarto(quartoAtual);
        document.getElementById('resumoPreco').innerText = `R$ ${precoDiaria.toFixed(2)}`;

        if (tipoQuarto === 'familia') {
            document.getElementById('campoHospedes').style.display = 'block';
            document.getElementById('qtdHospedes').addEventListener('input', calcularTotal);
        }

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
    const horaEntrada = document.getElementById('horaEntrada').value || '12:00';
    const dataSaida = document.getElementById('dataSaida').value;
    const horaSaida = document.getElementById('horaSaida').value || '12:00';

    if (dataEntrada && dataSaida) {
        const entrada = new Date(`${dataEntrada}T${horaEntrada}:00`);
        const saida = new Date(`${dataSaida}T${horaSaida}:00`);

        let diarias = Math.floor((saida - entrada) / (1000 * 3600 * 24));

        const [hEntrada] = horaEntrada.split(':').map(Number);
        if (hEntrada > 12) diarias++;

        const [hSaida] = horaSaida.split(':').map(Number);
        if (hSaida > 12) diarias++;

        if (diarias > 0) {
            let valorDiaria = precoDiaria;

            if (tipoQuarto === 'familia') {
                const qtdHospedes = parseInt(document.getElementById('qtdHospedes').value) || 1;
                valorDiaria = calcularDiariaFamilia(precoDiaria, qtdHospedes);
            }

            const total = diarias * valorDiaria;
            document.getElementById('totalExibido').innerText = `R$ ${total.toFixed(2)}`;
            document.getElementById('legendaDias').innerText = `Cálculo para ${diarias} diária(s)`;
        } else {
            document.getElementById('totalExibido').innerText = 'R$ 0,00';
            document.getElementById('legendaDias').innerText = 'A data de saída deve ser maior que a de entrada';
        }
    }
}

function calcularDiariaFamilia(valorBase, qtdHospedes) {
    const percentualAdicional = quartoAtual?.percentualAdicional || 15;
    const valorPorHospede = quartoAtual?.valorPorHospede || 15;
    const capacidadeMaxima = quartoAtual?.capacidadeMaxima || 7;

    const valorComPercentual = valorBase * (1 + (percentualAdicional / 100) * qtdHospedes);
    const totalHospedes = qtdHospedes * valorPorHospede;
    let total = valorComPercentual + totalHospedes;

    const desconto = calcularDesconto(qtdHospedes, capacidadeMaxima);
    
    const infoDesconto = document.getElementById('infoDesconto');
    const textoDesconto = document.getElementById('textoDesconto');
    if (desconto > 0) {
        infoDesconto.style.display = 'block';
        textoDesconto.innerText = `Desconto de grupo aplicado: ${desconto * 100}%`;
    } else {
        infoDesconto.style.display = 'none';
    }

    total = total * (1 - desconto);
    return total;
}

function calcularDesconto(qtdHospedes, capacidadeMaxima) {
    const proporcao = qtdHospedes / capacidadeMaxima;
    if (qtdHospedes < 3) return 0.0;
    if (proporcao < 0.5) return 0.05;
    if (proporcao < 0.75) return 0.10;
    if (proporcao < 1.0) return 0.15;
    return 0.20;
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

    let qtdHospedes = null;
    if (tipoQuarto === 'familia') {
        qtdHospedes = parseInt(document.getElementById('qtdHospedes').value);
        const capacidadeMaxima = quartoAtual?.capacidadeMaxima || 0;

        if (!qtdHospedes || qtdHospedes < 1) {
            alert('Por favor, informe a quantidade de hóspedes!');
            return;
        }
        if (qtdHospedes > capacidadeMaxima) {
            alert(`Este quarto comporta no máximo ${capacidadeMaxima} hóspedes!`);
            return;
        }
    }

    const aluguel = {
        dataEntrada: `${dataEntrada}T${horaEntrada}:00`,
        dataSaida: `${dataSaida}T${horaSaida}:00`,
        cliente: { id: parseInt(clienteId) },
        quarto: { id: parseInt(quartoId), tipo: tipoQuarto },
        qtdHospedes: qtdHospedes
    };

    try {
        const response = await fetch(`${API_URL}/alugueis`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify(aluguel)
        });

        if (response.ok) {
            const aluguelCriado = await response.json();
            window.location.href = `recibo.html?aluguelId=${aluguelCriado.id}`;
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