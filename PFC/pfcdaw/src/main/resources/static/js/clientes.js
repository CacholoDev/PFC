// Tabla Clientes
let modoEdicionC = false;
let clienteIdEdicion = null;

document.addEventListener("DOMContentLoaded", function () {

    cargarClientes();

    // abrimos modal cando clicken o button + cliente novo
    document.getElementById('btnCrearCliente').onclick = function () {
        modalCrearCliente();
    };

    // button gardar/actualizar cliente
    document.getElementById('btnGuardarCliente').onclick = function () {
        if (modoEdicionC) {
            actualizarCliente(clienteIdEdicion);
        } else {
            crearCliente();
        }
    }

});

// function badges colorear role
function getBadgeRoleClass(role) {
    if (role === 'ADMIN') return 'bg-dark text-warning';
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
                                <th>Direccion</th>
                                <th>Fecha Alta</th>
                                <th>Empresa</th>
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
                            <td>${cliente.direccion}</td>
                            <td>${new Date(cliente.fechaAlta).toLocaleDateString('es-ES')}</td>
                            <td>${cliente.nombreEmpresa}</td>
                            <td>${cliente.telefono}</td>
                            <td><span class="badge ${getBadgeRoleClass(cliente.role)}">${cliente.role}</span></td>
                            <td>
                                <button class="btn btn-sm btn-warning" title="Editar cliente" onclick="editarCliente(${cliente.id})">
                                    <i class="bi bi-pencil-square"></i>
                                </button>

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
                language: {
                    url: 'js/dataTables/dataTables-ES.json'
                },
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

// function modal crear cliente
function modalCrearCliente() {
    // limpar formulario
    document.getElementById('formCrearCliente').reset();

    // activar modo creación
    modoEdicionC = false;
    clienteIdEdicion = null;

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
    const empresa = document.getElementById('nombreEmpresaCliente').value.trim();
    const password = document.getElementById('passwordCliente').value;
    const role = document.getElementById('roleCliente').value;

    //validacions
    if (password.length < 5) {
        alert('La contraseña debe tener al menos 4 caracteres.');
        return;
    }
    if (!nombre || !apellido || !email || !password || !role || !telefono || !direccion || !empresa) {
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
        nombreEmpresa: empresa,
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
            } else if (response.status === 409) {
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

// function editarCliente
function editarCliente(clienteId) {
    // activar modo edición
    modoEdicionC = true;
    clienteIdEdicion = clienteId;

    // fetch cliente por id
    fetch(`/clientes/${clienteId}`)
        .then(response => response.json())
        .then(cliente => {
            // llenar formulario
            document.getElementById('nombreCliente').value = cliente.nombre;
            document.getElementById('apellidoCliente').value = cliente.apellido;
            document.getElementById('emailCliente').value = cliente.email;
            document.getElementById('telefonoCliente').value = cliente.telefono;
            document.getElementById('direccionCliente').value = cliente.direccion;
            document.getElementById('nombreEmpresaCliente').value = cliente.nombreEmpresa;
            document.getElementById('passwordCliente').value = cliente.password;
            document.getElementById('roleCliente').value = cliente.role;

            // cambiar titulo
            document.getElementById('modalClienteTitle').textContent = `Editar Cliente #${clienteId}`;
            document.getElementById('btnGuardarCliente').textContent = 'Actualizar';

            // Mostrar modal
            setTimeout(() => {
                const modal = new bootstrap.Modal(document.getElementById('modalCrearCliente'));
                modal.show();
            }, 300);
        });
}

// function actualizarCliente
function actualizarCliente(clienteId) {

    // recopilar datos do formulario
    const nombre = document.getElementById('nombreCliente').value.trim();
    const apellido = document.getElementById('apellidoCliente').value.trim();
    const email = document.getElementById('emailCliente').value.trim();
    const telefono = document.getElementById('telefonoCliente').value.trim();
    const direccion = document.getElementById('direccionCliente').value.trim();
    const empresa = document.getElementById('nombreEmpresaCliente').value.trim();
    const password = document.getElementById('passwordCliente').value;
    const role = document.getElementById('roleCliente').value;

    //validacions
    if(password.length < 5) {
        alert('La contraseña debe tener al menos 4 caracteres.');
        return;
    }

    if (!nombre || !apellido || !email || !role || !telefono || !direccion || !empresa || !password) {
        alert('Por favor, todos los campos son obligatorios.');
        return;
    }

    if (!telefono.match(/^[0-9]{9}$/)) {
        alert('El teléfono debe tener exactamente 9 dígitos numéricos.');
        return;
    }

    // crear obxeto cliente
    const clienteActualizado = {
        id: clienteId, // ✅ IMPORTANTE: incluir el ID para que JPA actualice en vez de crear
        nombre: nombre,
        apellido: apellido,
        email: email,
        telefono: telefono,
        direccion: direccion,
        nombreEmpresa: empresa,
        password: password,
        role: role
    };

    // enviar PUT request para actualizar cliente
    fetch(`/clientes/${clienteId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(clienteActualizado)
    })
        .then(response => {
            if (response.ok) {
                alert('Cliente actualizado correctamente.');
                cargarClientes(); // recargar clientes

                setTimeout(() => {
                    const modal = bootstrap.Modal.getInstance(document.getElementById('modalCrearCliente'));
                    modal.hide();
                }, 300);
            } else if (response.status === 409) {
                alert('El email ya está registrado. Por favor, utiliza otro email.');
            } else {
                alert('Error al actualizar el cliente.');
            }
        })
        .catch(error => {
            console.error('Error al actualizar cliente:', error);
            alert('Error al actualizar el cliente.');
        });
}