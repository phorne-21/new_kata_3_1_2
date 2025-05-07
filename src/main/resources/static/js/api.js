const api = {
    baseUrl: '',

    async request(url, method, body = null) {
        const options = {
            method, headers: {
                'Content-Type': 'application/json', 'Accept': 'application/json'
            }, credentials: 'include'
        };

        if (body) {
            options.body = JSON.stringify(body);
        }

        const response = await fetch(`${this.baseUrl}${url}`, options);

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Request failed');
        }

        return response.json();
    },

    async login(credentials) {
        return this.request('/login', 'POST', credentials);
    },

    // async logout() {
    //     return this.request('/auth/logout', 'POST');
    // },

    async logout() {
        const response = await fetch('/logout', {
            method: 'POST', credentials: 'include' // важно для передачи сессии
        });

        if (!response.ok) {
            throw new Error('Logout failed');
        }
    },

    async getCurrentUser() {
        return this.request('/user', 'GET');
    },

    async getAllUsers() {
        return this.request('/admin/users', 'GET');
    },

    async getUserById(id) {
        return this.request(`/admin/${id}`, 'GET');
    },

    async createUser(userData) {
        return this.request('/admin/users', 'POST', userData);
    },

    async updateUser(id, userData) {
        return this.request(`/admin/users/${id}`, 'PUT', userData);
    },

    async deleteUser(id) {
        return this.request(`/admin/users/${id}`, 'DELETE');
    },

    async getAllRoles() {
        return this.request('/admin/roles', 'GET');
    }
};

window.api = api;