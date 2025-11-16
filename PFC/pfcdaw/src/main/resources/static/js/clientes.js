// Tabla Clientes
document.addEventListener("DOMContentLoaded", function () {

    cargarClientes();

});

// function cargar clientes
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
                                <th>Acciones</th>
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
                            <td>
                                <button class="btn btn-sm btn-danger" title="Eliminar cliente" onclick="deleteCliente(${cliente.id})">
                                    <i class="bi bi-trash"></i>
                                </button>
                            </td>
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

// eliminar cliente
function deleteCliente(clienteId) {
    if (confirm("¿Estás seguro de que deseas eliminar este cliente?")) {
        fetch(`/clientes/${clienteId}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (response.ok) {
                alert('Cliente eliminado correctamente.');
                cargarClientes(); // Recargar la lista de clientes después de eliminar
            } else {
                alert('Error al eliminar el cliente.');
            }
        })
        .catch(error => {
            console.error('Error al eliminar cliente:', error);
            alert('Error al eliminar el cliente.');
        });
    }
}