const API_URL = 'http://localhost:8080';

const urlParams = new URLSearchParams(window.location.search);
const idResidencia = urlParams.get('id');

async function carregarResidencia() {
    try {
        const response = await fetch(`${API_URL}/residencias/${idResidencia}`);
        const residencia = await response.json();

        document.getElementById('nomeResidencia').innerText = residencia.endereco;
        document.getElementById('enderecoResidencia').innerHTML =
            `<i class="bi bi-geo-alt"></i> ${residencia.bairro || ''} - CEP: ${residencia.cep || ''}`;
        document.getElementById('descricaoResidencia').innerText = residencia.email || '';
        document.getElementById('fotoPrincipal').src =
            residencia.fotoUrl || 'https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?w=800';

        await carregarQuartos();

    } catch (error) {
        console.error('Erro ao carregar residência:', error);
        document.getElementById('nomeResidencia').innerText = 'Erro ao carregar residência';
    }
}

async function carregarQuartos() {
    try {
        const response = await fetch(`${API_URL}/quartos?residenciaId=${idResidencia}`);
        const quartos = await response.json();

        const listaQuartos = document.getElementById('listaQuartos');
        listaQuartos.innerHTML = '';

        if (quartos.length === 0) {
            listaQuartos.innerHTML = '<p class="text-muted">Nenhum quarto disponível.</p>';
            return;
        }

        quartos.forEach(quarto => {
            const tipo = getTipoQuarto(quarto);
            const tipoParam = getTipoParam(quarto);
            const tags = getTags(quarto);

            const card = document.createElement('div');
            card.className = 'quarto-card shadow-sm';
            card.innerHTML = `
                <div class="row align-items-center">
                    <div class="col-8">
                        <span class="tipo-quarto">${tipo}</span><br>
                        ${tags}
                    </div>
                    <div class="col-4 text-end">
                        <span class="valor-base">R$ ${quarto.valorBase?.toFixed(2)}</span><br>
                        <a href="reserva.html?residenciaId=${idResidencia}&quartoId=${quarto.id}&preco=${quarto.valorBase}&tipo=${tipoParam}" 
                           class="btn-reservar-quarto btn-reserva">Escolher</a>
                    </div>
                </div>
            `;
            listaQuartos.appendChild(card);
        });

    } catch (error) {
        console.error('Erro ao carregar quartos:', error);
    }
}

function getTipoQuarto(quarto) {
    if (quarto.quantidadeCamas !== undefined) return 'Quarto Individual';
    if (quarto.tipoCama !== undefined) return `Quarto Duplo (${quarto.tipoCama})`;
    if (quarto.capacidadeMaxima !== undefined) return 'Quarto Família';
    return 'Quarto';
}

function getTipoParam(quarto) {
    if (quarto.quantidadeCamas !== undefined) return 'individual';
    if (quarto.tipoCama !== undefined) return 'duplo';
    if (quarto.capacidadeMaxima !== undefined) return 'familia';
    return 'individual';
}

function getTags(quarto) {
    let tags = '';
    if (quarto.possuiAR) tags += '<span class="tag-item">Ar-condicionado</span>';
    if (quarto.possuiHidro) tags += '<span class="tag-item">Hidromassagem</span>';
    if (quarto.possuiBerco) tags += '<span class="tag-item">Berço disponível</span>';
    return tags;
}

document.addEventListener('DOMContentLoaded', carregarResidencia);