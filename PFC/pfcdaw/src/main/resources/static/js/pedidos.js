document.addEventListener("DOMContentLoaded", function () {

    function cargarPedidos() {

        const tablaPedidos = document.getElementById('tablaPedidos');
        tablaPedidos.innerHTML = 'Cargando pedidos...'; // Mensaje de carga

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
                                <th>NºLineas(NºProductos)</th>
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
                                    <td>${pedido.estado}</td>
                                    <td>${pedido.lineasPedido?.length || 0}</td>
                                    <td>
                                        <button class="btn btn-sm btn-warning" onclick="verDetallesPedido(${pedido.id})">
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

    cargarPedidos();

});

// Función para ver detalles del pedido
function verDetallesPedido(pedidoId) {
    fetch(`/pedidos/${pedidoId}`)
        .then(response => response.json())
        .then(pedido => {
            // logs pa ver o que trae o pedido + lineas
            console.log('📦 PEDIDO COMPLETO:', pedido);  
            console.log('📋 LÍNEAS:', pedido.lineasPedido);
            // 1. Rellenar ID do pedido no título
            document.getElementById('pedidoIdModal').textContent = pedido.id;
            
            // 2. Rellenar info do pedido
            const infoPedido = `
                <p><strong>Cliente:</strong> ${pedido.cliente.nombre} ${pedido.cliente.apellido} (${pedido.cliente.email})</p>
                <p><strong>Fecha:</strong> ${new Date(pedido.fechaPedido).toLocaleString()}</p>
                <p><strong>Estado:</strong> <span class="badge bg-warning text-dark">${pedido.estado}</span></p>
            `;
            document.getElementById('infoPedidoModal').innerHTML = infoPedido;
            
            // 3. Rellenar tabla das lineas
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
            
            // 4. Rellenar total
            document.getElementById('totalPedidoModal').textContent = pedido.total.toFixed(2);
            
            // 5. Abrir modal
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

/*
<td>
  <select class="form-select form-select-sm" onchange="cambiarEstadoPedido(${pedido.id}, this.value)">
    <option value="PENDIENTE" ${pedido.estado === 'PENDIENTE' ? 'selected' : ''}>PENDIENTE</option>
    <option value="EN_PREPARACION" ${pedido.estado === 'EN_PREPARACION' ? 'selected' : ''}>EN_PREPARACION</option>
    <option value="LISTO" ${pedido.estado === 'LISTO' ? 'selected' : ''}>LISTO</option>
    <option value="ENTREGADO" ${pedido.estado === 'ENTREGADO' ? 'selected' : ''}>ENTREGADO</option>
    <option value="CANCELADO" ${pedido.estado === 'CANCELADO' ? 'selected' : ''}>CANCELADO</option>
  </select>
</td>
*/

/*
// function cambiarEstadoPedido(pedidoId, nuevoEstado) 
function cambiarEstadoPedido(pedidoId, nuevoEstado) {
    fetch(`/pedidos/${pedidoId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ estado: nuevoEstado })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Error al cambiar el estado del pedido');
        }
        cargarPedidos(); // Recargar la tabla de pedidos después de cambiar el estado
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error al cambiar el estado del pedido');
    });

}
    */