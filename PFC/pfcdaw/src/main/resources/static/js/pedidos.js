document.addEventListener("DOMContentLoaded", function () {
    cargarPedidos();
});

// function para colorear badges x estado
function getBadgeClass(estado) {
    if (estado === 'PENDIENTE') return 'bg-warning text-dark';
    if (estado === 'EN_PREPARACION') return 'bg-info text-dark';
    if (estado === 'LISTO') return 'bg-primary text-dark';
    if (estado === 'ENTREGADO') return 'bg-success text-dark';
    if (estado === 'CANCELADO') return 'bg-danger text-dark';
    return 'bg-secondary text-dark';
}

// cargarPedidos (recarga desde cambiarEstadoPedido)
function cargarPedidos() {
    const tablaPedidos = document.getElementById('tablaPedidos');
    tablaPedidos.innerHTML = 'Cargando pedidos...';

    fetch('/pedidos')
        .then(response => response.json())
        .then(data => {

            let tablaHTML = `
                <table class="table table-striped table-hover">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Cliente</th>
                            <th>Fecha</th>
                            <th>Total</th>
                            <th>Estado-Pedido</th>
                            <th>NºLineas</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${data.map(pedido => `
                            <tr>
                                <td>${pedido.id}</td>
                                <td>${pedido.cliente.nombre} ${pedido.cliente.apellido} # ${pedido.cliente.email}</td>
                                <td>${new Date(pedido.fechaPedido).toLocaleString()}</td>
                                <td>${pedido.total.toFixed(2)} €</td>
                                <td><span class="badge ${getBadgeClass(pedido.estado)}">${pedido.estado}</span></td>
                                <td>${pedido.lineasPedido?.length || 0}</td>
                                <td>
                                    <button class="btn btn-sm btn-warning" title="Ver detalles del pedido" onclick="verDetallesPedido(${pedido.id})">
                                        <i class="bi bi-eye"></i>
                                    </button>
                                </td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            `;
            tablaPedidos.innerHTML = tablaHTML;
        });
}


// ver detalles pedido
function verDetallesPedido(pedidoId) {
    fetch(`/pedidos/${pedidoId}`)
        .then(response => response.json())
        .then(pedido => {
            // logs pa ver o que trae o pedido + lineas
            console.log('📦 PEDIDO COMPLETO:', pedido);
            console.log('📋 LÍNEAS:', pedido.lineasPedido);

            // 1. Rellenar ID do pedido no titulo
            document.getElementById('pedidoIdModal').textContent = pedido.id;

            // 2. Rellenar info do pedido + estado con badge coloreado
            const badgeClass = getBadgeClass(pedido.estado);

            const infoPedido = `
                <p><strong>Cliente:</strong> ${pedido.cliente.nombre} ${pedido.cliente.apellido} (${pedido.cliente.email})</p>
                <p><strong>Fecha:</strong> ${new Date(pedido.fechaPedido).toLocaleString()}</p>
                <p><strong>Estado:</strong> <span class="badge ${badgeClass}">${pedido.estado}</span></p>
            `;
            document.getElementById('infoPedidoModal').innerHTML = infoPedido;

            // 3. estado actual do select
            document.getElementById('selectEstadoPedido').value = pedido.estado;

            // 4. onclick do button pa cambiar o estado
            document.getElementById('btnCambiarEstado').onclick = function () {
                cambiarEstadoPedido(pedido.id);
            };

            // 5. rellenar tabla das lineas
            const tbody = document.getElementById('lineasPedidoBody');
            tbody.innerHTML = ''; // Limpar 
            pedido.lineasPedido.forEach(linea => {
                const subtotal = linea.cantidad * linea.producto.precio;
                tbody.innerHTML += `
                    <tr>
                        <td>${linea.producto.nombre}</td>
                        <td>${linea.producto.precio.toFixed(2)} €</td>
                        <td>${linea.cantidad}</td>
                        <td>${subtotal.toFixed(2)} €</td>
                    </tr>
                `;
            });

            // 6. Rellenar total
            document.getElementById('totalPedidoModal').textContent = pedido.total.toFixed(2);
            
            // 7. Mostrar modal con setTimeout pa evitar conflitos do boostrap 
            setTimeout(() => {
                const modal = new bootstrap.Modal(document.getElementById('modalDetallesPedido'));
                modal.show();
            }, 300);
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error al cargar los detalles del pedido');
        });
}

// function cambiar estado do pedido
function cambiarEstadoPedido(pedidoId) {
    const nuevoEstado = document.getElementById('selectEstadoPedido').value;
    const btnCambiarEstado = document.getElementById('btnCambiarEstado');

    // button disabled e cargando
    btnCambiarEstado.disabled = true;
    btnCambiarEstado.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span>Actualizando...';

    // Construir obxeto pedido co novo estado
    const pedidoActualizado = {
        estado: nuevoEstado
    };
    // enviando o backend modo put
    fetch(`/pedidos/${pedidoId}/estado`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(pedidoActualizado)
    })
        .then(response => {
            console.log('📡 Respuesta del servidor:', response);
            console.log('📊 Status:', response.status);
            if (!response.ok) {
                throw new Error('Error al cambiar el estado del pedido');
            }
            return response.json();
        })
        .then(pedidoActualizado => {
            console.log('✅ Estado actualizado:', pedidoActualizado);

            // exito
            alert(`Estado del pedido #${pedidoId} actualizado a: ${nuevoEstado}`);

            // pecho modal co setTimeout
            setTimeout(() => {
                const modal = bootstrap.Modal.getInstance(document.getElementById('modalDetallesPedido'));
                modal.hide();
            }, 300);

            // Recargar tabla pedidos
            cargarPedidos();

            // button enabled
            btnCambiarEstado.disabled = false;
            btnCambiarEstado.innerHTML = '<i class="bi bi-arrow-repeat me-1"></i>Actualizar Estado';
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error al cambiar el estado del pedido: ' + error.message);

            // button enabled
            btnCambiarEstado.disabled = false;
            btnCambiarEstado.innerHTML = '<i class="bi bi-arrow-repeat me-1"></i>Actualizar Estado';
        });
}