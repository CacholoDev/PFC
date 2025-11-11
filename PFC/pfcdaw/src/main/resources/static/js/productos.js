document.addEventListener("DOMContentLoaded", function () {
    cargarTablaProductos();
});

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
                                <th>Acciones</th>
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
                            <td>
                                <button class="btn btn-sm btn-warning" onclick="editarProducto(${producto.id})">
                                    <i class="bi bi-pencil-square"></i>
                                </button>
                                <button class="btn btn-sm btn-danger" onclick="deleteProducto(${producto.id})">
                                    <i class="bi bi-trash-fill"></i>
                                </button>
                            </td>
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


// Delete Producto co modal de confirmacion
// variable global para almacenar ID do producto a eliminar
let productoIdAEliminar = null; 

// function deleteProducto(id)
function deleteProducto(id) {
    // 1. gardar ID do producto a eliminar
    productoIdAEliminar = id;
    
    // 2. fetch GET /productos/{id} para obter o nome #nombreProductoEliminar
    fetch(`/productos/${id}`)
        .then(response => response.json())
        .then(producto => {
            // Rellenar contido do modal co nome do produto
            document.getElementById('nombreProductoEliminar').textContent = producto.nombre;
        });
        
    
    // 3. Abrir modal de confirmacion con settimeout
    setTimeout(() => {
        const modal = new bootstrap.Modal(document.getElementById('modalConfirmarDelete'));
        modal.show();
    }, 300);


}.

/* ou duas 2 functions function confirmarEliminacion() {
    // Fetch DELETE usando productoIdAEliminar
}
// Quitar listeners anteriores (para evitar duplicados)
    const btnConfirmar = document.getElementById('btnConfirmarDelete');
    const nuevoBtn = btnConfirmar.cloneNode(true);
    btnConfirmar.parentNode.replaceChild(nuevoBtn, btnConfirmar);
    
    // Añadir listener NUEVO
    nuevoBtn.addEventListener('click', function() {
        fetch(`/productos/${productoIdAEliminar}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (response.ok) {
                modal.hide();
                cargarTablaProductos();
                alert('Producto eliminado');
            }
        });
    });
*/