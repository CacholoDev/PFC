document.addEventListener("DOMContentLoaded", function () {

    function cargarTablaProductos() {

        const tablaProductos = document.getElementById('tablaProductos');
        tablaProductos.innerHTML = 'Cargando productos...'; // Mensaje de carga

        fetch('/productos')
            .then(response => response.json())
            .then(data => {
                let tablaHTML = `
                    <table class="table table-striped table-hover">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Nombre</th>
                                <th>Precio</th>
                                <th>Stock</th>
                            </tr>
                        </thead>
                        <tbody>
                `;

                data.forEach(producto => {
                    tablaHTML += `
                        <tr>
                            <td>${producto.id}</td>
                            <td>${producto.nombre}</td>
                            <td>${producto.precio}</td>
                            <td>${producto.stock}</td>
                        </tr>
                    `;
                });

                tablaHTML += `
                        </tbody>
                    </table>
                `;

                tablaProductos.innerHTML = tablaHTML;
            })
            .catch(error => {
                console.error('Error al cargar productos:', error);
                tablaProductos.innerHTML = 'Error al cargar los productos.';
            });
    }

    cargarTablaProductos();
});