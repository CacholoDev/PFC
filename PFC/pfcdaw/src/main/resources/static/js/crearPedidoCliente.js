document.addEventListener("DOMContentLoaded", function () {
    setTimeout(() => {
        
        document.getElementById('btnPedido').addEventListener('click', function() {
        // Abrir modal con Bootstrap
        const modal = new bootstrap.Modal(document.getElementById('modalCrearPedido'));
        modal.show();
    });
    }, 500);
});


/*
    // Cargar productos al abrir el modal
    cargarProductosDisponibles();
*/






/*
📋 Paso 2: Estructura del Modal

--Body del modal:
Lista de productos disponibles (se cargará con fetch desde /productos)
Por cada producto:
Nombre del producto
Precio
Stock disponible
Input para seleccionar cantidad (número)
Botón "Añadir al pedido"
Resumen del pedido:
Lista de productos seleccionados
Total calculado

--Footer del modal:
Botón "Cancelar" (cierra el modal)
Botón "Confirmar Pedido" (envía el POST)

📋 Paso 3: Lógica JavaScript (en crearPedidoCliente.js)
3.1. Cargar productos al abrir el modal
Cuando se abra el modal, haz un fetch a /productos para obtener todos los productos disponibles y renderízalos en una tabla o lista.

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