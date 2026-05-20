const API_URL = 'http://localhost:8080';

async function carregarResidencias() {
    try {
        const response = await fetch(`${API_URL}/residencias`);
        const residencias = await response.json();

        const container = document.querySelector('.row.g-4');
        container.innerHTML = '';

        if (residencias.length === 0) {
            container.innerHTML = '<p class="text-center text-muted">Nenhuma residência cadastrada ainda.</p>';
            return;
        }

        residencias.forEach(residencia => {
            const col = document.createElement('div');
            col.className = 'col-md-4';
            col.innerHTML = `
                <div class="residencia-card">
                    <img src="https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?w=500" 
                         class="residencia-img" alt="${residencia.nome || 'Residência'}">
                    <div class="residencia-content text-start">
                        <div class="residencia-local">${residencia.bairro || ''}, Maraú</div>
                        <h5 class="residencia-title">${residencia.endereco}</h5>
                        <a href="detalhes.html?id=${residencia.id}" class="btn-card">Ver Quartos</a>
                    </div>
                </div>
            `;
            container.appendChild(col);
        });

    } catch (error) {
        console.error('Erro ao carregar residências:', error);
    }
}

function filtrarResidencias() {
    const termoBusca = document.querySelector('.search-input').value.toLowerCase();
    const cards = document.querySelectorAll('.col-md-4');

    cards.forEach(card => {
        const titulo = card.querySelector('.residencia-title')?.innerText.toLowerCase();
        const local = card.querySelector('.residencia-local')?.innerText.toLowerCase();

        if (titulo?.includes(termoBusca) || local?.includes(termoBusca)) {
            card.style.display = 'block';
        } else {
            card.style.display = 'none';
        }
    });
}

document.querySelector('.btn-search').addEventListener('click', filtrarResidencias);
document.addEventListener('DOMContentLoaded', carregarResidencias);