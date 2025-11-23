// === MIS PEDIDOS (Usuario) ===

document.addEventListener("DOMContentLoaded", function () {

    // 1. usuario do localStorage
    const usuarioTexto = localStorage.getItem("usuario");

    // 2. usuario logueado?
    if (!usuarioTexto) {
        window.location.href = "login.html";
        return;
    }

    // 3. Pasar de texto a obxeto
    const usuario = JSON.parse(usuarioTexto);

    // 4. si nn existe o usuario vai po dashboard que ten esto mismo e mais opcions
    if (usuario.rol === 'ADMIN') {
        window.location.href = "dashboard.html";
        return;
    }

    // 5. nombre user no navbar
    const nombreUsuarioElement = document.getElementById("nombreUsuario");
    nombreUsuarioElement.innerHTML = `<i class="bi bi-person-circle text-dark me-1"></i><b class="text-dark">Hola, bienvenid@! ${usuario.nombre} ${usuario.apellido} # ${usuario.email} </b>`;

    // 6. pedidos do usuario
    cargarPedidosUsuario(usuario.id);

    // 7. logout
    const btnLogout = document.getElementById("btnLogout");
    btnLogout.addEventListener("click", function () {
        localStorage.removeItem("usuario");
        window.location.href = "login.html";
    });
});

// === CARGAR PEDIDOS x ID USUARIO ===
function cargarPedidosUsuario(clienteId) {
    const tablaPedidosCliente = document.getElementById("tablaPedidosCliente");

    // Cargando pedidos.....
    tablaPedidosCliente.innerHTML = `
        <div class="loading-message">
            <span class="spinner-border spinner-border-sm me-2"></span>
            Cargando tus pedidos....
        </div>
    `;

    // fetch pedidos ID /cliente/{id}
    fetch(`/pedidos/cliente/${clienteId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error("Error al cargar pedidos");
            }
            return response.json();
        })
        .then(pedidos => {
            // Si nn hay pedidos, mostrar mensaje
            if (pedidos.length === 0) {
                tablaPedidosCliente.innerHTML = `
                    <div class="no-pedidos">
                        <i class="bi bi-cart-x-fill"></i>
                        <p class="mt-3">Aún no tienes pedidos realizados.</p>
                    </div>
                `;
                return;
            }

            // tabla HTML
            let tablaHTML = `
            <div class="table-responsive">
                <table class="table table-striped table-hover">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Fecha</th>
                            <th>Estado-Pedido</th>
                            <th>Total</th>
                            <th>Nº Líneas</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
            `;

            // for pedido, row nova
            pedidos.forEach(pedido => {
                // Format fecha
                const fecha = new Date(pedido.fechaPedido).toLocaleDateString('es-ES', {
                    year: 'numeric',
                    month: 'short',
                    day: 'numeric',
                    hour: '2-digit',
                    minute: '2-digit'
                });

                // colorear x estado pendiente, listo , entregado...
                let badgeClass = 'bg-secondary';
                if (pedido.estado === 'PENDIENTE') badgeClass = 'bg-warning text-dark';
                if (pedido.estado === 'EN_PREPARACION') badgeClass = 'bg-info text-dark';
                if (pedido.estado === 'LISTO') badgeClass = 'bg-primary text-dark';
                if (pedido.estado === 'ENTREGADO') badgeClass = 'bg-success text-dark';
                if (pedido.estado === 'CANCELADO') badgeClass = 'bg-danger text-dark';

                tablaHTML += `
                    <tr>
                        <td>${pedido.id}</td>
                        <td>${fecha}</td>
                        <td><span class="badge ${badgeClass}">${pedido.estado}</span></td>
                        <td>${Number(pedido.total).toFixed(2)} €</td>
                        <td>${pedido.lineasPedido?.length || 0}</td>
                        <td>
                            <button class="btn btn-sm btn-warning" title="Ver lineas del pedido" onclick="verDetallesPedido(${pedido.id})">
                                <i class="bi bi-eye"></i>
                            </button>
                        </td>
                    </tr>
                `;
            });

            tablaHTML += `
                    </tbody>
                </table>
            </div>
            `;

            // insertar tabla
            tablaPedidosCliente.innerHTML = tablaHTML;

            // DATATABLES.NET
            // destruir dataTables si existe
            if ($.fn.DataTable.isDataTable('#tablaPedidosCliente table')) {
                $('#tablaPedidosCliente table').DataTable().destroy();
            }
            // Activar DataTables
            $('#tablaPedidosCliente table').DataTable({
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
            console.error('Error al cargar pedidos:', error);
            tablaPedidosCliente.innerHTML = `
                <div class="alert alert-danger" role="alert">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i>
                    Error al cargar los pedidos. Intenta de nuevo más tarde.
                </div>
            `;
        });
}

// function po modal de ver detalles pedido #detallesPedidoBody
function verDetallesPedido(pedidoId) {
    fetch(`/pedidos/${pedidoId}`)
        .then(response => response.json())
        .then(pedido => {
            // pintar detalles no modal #detallesPedidoBody
            const detallesBody = document.getElementById("detallesPedidoBody");
            detallesBody.innerHTML = "";
            // pintar productos
            pedido.lineasPedido.forEach(linea => {
                const productoDiv = document.createElement("div");
                productoDiv.classList.add("producto-pedido");
                productoDiv.innerHTML = `
                    <h6><b>${linea.producto.nombre}</b></h6>
                    <p>Cantidad: ${linea.cantidad}</p>
                    <p>Precio unitario: ${linea.producto.precio.toFixed(2)} €</p>
                    <p>Subtotal: ${(linea.cantidad * linea.producto.precio).toFixed(2)} €</p>
                    <hr>
                `;
                detallesBody.appendChild(productoDiv);
            });
            // abrir modal
            setTimeout(() => {
                const detallesModal = new bootstrap.Modal(document.getElementById("modalDetallesPedido"));
                detallesModal.show();
            }, 300);
        });
}

