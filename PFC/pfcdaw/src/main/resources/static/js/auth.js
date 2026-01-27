// Wrapper simple: agrega el header Authorization con el token guardado en localStorage.
function fetchWithAuth(url, options = {}) {
    const token = localStorage.getItem('token');
    const headers = { ...(options.headers || {}) };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    // Si envías JSON, añade Content-Type cuando no esté definido.
    if (options.body && !headers['Content-Type'] && !(options.body instanceof FormData)) {
        headers['Content-Type'] = 'application/json';
    }

    return fetch(url, { ...options, headers })
        .then((response) => {
            if (response.status === 401) {
                window.location.href = '/login.html';
                throw new Error('No autenticado');
            }
            if (response.status === 403) {
                alert('No tienes permisos para esta acción');
                throw new Error('Prohibido');
            }
            return response;
        });
}