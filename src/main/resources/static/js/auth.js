document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('login-form');
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            const credentials = {
                username: document.getElementById('username').value,
                password: document.getElementById('password').value
            };

            try {
                const response = await api.login(credentials);
                window.location.href = response.role === 'ADMIN' ? '/admin_panel.html' : '/user.html';
            } catch (error) {
                showError('Login failed: ' + error.message);
            }
        });
    }
});

function showError(message) {
    const errorElement = document.createElement('div');
    errorElement.className = 'alert alert-danger';
    errorElement.textContent = message;
    document.querySelector('main').prepend(errorElement);

    setTimeout(() => {
        errorElement.remove();
    }, 5000);
}