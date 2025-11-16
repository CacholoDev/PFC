// variable global para modo crear + edit
let modoEdicion = false;
let productoIdActual = null;

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
            editarProducto();
        }
    };

    // button añadir stock
    document.getElementById('btnAñadirStock').onclick = function () {
        aumentarStock();
    };

    // button reducir stock
    document.getElementById('btnReducirStock').onclick = function () {
        reducirStock();
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
                            <td>${producto.precio.toFixed(2)}€</td>
                            <td>${producto.stock}</td>
                            <td>
                                <button class="btn btn-sm btn-warning" title="Editar producto" onclick="modalEditarProducto(${producto.id})">
                                    <i class="bi bi-pencil-square"></i>
                                </button>
                                <button class="btn btn-sm btn-warning" title="Gestionar stock" onclick="modalEditarStock(${producto.id})">
                                    <i class="bi bi-boxes"></i>
                                </button>
                                <button class="btn btn-sm btn-danger" title="Eliminar producto" onclick="deleteProducto(${producto.id})">
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

// function modalCreateProducto
function modalCreateProducto() {

    // limpar form
    document.getElementById('formCrearEditarProducto').reset();

    // activar input stock
    document.getElementById('stockProducto').disabled = false;
    //quitar el mensaje de editar stock
    document.getElementById('tituloStockModal').innerHTML = 'Stock';

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
    const precioRaw = document.getElementById('precioProducto').value.replace(',', '.');
    const precio = parseFloat(precioRaw);
    if (precio < 0 || isNaN(precio)) {
        alert('El precio debe ser un número positivo');
        return;
    }
    const stock = parseInt(document.getElementById('stockProducto').value, 10);
    if (stock < 0 || isNaN(stock)) {
        alert('El stock debe ser un número positivo');
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
// function editProducto(id)
function modalEditarProducto(id) {
    modoEdicion = true;
    productoIdActual = id;

    fetch(`/productos/${id}`)
        .then(response => response.json())
        .then(producto => {
            // 1. Rellenar form coos datos do producto
            document.getElementById('nombreProducto').value = producto.nombre;
            document.getElementById('descripcionProducto').value = producto.descripcion.trim();
            document.getElementById('precioProducto').value = producto.precio.toFixed(2);
            document.getElementById('stockProducto').value = producto.stock;

            // 2. cambiando titulo do modal
            document.getElementById('modalProductoTitle').textContent = 'Editar Producto';
            //poñer o button do stock mentras editamos producto en disabled
            document.getElementById('stockProducto').disabled = true;
            //escribir que para editar vaia o boton de edicion de stock
            document.getElementById('tituloStockModal').innerHTML = '<small class="text-muted"><strong>Para editar el stock, utiliza el botón de gestión de stock <i class="bi bi-boxes"></i></strong></small>';

            // 3. abrir modal con setTimeout
            setTimeout(() => {
                const modal = new bootstrap.Modal(document.getElementById('modalCrearEditarProducto'));
                modal.show();
            }, 300);

        }).catch(error => {
            console.error('Error al obtener el producto:', error);
            alert('Error al obtener el producto');
        });
}
//function confirmar PutProducto
function editarProducto() {

    // 4. obter novos valores do input
    let name = document.getElementById('nombreProducto').value.trim();
    if (name === '' || name.length === 0) {
        alert('El nombre del producto es obligatorio');
        return;
    }
    let descripcion = document.getElementById('descripcionProducto').value.trim();
    let precioRaw = document.getElementById('precioProducto').value.replace(',', '.');
    let precio = parseFloat(precioRaw);
    if (precio < 0 || isNaN(precio)) {
        alert('El precio debe ser un número positivo');
        return;
    }

    // 5. crear json producto editado
    const productoEditado = {
        nombre: name,
        descripcion: descripcion,
        precio: precio,
    };
    console.log('Producto editado:', productoEditado);

    // 6. Enviar PUT o backend
    fetch(`/productos/${productoIdActual}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(productoEditado)
    })
        .then(response => {
            if (response.ok) {
                // pechar modal con timeout polos bugs oscuros esos
                setTimeout(() => {
                    const modalElement = document.getElementById('modalCrearEditarProducto');
                    const modal = bootstrap.Modal.getInstance(modalElement);
                    modal.hide();
                }, 300);
                // Recargar tabla
                cargarTablaProductos();
                alert('Producto editado con éxito');
            } else {
                alert('Error al editar el producto');
            }
        }).catch(error => {
            console.error('Error al editar el producto:', error);
            alert('Error al editar el producto');
        });

}


///////////////////////////////////////MANEXAR_STOCK////////////////////////////////////
// function modalEditarStock(id)
function modalEditarStock(id) {
    productoIdActual = id;

    fetch(`/productos/${id}`)
        .then(response => response.json())
        .then(producto => {
            // 1. Rellenar form coos datos do producto
            document.getElementById('nombreProductoStock').value = producto.nombre;
            document.getElementById('stockActualProducto').value = producto.stock;

            // 2. cambiando titulo do modal
            document.getElementById('modalStockTitle').textContent = 'Gestionar Stock Producto';

            // 3. abrir modal con setTimeout
            setTimeout(() => {
                const modal = new bootstrap.Modal(document.getElementById('modalEditarStock'));
                modal.show();
            }, 300);

            // limpar input cantidad
            document.getElementById('cantidadAnadirStock').value = 0;

        }).catch(error => {
            console.error('Error al obtener el producto:', error);
            alert('Error al obtener el producto');
        });

}

//function aumentarStock
function aumentarStock() {
    // 4. obter novo valor do input
    let cantidadAumentar = parseInt(document.getElementById('cantidadAnadirStock').value, 10);
    if (isNaN(cantidadAumentar) || cantidadAumentar <= 0) {
        alert('La cantidad a aumentar debe ser un número positivo');
        return;
    }
    // 5. enviar POST o backend
    fetch(`/productos/${productoIdActual}/AumStock`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ cantidad: cantidadAumentar })
    })
        .then(response => {
            if (response.ok) {
                // pechar modal con timeout polos bugs oscuros esos
                setTimeout(() => {
                    const modalElement = document.getElementById('modalEditarStock');
                    const modal = bootstrap.Modal.getInstance(modalElement);
                    modal.hide();
                }, 300);
                // Recargar tabla
                cargarTablaProductos();
                alert('Stock aumentado con éxito');
            } else {
                alert('Error al aumentar el stock');
            }
        }).catch(error => {
            console.error('Error al aumentar el stock:', error);
            alert('Error al aumentar el stock');
        });
}

//function reducirStock
function reducirStock() {
    let stockActual = parseInt(document.getElementById('stockActualProducto').value, 10);

    // 4. obter novo valor do input e validalo
    let cantidadReducir = parseInt(document.getElementById('cantidadAnadirStock').value, 10);
    if (isNaN(cantidadReducir) || cantidadReducir <= 0) {
        alert('La cantidad a reducir debe ser un número positivo');
        return;
    }
    if (cantidadReducir > stockActual) {
        alert('No puedes reducir más stock del disponible');
        return;
    }
    // 5. enviar POST o backend
    fetch(`/productos/${productoIdActual}/RedStock`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ cantidad: cantidadReducir })
    })
        .then(response => {
            if (response.ok) {
                // pechar modal con timeout polos bugs oscuros esos
                setTimeout(() => {
                    const modalElement = document.getElementById('modalEditarStock');
                    const modal = bootstrap.Modal.getInstance(modalElement);
                    modal.hide();
                }, 300);
                // Recargar tabla
                cargarTablaProductos();
                alert('Stock reducido con éxito');
            }
            else {
                alert('Error al reducir el stock');
            }
        }).catch(error => {
            console.error('Error al reducir el stock:', error);
            alert('Error al reducir el stock');
        });
}