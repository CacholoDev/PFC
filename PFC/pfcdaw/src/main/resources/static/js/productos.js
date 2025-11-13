document.addEventListener("DOMContentLoaded", function () {
    cargarTablaProductos();

    //coller o button crear producto ca function modalCreateProducto para que abra o modal
    document.getElementById('btnCrearProducto').onclick = function () {
        modalCreateProducto();
    };

        // button gardar producto
    document.getElementById('btnGuardarProducto').onclick = function () {
       if (modoEdicion === false) {
        // crear
        crearProducto();
    } else {
        // editar
    }
    };
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

////////////////////////////DELETE//////////////////////////////////////////////////////////

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
            // Rellenar modal co nome do produto
            document.getElementById('nombreProductoEliminar').textContent = producto.nombre;
        });

    // 2.5. button onclick delete
    document.getElementById('btnConfirmarDelete').onclick = function () {
        confirmarEliminacion();
    };

    // 3. Abrir modal de confirmacion con settimeout
    setTimeout(() => {
        const modal = new bootstrap.Modal(document.getElementById('modalConfirmarDelete'));
        modal.show();
    }, 300);


}

// confirmarEliminacion function
function confirmarEliminacion() {
    fetch(`/productos/${productoIdAEliminar}`, {
        method: 'DELETE'
    })
        .then(response => {
            if (response.ok) {
                // Cerrar modal
                const modalElement = document.getElementById('modalConfirmarDelete');
                const modal = bootstrap.Modal.getInstance(modalElement);
                modal.hide();
                // Recargar tabla
                cargarTablaProductos();
                alert('Producto eliminado');
            } else {
                alert('Error al eliminar el producto');
            }
        }).catch(error => {
            console.error('Error al eliminar el producto:', error);
            alert('Error al eliminar el producto');
        });
}

///////////////////////////////////CREATE///////////////////////////////////////////////////
// variable global para modo crear + edit
let modoEdicion = false; 
let productoIdActual = null;

// function modalCreateProducto
function modalCreateProducto() {

    // limpar form
    document.getElementById('formCrearEditarProducto').reset();

    // cambiar titulo do modal
    document.getElementById('modalProductoTitle').textContent = 'Crear Nuevo Producto';

    // abrir modal con setTimeout
    setTimeout(() => {
        const modal = new bootstrap.Modal(document.getElementById('modalCrearEditarProducto'));
        modal.show();
    }, 300);

    modoEdicion = false;

}

//function crearProducto
function crearProducto() {

    // admin rellena o form e clica en gardar (con validacions)
    const nombre = document.getElementById('nombreProducto').value.trim();
    if (nombre === '' || nombre.length === 0) {
        alert('El nombre del producto es obligatorio');
        return;
    }
    const descripcion = document.getElementById('descripcionProducto').value.trim();
    const precio = parseFloat(document.getElementById('precioProducto').value);
    if(precio < 0 || isNaN(precio)) {
        alert('El precio debe ser un número positivo');
        return;
    }
    const stock = parseInt(document.getElementById('stockProducto').value, 10);
    if(stock < 0 || isNaN(stock)) {
        alert('El stock debe ser un número entero positivo');
        return;
    }

    // crear json producto
    const nuevoProducto = {
        nombre: nombre,
        descripcion: descripcion,
        precio: precio,
        stock: stock
    };

    // enviar POST o backend
    fetch('/productos', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(nuevoProducto)
    })
        .then(response => {
            if (response.ok) {
                // cerrar modal con timeout polos bugs oscuros esos
                setTimeout(() => {
                    const modalElement = document.getElementById('modalCrearEditarProducto');
                    const modal = bootstrap.Modal.getInstance(modalElement);
                    modal.hide();
                }, 300);

                // Recargar tabla
                cargarTablaProductos();
                alert('Producto creado');
            } else {
                alert('Error al crear el producto');
            }
        }).catch(error => {
            console.error('Error al crear el producto:', error);
            alert('Error al crear el producto');
        });
}


///////////////////////////////////EDIT///////////////////////////////////////////////////