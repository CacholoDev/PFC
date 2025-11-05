// Tabla Clientes
document.addEventListener("DOMContentLoaded", function () {

    function cargarClientes() {
        const tablaClientes = document.getElementById('tablaClientes');
        tablaClientes.innerHTML = 'Cargando clientes...'; // Mostrar mensaje de carga

        fetch('/clientes')
            .then(response => response.json())
            .then(data => {
                // Crear tabla
                let tablaHTML = `
                    <table class="table table-striped table-hover">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Nombre</th>
                                <th>Email</th>
                                <th>Teléfono</th>
                                <th>Rol</th>
                            </tr>
                        </thead>
                        <tbody>
                `;

                // por cada cliente, fila nova
                data.forEach(cliente => {
                    tablaHTML += `
                        <tr>
                            <td>${cliente.id}</td>
                            <td>${cliente.nombre}</td>
                            <td>${cliente.email}</td>
                            <td>${cliente.telefono}</td>
                            <td>${cliente.role}</td>
                        </tr>
                    `;
                });

                tablaHTML += `
                        </tbody>
                    </table>
                `;

                // Insertar la tabla completa en el contenedor
                tablaClientes.innerHTML = tablaHTML;
            })
            .catch(error => {
                console.error('Error al cargar clientes:', error);
                tablaClientes.innerHTML = 'Error al cargar los clientes.';
            });
    }

    cargarClientes();
});