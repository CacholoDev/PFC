##### añadir o doc ######
-bootstrap
-usabilidat

### futuras melloras ###
añadir buttons creando a tabla accions para pedidos para q poidan borrar cambiar o estado e todo eso

## nextStage ###
-1. Funcionalidad de Crear Pedidos (Usuario)
En mis-pedidos.html, añadir un botón "Crear Pedido" que abra un modal o vaya a otra página.
El usuario debe poder:
Ver la lista de productos disponibles.
Seleccionar productos y cantidades.
Ver el total calculado en tiempo real.
Confirmar el pedido → envía POST a /pedidos con el DTO.

-2. CRUD completo en el Dashboard (Admin)
Ahora mismo solo muestras datos. Falta poder:
Clientes:
Crear: formulario para añadir nuevo cliente.
Editar: botón en cada fila que abra modal/formulario.
Eliminar: botón con confirmación.
Productos:
Crear: añadir nuevo producto (nombre, precio, stock, descripción).
Editar: modificar precio, stock, etc.
Eliminar: con confirmación.
Pedidos:
Ver detalles: botón que abra modal con las líneas del pedido (productos, cantidades).
Cambiar estado: dropdown o botones para pasar de PENDIENTE → EN_PREPARACION → LISTO → ENTREGADO.
Cancelar pedido: botón que cambie estado a CANCELADO.

-3. Validaciones y mejoras UX
Mensajes de confirmación al crear/editar/eliminar (modales o toasts).
Validación de stock al crear pedido (que no se puedan pedir más productos de los que hay).
Botón de refrescar tablas sin recargar la página.

Búsqueda/filtros en las tablas (por nombre, estado, fecha, etc.).
-4. Seguridad y refinamiento
Hashear contraseñas en el backend (ahora están en texto plano).
Añadir tokens JWT para autenticación más segura (opcional, más avanzado).
Mejorar manejo de errores (mostrar mensajes específicos del backend).

-5. Responsive y detalles finales
Probar en móvil y ajustar CSS si es necesario.
Añadir favicon y logo de la panadería.
README con instrucciones de cómo ejecutar el proyecto.
📋 Prioridad sugerida:
Crear pedidos (Usuario) → Es la funcionalidad más importante que falta.
Ver detalles de pedido (modal con líneas) → Para que admin y usuario vean qué productos hay en cada pedido.
CRUD de productos (Admin) → Para poder gestionar el catálogo.
Cambiar estado de pedidos (Admin) → Para marcar pedidos como listos/entregados.
CRUD de clientes (Admin) → Menos prioritario, pero completa el dashboard.


#### orden : ##
1. que o usuario cree pedidos
2. un modal en pedido para ver os detalles en plan que deixe ver que productos hay en cada pedido
3. que o admin poida xestionar o catalogo
4. cambiar estado de pedidos de pendiente a outro
5. que o admin poida crear usuariosç

