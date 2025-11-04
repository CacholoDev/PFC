document.addEventListener("DOMContentLoaded", function () {
    
    // 1. Obter usuario do localStorage
    const usuarioTexto = localStorage.getItem("usuario");
    
    // 2. Si non existe → redirixe o login
    if (!usuarioTexto) {
        window.location.href = 'login.html';
        return;  // Deter execución
    }

    // 3. Convertir de texto a objeto (1º parsear, 2º usar)
    const usuario = JSON.parse(usuarioTexto);

    // 4. Si o rol != ADMIN → redirixe a mis-pedidos
    if (usuario.rol !== 'ADMIN') {
        window.location.href = 'mis-pedidos.html';
        return;  // Deter execución
    }

    // 5. Mostrar nombre do usuario no navbar
    const nombreUsuarioElement = document.getElementById("nombreUsuario");
    nombreUsuarioElement.textContent = `Bienvenid@: ${usuario.nombre}`;

    // 6. Evento do buttn para pechar sesion
    const btnLogout = document.getElementById("btnLogout");
    btnLogout.addEventListener("click", function() {
        // Borrar usuario do localStorage
        localStorage.removeItem("usuario");
        // Redirxe o login
        window.location.href = "login.html";
    });

});
