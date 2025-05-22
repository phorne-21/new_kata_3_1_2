document.addEventListener('DOMContentLoaded', async () => {
    try {
        const user = await getCurrentUser();
        updateUserInfo(user);
        renderUserTable(user); // Переименовано для ясности
    } catch (error) {
        console.error('Error initializing page:', error);
        // Перенаправляем только если ошибка 401 (Unauthorized)
        if (error instanceof Error && error.message.includes('401')) {
            window.location.href = '/login';
        }
    }
});

async function getCurrentUser() {
    const response = await fetch('/api/user/current-user', {
        credentials: 'include'
    });

    if (response.status === 401) {
        throw new Error('Unauthorized (401)');
    }

    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }

    return await response.json();
}

function updateUserInfo(user) {
    document.getElementById('user-email').textContent = user.email;
    document.getElementById('user-roles').textContent =
        user.roles.map(role => role.name.replace('ROLE_', '')).join(', ');
}

function renderUserTable(user) {
    const tbody = document.getElementById('user-table-body');
    tbody.innerHTML = '';

    const tr = document.createElement('tr');
    tr.appendChild(createCell(user.id));
    tr.appendChild(createCell(user.firstName || ''));
    tr.appendChild(createCell(user.lastName || ''));
    tr.appendChild(createCell(user.age || ''));
    tr.appendChild(createCell(user.email));
    tr.appendChild(createCell(
        user.roles.map(role => role.name.replace('ROLE_', '')).join(', ')
    ));

    tbody.appendChild(tr);
}

function createCell(content) {
    const td = document.createElement('td');
    td.textContent = content;
    return td;
}

// Обработка выхода из системы
document.getElementById('logout-form').addEventListener('submit', async (event) => {
    event.preventDefault();
    try {
        const response = await fetch('/logout', {
            method: 'POST',
            credentials: 'include'
        });

        if (response.ok) {
            window.location.href = '/login';
        } else {
            console.error('Logout failed with status:', response.status);
        }
    } catch (error) {
        console.error('Error logging out:', error);
    }
});