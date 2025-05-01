document.getElementById('login-form').addEventListener('submit', async (e) => {
    e.preventDefault();

    const formData = {
        username: document.getElementById('username').value,
        password: document.getElementById('password').value
    };

    try {
        const response = await fetch('/api/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(formData)
        });

        if (response.ok) {
            window.location.href = '/'; // Перенаправление после успешного входа
        } else {
            showError('Invalid credentials');
        }
    } catch (error) {
        showError('Login failed');
    }
});