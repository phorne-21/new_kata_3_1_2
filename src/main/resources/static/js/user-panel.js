document.addEventListener('DOMContentLoaded', async () => {
    try {
        const user = await api.getCurrentUser();
        if (!user.roles.some(r => r.name === 'ROLE_ADMIN')) {
            window.location.href = '/admin_panel.html';
            return;
        }

        renderUserData(user);
        setupEventListeners();
    } catch (error) {
        window.location.href = '/login.html';
    }
});

document.getElementById('logout-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
        await api.logout();
        window.location.href = '/login.html'; // переход на страницу авторизации
    } catch (error) {
        console.error('Logout failed:', error);
        alert('Logout failed. Please try again.');
    }
});

async function loadUserData() {
    try {
        const user = await api.getCurrentUser();
        renderUserData(user);
    } catch (error) {
        throw new Error('Failed to load user: ' + error.message);
    }
}

function renderUserData(user) {
    // Шапка
    document.getElementById('user-email').textContent = user.email;
    document.getElementById('user-roles').textContent =
        user.roles.map(r => r.name.substring(5)).join(', ');

    // Таблица
    const tbody = document.getElementById('user-table-body');
    tbody.innerHTML = `
        <tr>
            <td>${user.id}</td>
            <td>${user.firstName}</td>
            <td>${user.lastName}</td>
            <td>${user.age}</td>
            <td>${user.email}</td>
            <td>${user.roles.map(r => r.name.substring(5)).join(', ')}</td>
        </tr>
    `;
}

function setupEventListeners() {
    // Обработчик выхода
    document.getElementById('logout-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        try {
            await api.logout();
            window.location.href = '/login';
        } catch (error) {
            console.error('Logout failed:', error);
            alert('Logout failed');
        }
    });
}