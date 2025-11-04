// === LOGIN ===

// Esperar pola carga DOM
document.addEventListener('DOMContentLoaded', function() {
    
    const loginForm = document.getElementById('loginForm');
    const btnLogin = document.getElementById('btnLogin');
    const alertError = document.getElementById('alertError');
    const errorMessage = document.getElementById('errorMessage');
    
    // Listener formulario
    loginForm.addEventListener('submit', async function(e) {
        e.preventDefault(); // Previr recarga
        
        // Obtener valores do form
        const email = document.getElementById('email').value.trim();
        const password = document.getElementById('password').value;
        
        // Ocultar alerta do error si ta visible co d-none de bootstrap
        alertError.classList.add('d-none');
        
        // Cambiar o button a "cargando..."
        btnLogin.disabled = true;
        btnLogin.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Iniciando sesión...';
        
        try {
            // Peeticion POST o backend
            const response = await fetch('http://localhost:8080/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email, password })
            });
            
            // Si a respuesta non e OK --> (401, 500, etc.)
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Credenciales inválidas');
            }
            
            // Obtener datos do usuario
            const data = await response.json();
            console.log('Login exitoso:', data);
                        
            // LocalStorage
            localStorage.setItem('usuario', JSON.stringify(data));
            
            // Redirixir x rol
            if (data.rol === 'ADMIN') {
                window.location.href = 'dashboard.html';
            } else {
                window.location.href = 'mis-pedidos.html';
            }
            
        } catch (error) {
            // Mostrar erro na alerta
            console.error('Error en login:', error);
            errorMessage.textContent = error.message || 'Error al iniciar sesión. Intenta de nuevo.';
            alertError.classList.remove('d-none');
            
            // Restauro button
            btnLogin.disabled = false;
            btnLogin.innerHTML = '<i class="bi bi-box-arrow-in-right me-2"></i>Iniciar sesión';
        }
    });
    
});
