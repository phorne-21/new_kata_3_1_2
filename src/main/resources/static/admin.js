let currentUserId = null;
let currentUserEditId = null;

// Инициализация модальных окон
const editModal = new bootstrap.Modal(document.getElementById('editModal'));
const deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));

// Загрузка текущего пользователя
async function loadCurrentUser() {
    try {
        const response = await fetch('/api/admin/current-user', {
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('Failed to fetch current user');
        }

        const user = await response.json();
        document.getElementById('user-email').textContent = user.email;
        document.getElementById('user-roles').textContent = user.roles.map(role =>
            role.name.replace('ROLE_', '')).join(', ');

        return user;
    } catch (error) {
        console.error('Error loading current user:', error);
        throw error;
    }
}

// Загрузка всех пользователей
async function loadUsers() {
    try {
        const response = await fetch('/api/admin/users', {
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('Failed to fetch users');
        }

        const users = await response.json();
        renderUsersTable(users);
    } catch (error) {
        console.error('Error loading users:', error);
        showError('Failed to load users');
    }
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
            throw new Error('Logout failed');
        }
    } catch (error) {
        console.error('Error logging out:', error);
        showError('Logout failed');
    }
});

// Создание ячейки таблицы
function createCell(content) {
    const td = document.createElement('td');
    td.textContent = content;
    return td;
}

// Рендеринг таблицы пользователей
function renderUsersTable(users) {
    const tbody = document.getElementById('users-table-body');
    tbody.innerHTML = '';

    users.forEach(user => {
        const tr = document.createElement('tr');

        tr.appendChild(createCell(user.id));
        tr.appendChild(createCell(user.firstName));
        tr.appendChild(createCell(user.lastName));
        tr.appendChild(createCell(user.age));
        tr.appendChild(createCell(user.email));
        tr.appendChild(createCell(user.roles.map(role =>
            role.name.replace('ROLE_', '')).join(', ')));

        // Кнопка Edit
        const editTd = document.createElement('td');
        const editButton = document.createElement('button');
        editButton.className = 'btn btn-primary btn-sm edit-btn';
        editButton.textContent = 'Edit';
        editButton.onclick = () => openEditModal(user.id);
        editTd.appendChild(editButton);
        tr.appendChild(editTd);

        // Кнопка Delete
        const deleteTd = document.createElement('td');
        const deleteButton = document.createElement('button');
        deleteButton.className = 'btn btn-danger btn-sm delete-btn';
        deleteButton.textContent = 'Delete';
        deleteButton.onclick = () => openDeleteModal(user.id);
        deleteTd.appendChild(deleteButton);
        tr.appendChild(deleteTd);

        tbody.appendChild(tr);
    });
}

// Загрузка информации пользователя
async function loadUserInfo() {
    try {
        const user = await loadCurrentUser();
        const tbody = document.getElementById('user-table-body');
        tbody.innerHTML = `
            <tr>
                <td>${user.id}</td>
                <td>${user.firstName || ''}</td>
                <td>${user.lastName || ''}</td>
                <td>${user.age || ''}</td>
                <td>${user.email}</td>
                <td>${user.roles.map(role => role.name.replace('ROLE_', '')).join(', ')}</td>
            </tr>
        `;
    } catch (error) {
        console.error('Error loading user info:', error);
        showError('Failed to load user info');
    }
}

