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