const API_URL = 'http://localhost:8080';

document.getElementById('cpf').addEventListener('input', function (e) {
    var v = e.target.value.replace(/\D/g, '');
    v = v.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4");
    e.target.value = v;
});

document.getElementById('cep').addEventListener('input', function (e) {
    var v = e.target.value.replace(/\D/g, '');
    v = v.replace(/(\d{5})(\d{3})/, "$1-$2");
    e.target.value = v;
});

document.getElementById('telefone').addEventListener('input', function (e) {
    var v = e.target.value.replace(/\D/g, '');
    v = v.replace(/(\d{2})(\d{5})(\d{4})/, "($1) $2-$3");
    e.target.value = v;
});


document.getElementById('form-cadastro').addEventListener('submit', async function (e) {
    e.preventDefault();
    const tipo = document.querySelector('input[name="tipoCadastro"]:checked').value;
    const btn = document.getElementById('btn-cadastro');
    const erro = document.getElementById('msg-erro');
    const sucesso = document.getElementById('msg-sucesso');
    erro.classList.add('d-none');
    sucesso.classList.add('d-none');

    const senha = document.getElementById('senha').value;
    const confirmar = document.getElementById('confirmar-senha').value;

    if (senha !== confirmar) {
        erro.textContent = 'As senhas não coincidem.';
        erro.classList.remove('d-none');
        return;
    }
    if (senha.length < 6) {
        erro.textContent = 'A senha deve ter no mínimo 6 caracteres.';
        erro.classList.remove('d-none');
        return;
    }

    btn.disabled = true;
    btn.textContent = 'Cadastrando...';

    const body = {
        nome: document.getElementById('nome').value,
        cpf: document.getElementById('cpf').value,
        email: document.getElementById('email').value,
        telefone: document.getElementById('telefone').value,
        endereco: `${document.getElementById('rua').value}, ${document.getElementById('numero').value} - ${document.getElementById('bairro').value}, CEP ${document.getElementById('cep').value}`,
        senha: senha
    };

    const endpoint = tipo === 'proprietario' ? '/proprietarios/cadastro' : '/auth/cadastro';

    try {
        const res = await fetch(`${API_URL}${endpoint}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify(body)
        });

        const data = await res.json();

        if (!res.ok) {
            throw new Error(data.erro || 'Erro ao realizar cadastro.');
        }

        if (tipo === 'proprietario') {
            sessionStorage.setItem('proprietarioNome', data.nome);
            sessionStorage.setItem('proprietarioId', data.id);
            sessionStorage.setItem('tipo', 'proprietario');
        } else {
            sessionStorage.setItem('clienteNome', data.nome);
            sessionStorage.setItem('clienteId', data.clienteId);
            sessionStorage.setItem('tipo', 'cliente');
        }

        sucesso.textContent = `Bem-vindo, ${data.nome}! Redirecionando...`;
        sucesso.classList.remove('d-none');

        setTimeout(() => {
            if (tipo === 'proprietario') {
                window.location.href = 'proprietario.html';
            } else {
                window.location.href = 'index.html';
            }
        }, 1500);

    } catch (err) {
        erro.textContent = err.message === 'Failed to fetch'
            ? 'Não foi possível conectar ao servidor.'
            : err.message;
        erro.classList.remove('d-none');
        btn.disabled = false;
        btn.textContent = 'Finalizar Cadastro';
    }
});