// Загрузка ролей
async function loadRoles() {
    try {
        const response = await fetch('/api/admin/roles', {
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('Failed to load roles');
        }

        return await response.json();
    } catch (error) {
        console.error('Error loading roles:', error);
        showError('Failed to load roles');
        return [];
    }
}

// Открытие модального окна редактирования
async function openEditModal(userId) {
    try {
        const [user, roles] = await Promise.all([
            fetch(`/api/admin/users/${userId}`, { credentials: 'include' }).then(res => res.json()),
            loadRoles()
        ]);

        currentUserEditId = userId;

        const form = document.getElementById('edit-user-form');
        form.querySelector('input[name="id"]').value = userId;
        form.querySelector('#user-id').value = userId;
        form.querySelector('#edit-user-firstName').value = user.firstName || '';
        form.querySelector('#edit-user-lastname').value = user.lastName || '';
        form.querySelector('#edit-user-age').value = user.age || '';
        form.querySelector('#edit-user-email').value = user.email;

        const roleSelect = form.querySelector('#edit-roles-select');
        roleSelect.innerHTML = '';

        roles.forEach(role => {
            const option = document.createElement('option');
            option.value = role.id;
            option.textContent = role.name.replace('ROLE_', '');
            option.selected = user.roles.some(r => r.id === role.id);
            roleSelect.appendChild(option);
        });

        editModal.show();
    } catch (error) {
        console.error('Error opening edit modal:', error);
        showError('Failed to load user data for editing');
    }
}

// Открытие модального окна удаления
async function openDeleteModal(userId) {
    try {
        const user = await fetch(`/api/admin/users/${userId}`, {
            credentials: 'include'
        }).then(res => res.json());

        currentUserId = userId;

        const form = document.getElementById('delete-user-form');
        form.querySelector('input[name="id"]').value = userId;
        form.querySelector('#delete-display-user-id').value = userId;
        form.querySelector('#delete-display-user-firstName').value = user.firstName || '';
        form.querySelector('#delete-display-user-lastname').value = user.lastName || '';
        form.querySelector('#delete-display-user-age').value = user.age || '';
        form.querySelector('#delete-display-user-email').value = user.email;
        document.getElementById('delete-display-user-roles').textContent =
            user.roles.map(role => role.name.replace('ROLE_', '')).join(', ');

        deleteModal.show();
    } catch (error) {
        console.error('Error opening delete modal:', error);
        showError('Failed to load user data for deletion');
    }
}

// Обработка формы редактирования
document.getElementById('edit-user-form').addEventListener('submit', async (event) => {
    event.preventDefault();

    const formData = new FormData(event.target);
    const userId = formData.get('id');

    const rolesSelect = event.target.querySelector('#edit-roles-select');
    const selectedRoles = Array.from(rolesSelect.selectedOptions).map(option => ({
        id: parseInt(option.value),
        name: option.textContent
    }));

    const userData = {
        id: parseInt(userId),
        email: formData.get('email'),
        firstName: formData.get('firstName'),
        lastName: formData.get('lastName'),
        age: parseInt(formData.get('age')),
        password: formData.get('password') || null,
        roles: selectedRoles
    };

    try {
        const response = await fetch(`/api/admin/users/${userId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify(userData)
        });

        if (!response.ok) {
            throw new Error('Failed to update user');
        }

        editModal.hide();
        await loadUsers();
        await loadUserInfo();
    } catch (error) {
        console.error('Error updating user:', error);
        showError('Failed to update user');
    }
});

// Обработка формы удаления
document.getElementById('delete-user-form').addEventListener('submit', async (event) => {
    event.preventDefault();

    const formData = new FormData(event.target);
    const userId = formData.get('id');

    try {
        const response = await fetch(`/api/admin/users/${userId}`, {
            method: 'DELETE',
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('Failed to delete user');
        }

        deleteModal.hide();
        await loadUsers();

        // Если удалили себя - разлогиниваем
        const currentUser = await loadCurrentUser();
        if (parseInt(userId) === currentUser.id) {
            window.location.href = '/login';
        }
    } catch (error) {
        console.error('Error deleting user:', error);
        showError('Failed to delete user');
    }
});

// Обработка формы создания пользователя
document.getElementById('create-user-form').addEventListener('submit', async (event) => {
    event.preventDefault();

    const formData = new FormData(event.target);
    const rolesSelect = event.target.querySelector('#roles-select');

    const userData = {
        firstName: formData.get('firstName'),
        lastName: formData.get('lastName'),
        age: parseInt(formData.get('age')),
        email: formData.get('email'),
        password: formData.get('password'),
        roles: Array.from(rolesSelect.selectedOptions).map(option => ({
            id: parseInt(option.value),
            name: option.textContent
        }))
    };

    try {
        const response = await fetch('/api/admin/users', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify(userData)
        });

        if (!response.ok) {
            throw new Error('Failed to create user');
        }

        event.target.reset();
        await loadUsers();

        // Переключаем на вкладку с таблицей пользователей
        document.querySelector('.nav-tabs .nav-link').click();
    } catch (error) {
        console.error('Error creating user:', error);
        showError('Failed to create user');
    }
});

// Загрузка ролей в select
async function loadRolesToSelect() {
    try {
        const roles = await loadRoles();
        const rolesSelect = document.querySelector('#roles-select');
        const editRolesSelect = document.querySelector('#edit-roles-select');

        [rolesSelect, editRolesSelect].forEach(select => {
            if (select) {
                select.innerHTML = '';
                roles.forEach(role => {
                    const option = document.createElement('option');
                    option.value = role.id;
                    option.textContent = role.name.replace('ROLE_', '');
                    select.appendChild(option);
                });
            }
        });
    } catch (error) {
        console.error('Error loading roles to select:', error);
        showError('Failed to load roles');
    }
}

// Показать ошибку
function showError(message) {
    const errorAlert = document.getElementById('error-alert');
    if (errorAlert) {
        errorAlert.textContent = message;
        errorAlert.classList.remove('d-none');
        setTimeout(() => errorAlert.classList.add('d-none'), 5000);
    }
}

// Инициализация страницы
document.addEventListener('DOMContentLoaded', async () => {
    try {
        await Promise.all([
            loadCurrentUser(),
            loadUsers(),
            loadUserInfo(),
            loadRolesToSelect()
        ]);
    } catch (error) {
        console.error('Error initializing page:', error);
        showError('Failed to initialize page');
    }
});
