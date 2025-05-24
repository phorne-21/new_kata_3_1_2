// Проверяем, не объявлена ли переменная currentUser уже
if (typeof currentUser === 'undefined') {
    let currentUser = null;
}
let currentEditUserId = null;

// Инициализация модальных окон только если они существуют
let editModal, deleteModal;
if (document.getElementById('editModal')) {
    editModal = new bootstrap.Modal(document.getElementById('editModal'));
}
if (document.getElementById('deleteModal')) {
    deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
}

// Основная функция инициализации страницы
document.addEventListener('DOMContentLoaded', async () => {
    try {
        console.log("Initializing admin page...");

        // Загружаем текущего пользователя
        currentUser = await loadCurrentUser();
        console.log("Current user:", currentUser);

        // Проверяем права администратора
        if (!hasAdminRole(currentUser)) {
            showError("Access denied. Admin role required");
            return;
        }

        // Инициализируем интерфейс
        await initAdminInterface();

        console.log("Admin page initialized successfully");
    } catch (error) {
        console.error('Error initializing page:', error);
        showError('Failed to initialize page: ' + error.message);
    }
});

function hasAdminRole(user) {
    if (!user || !user.roles) return false;
    return user.roles.some(role => {
        const roleName = typeof role === 'string' ? role : role.name;
        return roleName === 'ROLE_ADMIN';
    });
}

async function loadCurrentUser() {
    try {
        console.log("Fetching current user...");
        const response = await fetch('/api/admin/current-user', {
            credentials: 'include',
            headers: {
                'Accept': 'application/json'
            }
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Failed to fetch current user: ${response.status} ${errorText}`);
        }

        const user = await response.json();
        console.log("Current user data:", user);

        // Обновляем информацию в шапке
        updateUserHeader(user);

        return user;
    } catch (error) {
        console.error('Error loading current user:', error);
        throw error;
    }
}

function updateUserHeader(user) {
    const emailElement = document.getElementById('user-email');
    const rolesElement = document.getElementById('user-roles');

    if (emailElement && rolesElement) {
        emailElement.textContent = user?.email || 'No email';

        const roles = user?.roles || [];
        const formattedRoles = roles
            .map(role => {
                if (typeof role === 'string') {
                    return role.replace('ROLE_', '');
                }
                return role?.name?.replace('ROLE_', '') || 'N/A';
            })
            .filter(role => role !== 'N/A')
            .join(', ');

        rolesElement.textContent = formattedRoles || 'No roles assigned';
    }
}

async function initAdminInterface() {
    try {
        // Загружаем данные
        await Promise.all([
            loadUsers(),
            loadRolesToSelect(),
            loadUserInfo()
        ]);

        // Активируем админ-вкладку
        const adminTab = new bootstrap.Tab(document.getElementById('admin-tab'));
        adminTab.show();

        // Активируем вкладку таблицы пользователей внутри админ-панели
        const usersTab = document.querySelector('#admin-content a[href="#users-table"]');
        if (usersTab) {
            new bootstrap.Tab(usersTab).show();
        }

    } catch (error) {
        console.error('Error initializing admin interface:', error);
        throw error;
    }
}

async function loadUserInfo() {
    try {
        const user = await loadCurrentUser();
        renderUserInfoTable(user);
    } catch (error) {
        console.error('Error loading user info:', error);
        showError('Failed to load user info: ' + error.message);
    }
}

function renderUserInfoTable(user) {
    const tbody = document.getElementById('user-table-body');
    if (!tbody) return;

    tbody.innerHTML = `
        <tr>
            <td>${user.id || ''}</td>
            <td>${user.firstName || ''}</td>
            <td>${user.lastName || ''}</td>
            <td>${user.age || ''}</td>
            <td>${user.email || ''}</td>
            <td>${(user.roles || [])
        .map(role => {
            if (typeof role === 'string') {
                return role.replace('ROLE_', '');
            }
            return role?.name?.replace('ROLE_', '') || '';
        })
        .filter(role => role)
        .join(', ')}</td>
        </tr>
    `;
}

async function loadUsers() {
    try {
        console.log("Loading users list...");
        const response = await fetch('/api/admin/users', {
            credentials: 'include',
            headers: {
                'Accept': 'application/json'
            }
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Failed to fetch users: ${response.status} ${errorText}`);
        }

        const users = await response.json();
        console.log("Users data received:", users);

        renderUsersTable(users);
    } catch (error) {
        console.error('Error loading users:', error);
        showError('Failed to load users: ' + error.message);
    }
}

