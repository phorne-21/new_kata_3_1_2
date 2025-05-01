const api = {
    baseUrl: '/api',

    async getAllUsers() {
        const response = await fetch(`${this.baseUrl}/users`);
        return await response.json();
    },

    async getCurrentUser() {
        const response = await fetch(`${this.baseUrl}/users/me`);
        return await response.json();
    },

    async updateUser(userData) {
        const response = await fetch(`${this.baseUrl}/users/${userData.id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userData)
        });
        return await response.json();
    },

    async deleteUser(userId) {
        await fetch(`${this.baseUrl}/users/${userId}`, {
            method: 'DELETE'
        });
    },

    async createUser(userData) {
        const response = await fetch(`${this.baseUrl}/users`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userData)
        });
        return await response.json();
    }
};

// Вспомогательная функция для показа ошибок
function showError(message) {
    // Можно использовать Bootstrap Toast или простой alert
    alert(message);
}

// Экспорт для использования в других файлах
window.api = api;
window.showError = showError;