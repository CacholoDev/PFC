// Función cargarPedidos (scope global para poder recargar desde cambiarEstadoPedido)
function cargarPedidos() {
    const tablaPedidos = document.getElementById('tablaPedidos');
    tablaPedidos.innerHTML = 'Cargando pedidos...'; // Mensaje de carga

    fetch('/pedidos')
        .then(response => response.json())
        .then(data => {
            // Colorear badges según estado
            const getBadgeClass = (estado) => {
                if (estado === 'PENDIENTE') return 'bg-warning text-dark';
                if (estado === 'EN_PREPARACION') return 'bg-info text-dark';
                if (estado === 'LISTO') return 'bg-primary text-dark';
                if (estado === 'ENTREGADO') return 'bg-success text-dark';
                if (estado === 'CANCELADO') return 'bg-danger text-dark';
                return 'bg-secondary text-dark';
            };
            
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
                                <td><span class="badge ${getBadgeClass(pedido.estado)}">${pedido.estado}</span></td>
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

document.addEventListener("DOMContentLoaded", function () {
    cargarPedidos();
    
    // Listener para recargar pedidos desde otras funciones
    document.addEventListener('cargarPedidos', cargarPedidos);
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
            
            // 2. Rellenar info do pedido con badge coloreado
            let badgeClass = 'bg-secondary';
            if (pedido.estado === 'PENDIENTE') badgeClass = 'bg-warning text-dark';
            if (pedido.estado === 'EN_PREPARACION') badgeClass = 'bg-info text-dark';
            if (pedido.estado === 'LISTO') badgeClass = 'bg-primary text-dark';
            if (pedido.estado === 'ENTREGADO') badgeClass = 'bg-success text-dark';
            if (pedido.estado === 'CANCELADO') badgeClass = 'bg-danger text-dark';

            const infoPedido = `
                <p><strong>Cliente:</strong> ${pedido.cliente.nombre} ${pedido.cliente.apellido} (${pedido.cliente.email})</p>
                <p><strong>Fecha:</strong> ${new Date(pedido.fechaPedido).toLocaleString()}</p>
                <p><strong>Estado:</strong> <span class="badge ${badgeClass}">${pedido.estado}</span></p>
            `;
            document.getElementById('infoPedidoModal').innerHTML = infoPedido;
            
            // 3. Establecer estado actual en el selector
            document.getElementById('selectEstadoPedido').value = pedido.estado;
            
            // 4. Configurar botón de cambiar estado (remover listeners previos)
            const btnCambiarEstado = document.getElementById('btnCambiarEstado');
            const newBtn = btnCambiarEstado.cloneNode(true);
            btnCambiarEstado.parentNode.replaceChild(newBtn, btnCambiarEstado);
            
            // 5. Añadir listener al nuevo botón
            newBtn.addEventListener('click', function() {
                cambiarEstadoPedido(pedido.id);
            });
            
            // 6. Rellenar tabla das lineas
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
            
            // 7. Rellenar total
            document.getElementById('totalPedidoModal').textContent = pedido.total.toFixed(2);
            
            // 8. Abrir modal con setTimeout de 500ms
            setTimeout(() => {
                const modal = new bootstrap.Modal(document.getElementById('modalDetallesPedido'));
                modal.show();
            }, 500);
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error al cargar los detalles del pedido');
        });
}

// Función para cambiar estado del pedido
function cambiarEstadoPedido(pedidoId) {
    const nuevoEstado = document.getElementById('selectEstadoPedido').value;
    const btnCambiarEstado = document.getElementById('btnCambiarEstado');
    
    // Deshabilitar botón y mostrar loading
    btnCambiarEstado.disabled = true;
    btnCambiarEstado.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span>Actualizando...';
    
    // Construir objeto pedido con el nuevo estado
    const pedidoActualizado = {
        estado: nuevoEstado
    };
    
    fetch(`/pedidos/${pedidoId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(pedidoActualizado)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Error al cambiar el estado del pedido');
        }
        return response.json();
    })
    .then(pedidoActualizado => {
        console.log('✅ Estado actualizado:', pedidoActualizado);
        
        // Mostrar mensaje de éxito
        alert(`Estado del pedido #${pedidoId} actualizado a: ${nuevoEstado}`);
        
        // Cerrar modal con setTimeout de 500ms
        setTimeout(() => {
            const modal = bootstrap.Modal.getInstance(document.getElementById('modalDetallesPedido'));
            modal.hide();
        }, 500);
        
        // Recargar tabla de pedidos
        const cargarPedidosEvent = new Event('cargarPedidos');
        document.dispatchEvent(cargarPedidosEvent);
        
        // Re-habilitar botón
        btnCambiarEstado.disabled = false;
        btnCambiarEstado.innerHTML = '<i class="bi bi-arrow-repeat me-1"></i>Actualizar Estado';
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error al cambiar el estado del pedido: ' + error.message);
        
        // Re-habilitar botón
        btnCambiarEstado.disabled = false;
        btnCambiarEstado.innerHTML = '<i class="bi bi-arrow-repeat me-1"></i>Actualizar Estado';
    });
}