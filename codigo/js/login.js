const API_URL = 'http://localhost:8080';

document.getElementById('form-login').addEventListener('submit', async function (e) {
    e.preventDefault();
    const btn = document.getElementById('btn-login');
    const erro = document.getElementById('msg-erro');
    erro.classList.add('d-none');
    btn.disabled = true;
    btn.textContent = 'Entrando...';

    try {
        const res = await fetch(`${API_URL}/auth/login`, {
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

        sessionStorage.setItem('clienteNome', data.nome);
        sessionStorage.setItem('clienteId', data.clienteId);
        window.location.href = 'index.html';

    } catch (err) {
        if (err.message === 'Failed to fetch') {
            erro.textContent = 'Não foi possível conectar ao servidor. Verifique se o backend está rodando.';
        } else {
            erro.textContent = err.message;
        }
        erro.classList.remove('d-none');
        btn.disabled = false;
        btn.textContent = 'Acessar Hospedaria';
    }
});