function renderUsersTable(users) {
    const tbody = document.getElementById('users-table-body');
    if (!tbody) {
        console.error("Users table body not found");
        return;
    }

    tbody.innerHTML = '';

    users.forEach(user => {
        const tr = document.createElement('tr');

        // Добавляем основные данные
        tr.appendChild(createCell(user.id));
        tr.appendChild(createCell(user.firstName));
        tr.appendChild(createCell(user.lastName));
        tr.appendChild(createCell(user.age));
        tr.appendChild(createCell(user.email));

        // Форматируем роли
        const rolesText = (user.roles || [])
            .map(role => {
                if (typeof role === 'string') {
                    return role.replace('ROLE_', '');
                }
                return role?.name?.replace('ROLE_', '') || 'N/A';
            })
            .filter(role => role !== 'N/A')
            .join(', ');
        tr.appendChild(createCell(rolesText));

        // Кнопка Edit
        const editBtn = document.createElement('button');
        editBtn.className = 'btn btn-primary btn-sm edit-btn';
        editBtn.textContent = 'Edit';
        editBtn.onclick = () => openEditModal(user.id);
        tr.appendChild(createCellWithElement(editBtn));

        // Кнопка Delete
        const deleteBtn = document.createElement('button');
        deleteBtn.className = 'btn btn-danger btn-sm delete-btn';
        deleteBtn.textContent = 'Delete';
        deleteBtn.onclick = () => openDeleteModal(user.id);
        tr.appendChild(createCellWithElement(deleteBtn));

        tbody.appendChild(tr);
    });
}

function createCell(content) {
    const td = document.createElement('td');
    td.textContent = content !== null && content !== undefined ? content : '';
    return td;
}

function createCellWithElement(element) {
    const td = document.createElement('td');
    td.appendChild(element);
    return td;
}

async function loadRolesToSelect() {
    try {
        console.log("Loading roles...");
        const response = await fetch('/api/admin/roles', {
            credentials: 'include',
            headers: {
                'Accept': 'application/json'
            }
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Failed to load roles: ${response.status} ${errorText}`);
        }

        const roles = await response.json();
        console.log("Roles data received:", roles);

        // Заполняем select для создания пользователя
        const rolesSelect = document.getElementById('roles-select');
        const editRolesSelect = document.getElementById('edit-roles-select');

        [rolesSelect, editRolesSelect].forEach(select => {
            if (select) {
                select.innerHTML = '';
                roles.forEach(role => {
                    const option = document.createElement('option');
                    option.value = role.name;
                    option.textContent = role.name.replace('ROLE_', '');
                    select.appendChild(option);
                });
            }
        });
    } catch (error) {
        console.error('Error loading roles:', error);
        showError('Failed to load roles: ' + error.message);
    }
}

async function openEditModal(userId) {
    try {
        // Инициализируем модальное окно, если оно еще не инициализировано
        if (!editModal && document.getElementById('editModal')) {
            editModal = new bootstrap.Modal(document.getElementById('editModal'));
        }

        if (!editModal) {
            throw new Error("Edit modal element not found or not initialized");
        }

        console.log(`Opening edit modal for user ${userId}...`);
        currentEditUserId = userId;

        const response = await fetch(`/api/admin/users/${userId}`, {
            credentials: 'include',
            headers: {
                'Accept': 'application/json'
            }
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Failed to fetch user: ${response.status} ${errorText}`);
        }

        const user = await response.json();
        const roles = await loadRoles();

        const form = document.getElementById('edit-user-form');
        if (!form) {
            throw new Error("Edit form not found");
        }

        // Заполняем форму данными пользователя
        form.querySelector('input[name="id"]').value = userId;
        form.querySelector('#user-id').value = userId;
        form.querySelector('#edit-user-firstName').value = user.firstName || '';
        form.querySelector('#edit-user-lastName').value = user.lastName || '';
        form.querySelector('#edit-user-age').value = user.age || '';
        form.querySelector('#edit-user-email').value = user.email || '';

        // Заполняем select с ролями
        const roleSelect = form.querySelector('#edit-roles-select');
        if (roleSelect) {
            roleSelect.innerHTML = '';
            roles.forEach(role => {
                const option = document.createElement('option');
                option.value = role.name;
                option.textContent = role.name.replace('ROLE_', '');

                // Проверяем, есть ли роль у пользователя
                const hasRole = user.roles.some(r =>
                    (typeof r === 'string' ? r : r.name) === role.name
                );
                option.selected = hasRole;

                roleSelect.appendChild(option);
            });
        }

        editModal.show();
        console.log("Edit modal opened successfully");
    } catch (error) {
        console.error('Error opening edit modal:', error);
        showError('Failed to load user data for editing: ' + error.message);
    }
}

