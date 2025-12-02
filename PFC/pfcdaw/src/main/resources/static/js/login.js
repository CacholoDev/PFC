// === LOGIN ===

// Esperar pola carga DOM
document.addEventListener('DOMContentLoaded', function () {

    // Listener formulario
    const loginForm = document.getElementById('loginForm');
    loginForm.addEventListener('submit', function (e) {
        e.preventDefault(); // Previr recarga
        Login();
    });

    // Listener boton registro (abre modal)
    const btnRegister = document.getElementById("btnRegister");
    btnRegister.addEventListener("click", function () {
        registrarse();
    });

    // Listener formulario registro
    const registerForm = document.getElementById('registerForm');
    registerForm.addEventListener('submit', function (e) {
        console.log('Registro submit');
        e.preventDefault(); // Previr recarga
        crearCuenta();
    });


});

async function Login() {

    const btnLogin = document.getElementById('btnLogin');
    const alertError = document.getElementById('alertError');
    const errorMessage = document.getElementById('errorMessage');


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
        const response = await fetch('/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, password })
        });

        // Si a resposta non e OK --> (401, 500, etc.)
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

}

function registrarse() {
    // Limpiar campos
    document.getElementById('registerForm').reset();

    // Mostrar modal
    setTimeout(() => {
        const modalRegister = new bootstrap.Modal(document.getElementById('modalRegister'));
        modalRegister.show();
    }, 300);



}

async function crearCuenta() {
    const nombre = document.getElementById('nombreUser').value.trim();
    const apellido = document.getElementById('apellidoUser').value.trim();
    const email = document.getElementById('emailUser').value.trim();
    const tlf = document.getElementById('telefonoUser').value.trim();
    const direccion = document.getElementById('direccionUser').value.trim();
    const nombreEmpresa = document.getElementById('nombreEmpresaUser').value.trim();
    const password = document.getElementById('passwordUser').value;
    const confirmPass = document.getElementById('confirmPasswordUser').value;

    // Validar password
    if (password !== confirmPass) {
        alert('Las contraseñas no coinciden.');
        return;
    }

    const nuevoUsuario = {
        nombre: nombre,
        apellido: apellido,
        email: email,
        direccion: direccion,
        nombreEmpresa: nombreEmpresa,
        telefono: tlf,
        password: password,
    };

    // Post usuario
    try {
        const response = await fetch('/clientes', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(nuevoUsuario)
        });
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Error al crear la cuenta');
        }
        const data = await response.json();
        alert('Cuenta creada con éxito. Ahora puedes iniciar sesión.');
        console.log('Usuario registrado:', data);
        // Cerrar modal
        setTimeout(() => {
            const modalElement = document.getElementById('modalRegister');
            const modal = bootstrap.Modal.getInstance(modalElement);
            modal.hide();
        }, 300);
    } catch (error) {
        console.error('Error al crear cuenta:', error);
        alert('Error al crear la cuenta: ' + error.message);
    }


}