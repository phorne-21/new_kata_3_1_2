document.addEventListener('DOMContentLoaded', async () => {
    try {
        const user = await api.getCurrentUser();
        if (!user.roles.some(r => r.name === 'ROLE_ADMIN')) {
            window.location.href = '/user.html';
            return;
        }

        await loadUsers();
        await loadRoles(); // Загружаем роли для селектов
        setupEventListeners();
        renderCurrentUser(user); // Загружаем данные текущего пользователя
    } catch (error) {
        showError('Access denied or initialization failed: ' + error.message);
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

// Загрузка всех пользователей
async function loadUsers() {
    try {
        const users = await api.getAllUsers();
        renderUsersTable(users);
    } catch (error) {
        showError('Failed to load users: ' + error.message);
        throw error;
    }
}

// Загрузка текущего пользователя (для шапки и user вкладки)
async function loadCurrentUser() {
    try {
        const user = await api.getCurrentUser();
        renderCurrentUser(user);
        renderUserInfo(user); // Для вкладки User
    } catch (error) {
        showError('Failed to load current user: ' + error.message);
    }
}

// Загрузка всех ролей для форм
async function loadRoles() {
    try {
        const roles = await api.getAllRoles();
        renderRoleOptions(roles);
    } catch (error) {
        showError('Failed to load roles: ' + error.message);
    }
}

// Рендер таблицы пользователей
function renderUsersTable(users) {
    const tbody = document.getElementById('users-table-body');
    tbody.innerHTML = '';
    const template = document.getElementById('user-row-template');

    users.forEach(user => {
        const clone = template.content.cloneNode(true);

        // Заполняем данные
        clone.querySelector('.user-id').textContent = user.id;
        clone.querySelector('.user-firstname').textContent = user.firstName;
        clone.querySelector('.user-lastname').textContent = user.lastName;
        clone.querySelector('.user-age').textContent = user.age;
        clone.querySelector('.user-email').textContent = user.email;
        clone.querySelector('.user-roles').textContent = user.roles.map(r => r.name.substring(5)).join(', ');

        // Назначаем обработчики с передачей ID пользователя
        clone.querySelector('.edit-btn').dataset.id = user.id;
        clone.querySelector('.delete-btn').dataset.id = user.id;

        tbody.appendChild(clone);
    });
}

// Рендер текущего пользователя в шапке
function renderCurrentUser(user) {
    document.getElementById('user-email').textContent = user.email;
    document.getElementById('user-roles').textContent =
        user.roles.map(r => r.name.substring(5)).join(', ');
}

// Рендер информации о пользователе (вкладка User)
function renderUserInfo(user) {
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

// Рендер опций ролей в селектах
function renderRoleOptions(roles) {
    const createSelect = document.getElementById('roles-select');
    const editSelect = document.getElementById('edit-roles-select');

    // Очищаем и заполняем оба селекта
    [createSelect, editSelect].forEach(select => {
        select.innerHTML = '';
        roles.forEach(role => {
            const option = document.createElement('option');
            option.value = role.name;
            option.textContent = role.name.substring(5);
            select.appendChild(option);
        });
    });
}

// Открытие модалки редактирования
async function openEditModal(userId) {
    try {
        const user = await api.getUserById(userId);

        // Заполняем форму
        document.getElementById('edit-id').value = user.id;
        document.getElementById('user-id').value = user.id;
        document.getElementById('edit-user-firstName').value = user.firstName;
        document.getElementById('edit-user-lastname').value = user.lastName;
        document.getElementById('edit-user-age').value = user.age;
        document.getElementById('edit-user-email').value = user.email;

        // Устанавливаем выбранные роли
        const roleSelect = document.getElementById('edit-roles-select');
        Array.from(roleSelect.options).forEach(option => {
            option.selected = user.roles.some(r => r.name === option.value);
        });

        new bootstrap.Modal('#editModal').show();
    } catch (error) {
        showError('Failed to load user data: ' + error.message);
    }
}

// Открытие модалки удаления
async function openDeleteModal(userId) {
    try {
        const user = await api.getUserById(userId);

        // Заполняем данные
        document.getElementById('delete-user-id').value = user.id;
        document.getElementById('delete-display-user-id').value = user.id;
        document.getElementById('user-firstName').value = user.firstName;
        document.getElementById('user-lastname').value = user.lastName;
        document.getElementById('user-age').value = user.age;
        document.getElementById('delete-display-user-email').value = user.email;
        document.getElementById('delete-display-user-roles').textContent =
            user.roles.map(r => r.name.substring(5)).join(', ');

        new bootstrap.Modal('#deleteModal').show();
    } catch (error) {
        showError('Failed to load user data: ' + error.message);
    }
}

// Настройка всех обработчиков событий
function setupEventListeners() {
    // Обработчик формы создания пользователя
    document.getElementById('create-user-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const formData = new FormData(e.target);
        const data = {
            firstName: formData.get('firstName'),
            lastName: formData.get('lastname'),
            age: formData.get('age'),
            email: formData.get('email'),
            password: formData.get('password'),
            roles: Array.from(formData.getAll('roleNames'))
        };

        try {
            await api.createUser(data);
            await loadUsers();
            e.target.reset();
            bootstrap.Tab.getInstance('#new-user-tab').show(); // Переключаем на таблицу
        } catch (error) {
            showError('Failed to create user: ' + error.message);
        }
    });

    // Обработчик формы редактирования
    document.getElementById('edit-user-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const formData = new FormData(e.target);
        const data = {
            id: formData.get('id'),
            firstName: formData.get('firstName'),
            lastName: formData.get('lastname'),
            age: formData.get('age'),
            email: formData.get('email'),
            password: formData.get('password'),
            roles: Array.from(formData.getAll('roleNames'))
        };

        try {
            await api.updateUser(data);
            await loadUsers();
            bootstrap.Modal.getInstance('#editModal').hide();
        } catch (error) {
            showError('Failed to update user: ' + error.message);
        }
    });

    // Обработчик формы удаления
    document.getElementById('delete-user-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const userId = document.getElementById('delete-user-id').value;

        try {
            await api.deleteUser(userId);
            await loadUsers();
            bootstrap.Modal.getInstance('#deleteModal').hide();
        } catch (error) {
            showError('Failed to delete user: ' + error.message);
        }
    });

    // Обработчик выхода
    document.getElementById('logout-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        try {
            await api.logout();
            window.location.href = '/login';
        } catch (error) {
            showError('Logout failed: ' + error.message);
        }
    });

    // Делегирование событий для кнопок в таблице
    document.getElementById('users-table-body').addEventListener('click', (e) => {
        if (e.target.classList.contains('edit-btn')) {
            openEditModal(e.target.dataset.id);
        } else if (e.target.classList.contains('delete-btn')) {
            openDeleteModal(e.target.dataset.id);
        }
    });
}

// Показать ошибку
function showError(message) {
    const errorAlert = document.getElementById('error-alert');
    if (errorAlert) {
        errorAlert.textContent = message;
        errorAlert.classList.remove('d-none');
        setTimeout(() => errorAlert.classList.add('d-none'), 5000);
    } else {
        alert(message); // Fallback
    }
}