async function loadRoles() {
    const response = await fetch('/api/admin/roles', {
        credentials: 'include',
        headers: {
            'Accept': 'application/json'
        }
    });

    if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Failed to load roles: ${response.status} ${errorText}`);
    }

    return await response.json();
}

// Обработка формы редактирования
document.getElementById('edit-user-form')?.addEventListener('submit', async (event) => {
    event.preventDefault();
    console.log("Edit form submitted");

    try {
        const formData = new FormData(event.target);
        const userId = formData.get('id');
        const rolesSelect = event.target.querySelector('#edit-roles-select');

        // Получаем выбранные роли
        const selectedRoles = Array.from(rolesSelect.selectedOptions)
            .map(option => option.value);

        const userData = {
            id: parseInt(userId),
            email: formData.get('email'),
            firstName: formData.get('firstName'),
            lastName: formData.get('lastName'),
            age: formData.get('age') ? parseInt(formData.get('age')) : null,
            password: formData.get('password') || null,
            roles: selectedRoles
        };

        console.log("Sending user update:", userData);
        const response = await fetch(`/api/admin/users/${userId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            credentials: 'include',
            body: JSON.stringify(userData)
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Failed to update user: ${response.status} ${errorText}`);
        }

        editModal.hide();
        await Promise.all([loadUsers(), loadUserInfo()]);
        showError('User updated successfully', 'success');
    } catch (error) {
        console.error('Error updating user:', error);
        showError('Failed to update user: ' + error.message);
    }
});

async function openDeleteModal(userId) {
    try {
        if (!deleteModal) {
            throw new Error("Delete modal not initialized");
        }

        console.log(`Opening delete modal for user ${userId}...`);
        const user = await fetch(`/api/admin/users/${userId}`, {
            credentials: 'include',
            headers: {
                'Accept': 'application/json'
            }
        }).then(res => res.json());

        const form = document.getElementById('delete-user-form');
        if (!form) {
            throw new Error("Delete form not found");
        }

        // Заполняем форму данными пользователя
        form.querySelector('input[name="id"]').value = userId;
        form.querySelector('#delete-display-user-id').value = userId;
        form.querySelector('#delete-display-user-firstName').value = user.firstName || '';
        form.querySelector('#delete-display-user-lastName').value = user.lastName || '';
        form.querySelector('#delete-display-user-age').value = user.age || '';
        form.querySelector('#delete-display-user-email').value = user.email || '';

        // Форматируем роли для отображения
        document.getElementById('delete-display-user-roles').textContent =
            (user.roles || [])
                .map(role => {
                    if (typeof role === 'string') {
                        return role.replace('ROLE_', '');
                    }
                    return role?.name?.replace('ROLE_', '') || '';
                })
                .filter(role => role)
                .join(', ');

        deleteModal.show();
        console.log("Delete modal opened successfully");
    } catch (error) {
        console.error('Error opening delete modal:', error);
        showError('Failed to load user data for deletion: ' + error.message);
    }
}

// Обработка формы удаления
document.getElementById('delete-user-form')?.addEventListener('submit', async (event) => {
    event.preventDefault();
    console.log("Delete form submitted");

    try {
        const formData = new FormData(event.target);
        const userId = formData.get('id');

        console.log(`Deleting user ${userId}...`);
        const response = await fetch(`/api/admin/users/${userId}`, {
            method: 'DELETE',
            credentials: 'include'
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Failed to delete user: ${response.status} ${errorText}`);
        }

        deleteModal.hide();
        await loadUsers();

        // Если удалили себя - разлогиниваем
        if (currentUser && parseInt(userId) === currentUser.id) {
            window.location.href = '/login';
        }

        showError('User deleted successfully', 'success');
    } catch (error) {
        console.error('Error deleting user:', error);
        showError('Failed to delete user: ' + error.message);
    }
});

