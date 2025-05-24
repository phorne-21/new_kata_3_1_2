document.addEventListener('DOMContentLoaded', async () => {
    try {
        console.log("Loading user data...");
        const user = await getCurrentUser();
        console.log("User data received:", user);

        updateUserInfo(user);
        renderUserTable(user);
    } catch (error) {
        console.error('Error loading user data:', error);
        // Можно добавить отображение ошибки пользователю
        const errorElement = document.createElement('div');
        errorElement.className = 'alert alert-danger';
        errorElement.textContent = 'Failed to load user data. Please try again.';
        document.querySelector('.main-content').prepend(errorElement);
    }
});

async function getCurrentUser() {
    try {
        console.log("Fetching user data from /api/user/current-user...");
        const response = await fetch('/api/user/current-user', {
            credentials: 'include'
        });
        console.log("Response status:", response.status);
        if (!response.ok) {
            const errorText = await response.text();
            console.error("Error response:", errorText);
            throw new Error(`Server returned ${response.status}: ${errorText}`);
        }

        const userData = await response.json();
        console.log("Parsed user data:", userData);
        return userData;
    } catch (error) {
        console.error("Error in getCurrentUser:", error);
        throw error;
    }
}

function updateUserInfo(user) {
    try {
        console.log("Updating user info with:", user);

        const emailElement = document.getElementById('user-email');
        const rolesElement = document.getElementById('user-roles');

        if (!emailElement || !rolesElement) {
            throw new Error('Required DOM elements not found');
        }

        // Устанавливаем email
        emailElement.textContent = user?.email || 'No email';

        // Обрабатываем роли (учитываем, что roles может быть массивом строк)
        const roles = user?.roles || [];
        const formattedRoles = roles
            .map(role => {
                // Если role - строка (из UserReadDTO), просто обрабатываем
                if (typeof role === 'string') {
                    return role.replace('ROLE_', '');
                }
                // Если role - объект (на всякий случай)
                return role?.name?.replace('ROLE_', '') || 'N/A';
            })
            .filter(role => role !== 'N/A')
            .join(', ');

        rolesElement.textContent = formattedRoles || 'No roles assigned';
    } catch (error) {
        console.error("Error in updateUserInfo:", error);
        throw error;
    }
}

function renderUserTable(user) {
    try {
        console.log("Rendering user table with:", user);

        const tbody = document.getElementById('user-table-body');
        if (!tbody) {
            throw new Error('Table body element not found');
        }

        tbody.innerHTML = '';

        const tr = document.createElement('tr');

        // Формируем данные для таблицы
        const userData = [
            user?.id ?? 'N/A',
            user?.firstName ?? 'N/A',
            user?.lastName ?? 'N/A',
            user?.age ?? 'N/A',
            user?.email ?? 'N/A',
            // Форматируем роли аналогично updateUserInfo
            (user?.roles || [])
                .map(role => {
                    if (typeof role === 'string') {
                        return role.replace('ROLE_', '');
                    }
                    return role?.name?.replace('ROLE_', '') || 'N/A';
                })
                .filter(role => role !== 'N/A')
                .join(', ') || 'No roles'
        ];

        // Добавляем ячейки в строку
        userData.forEach(data => {
            const td = document.createElement('td');
            td.textContent = data;
            tr.appendChild(td);
        });

        tbody.appendChild(tr);
        console.log("Table rendered successfully");
    } catch (error) {
        console.error("Error in renderUserTable:", error);
        throw error;
    }
}

// Обработка выхода из системы
document.getElementById('logout-form')?.addEventListener('submit', async (event) => {
    event.preventDefault();
    try {
        console.log("Attempting logout...");
        const response = await fetch('/logout', {
            method: 'POST',
            credentials: 'include'
        });

        if (response.ok) {
            console.log("Logout successful, redirecting to login page");
            window.location.href = '/login';
        } else {
            const errorText = await response.text();
            console.error(`Logout failed with status ${response.status}:`, errorText);
            alert('Logout failed. Please try again.');
        }
    } catch (error) {
        console.error("Error during logout:", error);
        alert('An error occurred during logout. Please try again.');
    }
});