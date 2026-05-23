const API_URL = 'http://localhost:8080';

document.getElementById('form-login').addEventListener('submit', async function (e) {
    e.preventDefault();
    const tipo = document.querySelector('input[name="tipoCadastro"]:checked')?.value || 'cliente';
    const btn = document.getElementById('btn-login');
    const erro = document.getElementById('msg-erro');
    erro.classList.add('d-none');
    btn.disabled = true;
    btn.textContent = 'Entrando...';

    const endpoint = tipo === 'proprietario' ? '/proprietarios/login' : '/auth/login';

    try {
        const res = await fetch(`${API_URL}${endpoint}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify({
                email: document.getElementById('email').value,
                senha: document.getElementById('senha').value
            })
        });

        const data = await res.json();

        if (!res.ok) {
            throw new Error(data.erro || 'E-mail ou senha inválidos.');
        }

        if (tipo === 'proprietario') {
            sessionStorage.setItem('proprietarioNome', data.nome);
            sessionStorage.setItem('proprietarioId', data.id);
            sessionStorage.setItem('tipo', 'proprietario');
            window.location.href = 'proprietario.html';
        } else {
            sessionStorage.setItem('clienteNome', data.nome);
            sessionStorage.setItem('clienteId', data.clienteId);
            sessionStorage.setItem('tipo', 'cliente');
            window.location.href = 'index.html';
        }

    } catch (err) {
        erro.textContent = err.message === 'Failed to fetch'
            ? 'Não foi possível conectar ao servidor.'
            : err.message;
        erro.classList.remove('d-none');
        btn.disabled = false;
        btn.textContent = 'Acessar Hospedaria';
    }
});