// Обработка формы создания пользователя
document.getElementById('create-user-form')?.addEventListener('submit', async (event) => {
    event.preventDefault();
    console.log("Create user form submitted");

    try {
        const formData = new FormData(event.target);
        const rolesSelect = event.target.querySelector('#roles-select');

        // Получаем выбранные роли
        const selectedRoles = Array.from(rolesSelect.selectedOptions)
            .map(option => option.value);

        const userData = {
            firstName: formData.get('firstName'),
            lastName: formData.get('lastName'),
            age: formData.get('age') ? parseInt(formData.get('age')) : null,
            email: formData.get('email'),
            password: formData.get('password'),
            roles: selectedRoles
        };

        console.log("Creating new user:", userData);
        const response = await fetch('/api/admin/users', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            credentials: 'include',
            body: JSON.stringify(userData)
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Failed to create user: ${response.status} ${errorText}`);
        }

        event.target.reset();
        await loadUsers();

        // Переключаем на вкладку с таблицей пользователей
        const usersTabLink = document.querySelector('a[href="#users-table"]');
        if (usersTabLink) {
            const usersTab = new bootstrap.Tab(usersTabLink);
            usersTab.show();

            // Также активируем соответствующий контент
            const usersContent = document.getElementById('users-table');
            if (usersContent) {
                document.querySelectorAll('.tab-pane').forEach(pane => {
                    pane.classList.remove('show', 'active');
                });
                usersContent.classList.add('show', 'active');
            }
        }

        showError('User created successfully', 'success');
    } catch (error) {
        console.error('Error creating user:', error);
        showError('Failed to create user: ' + error.message);
    }
});

// Обработка выхода из системы
document.getElementById('logout-form')?.addEventListener('submit', async (event) => {
    event.preventDefault();
    console.log("Logout form submitted");

    try {
        const response = await fetch('/logout', {
            method: 'POST',
            credentials: 'include'
        });

        if (response.ok) {
            window.location.href = '/login';
        } else {
            const errorText = await response.text();
            throw new Error(`Logout failed: ${response.status} ${errorText}`);
        }
    } catch (error) {
        console.error('Error logging out:', error);
        showError('Logout failed: ' + error.message);
    }
});

// Показать сообщение об ошибке/успехе
function showError(message, type = 'error') {
    const errorAlert = document.getElementById('error-alert');
    if (errorAlert) {
        errorAlert.textContent = message;
        errorAlert.classList.remove('d-none');
        errorAlert.className = `alert alert-${type === 'error' ? 'danger' : 'success'} d-block`;
        setTimeout(() => errorAlert.classList.add('d-none'), 5000);
    }

    // Также выводим в консоль
    if (type === 'error') {
        console.error(message);
    } else {
        console.log(message);
    }
}