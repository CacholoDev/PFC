document.addEventListener("DOMContentLoaded", function () {
    setTimeout(() => {
        
        document.getElementById('btnPedido').addEventListener('click', function() {
        // Abrir modal con Bootstrap
        const modal = new bootstrap.Modal(document.getElementById('modalCrearPedido'));
        modal.show();

        // Cargar productos disponibles
        cargarProductosDisponibles();






    });
    }, 500);

    // Cargar productos disponibles
    function cargarProductosDisponibles() {
        const listaProductosDiv = document.getElementById('listaProductos');
        listaProductosDiv.innerHTML = 'Cargando productos...';
        fetch('/productos')
            .then(response => response.json())
            .then(data => {
                listaProductosDiv.innerHTML = '';
                data.forEach(producto => {
                    const productoDiv = document.createElement('div');
                    productoDiv.classList.add('producto');
                    productoDiv.innerHTML = `
                        <h6 class="text-black"><b>${producto.nombre}</b></h6>
                        <p>Precio: ${producto.precio} €</p>
                        <p>Stock: ${producto.stock}</p>
                        <input type="number" min="1" max="${producto.stock}" value="1" class="form-control">
                        <button class="btn btn-warning mt-2 mb-2">Añadir al pedido</button>
                    `;
                    listaProductosDiv.appendChild(productoDiv);
                });
            })
            .catch(error => {
                console.error('Error al cargar productos:', error);
                listaProductosDiv.innerHTML = 'Error al cargar productos.';
            });
    }

});


/*
¿Cómo gestionar el array de selección y actualizar el resumen?
¿Cómo construir el objeto DTO correcto para el POST?
¿Cómo manejar errores (stock insuficiente, producto no disponible)?
*/

/*

📋 Paso 3: Lógica JavaScript (en crearPedidoCliente.js)

3.2. Gestionar selección de productos
Necesitas un array temporal en JavaScript para guardar los productos seleccionados: let productosSeleccionados = [];
Cuando el usuario haga click en "Añadir" de un producto:
Coge el producto y la cantidad seleccionada
Añádelo al array productosSeleccionados
Actualiza el resumen del pedido (lista + total)

3.3. Calcular el total en tiempo real
Cada vez que añadas un producto al array: 
let total = 0;
productosSeleccionados.forEach(item => {
    total += item.precio * item.cantidad;
});
// Mostrar el total en el modal

3.4. Enviar el pedido al backend
Cuando el usuario haga click en "Confirmar Pedido":
--Construye el objeto DTO que espera el backend (revisa PedidoCreateDto.java).
--Haz un POST a /pedidos con el DTO.
--Si es exitoso:
-Cierra el modal
-Recarga la tabla de pedidos
-Muestra un mensaje de éxito
🔍 Preguntas para ti antes de seguir:
-¿Has encontrado la estructura del modal en la doc de Bootstrap? (https://getbootstrap.com/docs/5.3/components/modal/)
-¿Sabes qué campos necesita el PedidoCreateDto en el backend? (clienteId, lista de líneas con productoId y cantidad)
-¿Dónde vas a poner el archivo crearPedidoCliente.js? (en la carpeta js/ y enlazarlo en mis-pedidos.html)
📝 Resumen del flujo:
-Usuario hace click en "Crear Pedido" → abre modal
-Modal carga lista de productos desde /productos
-Usuario selecciona productos y cantidades → se añaden a un array temporal
-El total se calcula automáticamente en cada cambio
-Usuario hace click en "Confirmar Pedido" → POST a /pedidos
-Backend crea el pedido → modal se cierra y tabla se recarga
*/