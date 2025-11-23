// Tabla Clientes
document.addEventListener("DOMContentLoaded", function () {

    cargarClientes();

    // abrimos modal cando clicken o button + cliente novo
    document.getElementById('btnCrearCliente').onclick = function () {
        modalCrearCliente();
    };

    // button gardar cliente para o post
    document.getElementById('btnGuardarCliente').onclick = function () {
        crearCliente();
    }

});

// function badges colorear role
function getBadgeRoleClass(role) {
    if (role === 'ADMIN') return 'bg-warning text-black';
    if (role === 'USER') return 'bg-info text-black';
    if (role !== 'ADMIN' && role !== 'USER') return 'bg-secondary text-dark';
}


// function cargar clientes
function cargarClientes() {
    const tablaClientes = document.getElementById('tablaClientes');
    tablaClientes.innerHTML = 'Cargando clientes...';

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
                            <td><span class="badge ${getBadgeRoleClass(cliente.role)}">${cliente.role}</span></td>
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

            // tabla completa
            tablaClientes.innerHTML = tablaHTML;

            // DATATABLES.NET
            // destruir dataTables si existe
            if ($.fn.DataTable.isDataTable('#tablaClientes table')) {
                $('#tablaClientes table').DataTable().destroy();
            }
            // Activar DataTables
            $('#tablaClientes table').DataTable({
                dom: 'Bfrtip',  // B = Buttons, f = filtro (search), r = processing, t = tabla, i = info, p = paginación
                buttons: [
                    { extend: 'copy', text: 'Copiar' },
                    { extend: 'csv', text: 'CSV' },
                    {
                        extend: 'excel',
                        text: 'Excel',
                        title: 'Lista de Clientes',
                        exportOptions: {
                            columns: ':visible:not(:last-child)'  // Excluir columna Acciones
                        }
                    },
                    {
                        extend: 'pdf',
                        text: 'PDF',
                        title: 'Lista de Clientes',
                        orientation: 'landscape',  // Horizontal
                        pageSize: 'A4',
                        exportOptions: {
                            columns: ':visible:not(:last-child)'  // Excluir columna Acciones
                        }
                    },
                    { extend: 'print', text: 'Imprimir' }
                ]
            });

        })
        .catch(error => {
            console.error('Error al cargar clientes:', error);
            tablaClientes.innerHTML = 'Error al cargar los clientes.';
        });
}
// delete cliente
function deleteCliente(clienteId) {
    if (confirm("¿Estás seguro de que deseas eliminar este cliente?")) {
        fetch(`/clientes/${clienteId}`, {
            method: 'DELETE'
        })
            .then(response => {
                if (response.ok) {
                    alert('Cliente eliminado correctamente.');
                    cargarClientes(); // recargar clients
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

// function modalCrearCliente
function modalCrearCliente() {
    // Limpiar campos
    document.getElementById('formCrearCliente').reset();

    // cambiar titulo
    document.getElementById('modalClienteTitle').textContent = 'Crear Nuevo Cliente';
    document.getElementById('btnGuardarCliente').textContent = 'Guardar';

    // Mostrar modal
    setTimeout(() => {
        const modal = new bootstrap.Modal(document.getElementById('modalCrearCliente'));
        modal.show();
    }, 300);

}

// function crearCliente
function crearCliente() {
    const nombre = document.getElementById('nombreCliente').value.trim();
    const apellido = document.getElementById('apellidoCliente').value.trim();
    const email = document.getElementById('emailCliente').value.trim();
    const telefono = document.getElementById('telefonoCliente').value.trim();
    const direccion = document.getElementById('direccionCliente').value.trim();
    const password = document.getElementById('passwordCliente').value;
    const role = document.getElementById('roleCliente').value;

    //validacions
    if (!nombre || !apellido || !email || !password || !role || !telefono || !direccion) {
        alert('Por favor, completa los campos obligatorios.');
        return;
    }
    if (!telefono.match(/^[0-9]{9}$/)) {
        alert('El teléfono debe tener exactamente 9 dígitos numéricos.');
        return;
    }

    const nuevoCliente = {
        nombre: nombre,
        apellido: apellido,
        email: email,
        telefono: telefono,
        direccion: direccion,
        password: password,
        role: role
    };

    // Post cliente
    fetch('/clientes', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(nuevoCliente)
    })
        .then(response => {
            if (response.ok) {
                alert('Cliente creado correctamente.');
                cargarClientes(); // recargar clients

                setTimeout(() => {
                    const modal = bootstrap.Modal.getInstance(document.getElementById('modalCrearCliente'));
                    modal.hide();
                }, 300);
            } else if(response.status === 409) {
                    alert('El email ya está registrado. Por favor, utiliza otro email.');
            } else {
                alert('Error al crear el cliente.');
            }
        })
        .catch(error => {
            console.error('Error al crear cliente:', error);
            alert('Error al crear el cliente.');
        });
}