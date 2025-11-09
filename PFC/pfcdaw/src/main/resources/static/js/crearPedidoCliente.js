document.addEventListener("DOMContentLoaded", function () {
  let productosSeleccionados = [];
  let totalPedido = 0;
  setTimeout(() => {
    document.getElementById("btnPedido").addEventListener("click", function () {
      // Abrir modal con Bootstrap
      const modal = new bootstrap.Modal(
        document.getElementById("modalCrearPedido")
      );
      modal.show();

      // Cargar productos disponibles
      cargarProductosDisponibles();
      // button enviar pedido
      document.getElementById("btnConfirmarPedido").addEventListener("click", enviarPedido);
    });
  }, 500);

  // Cargar productos disponibles
  function cargarProductosDisponibles() {
    const listaProductosDiv = document.getElementById("listaProductos");
    listaProductosDiv.innerHTML = "Cargando productos...";
    fetch("/productos")
      .then((response) => response.json())
      .then((data) => {
        listaProductosDiv.innerHTML = "";
        data.forEach((producto) => {
          const productoDiv = document.createElement("div");
          productoDiv.classList.add("producto");
          productoDiv.innerHTML = `
                        <h6 class="text-black"><b>${producto.nombre}</b></h6>
                        <p>Precio: ${producto.precio} €</p>
                        <p>Stock: ${producto.stock}</p>
                        <input type="number" min="1" max="${producto.stock}" value="1" class="form-control">
                        <button class="btn btn-warning mt-2 mb-2">Añadir al carrito</button>
                    `;
          listaProductosDiv.appendChild(productoDiv);

          // Actualizar resumen
          actualizarResumen(producto, productoDiv);
        });
      })
      .catch((error) => {
        console.error("Error al cargar productos:", error);
        listaProductosDiv.innerHTML = "Error al cargar productos.";
      });
  }

  // carrito
  function actualizarResumen(producto, productoDiv) {
    // === CAPTURAR BUTTON + INPUT ===
    const inputCantidad = productoDiv.querySelector('input[type="number"]');
    const btnAnadir = productoDiv.querySelector("button");

    // Listener BUTTON "Añadir al pedido"
    btnAnadir.addEventListener("click", function () {
      // 1. Leer cantidade
      const cantidad = Number(inputCantidad.value);

      // 2. Validar cantidad
      if (cantidad < 1) {
        alert("La cantidad debe ser al menos 1");
        return;
      }
      if (cantidad > producto.stock) {
        alert(`No hay suficiente stock. Máximo: ${producto.stock}`);
        return;
      }

      // 3. Buscar si existe no array
      const lineaExistente = productosSeleccionados.find(
        (item) => item.id === producto.id
      );

      if (lineaExistente) {
        // Si existe, sumar
        lineaExistente.cantidad += cantidad;
      } else {
        // Si non existe, añadilo
        productosSeleccionados.push({
          id: producto.id,
          nombre: producto.nombre,
          precio: producto.precio,
          stock: producto.stock,
          cantidad: cantidad,
        });
      }

      // alert notificatoria
      alert(`${cantidad} x ${producto.nombre} añadido al pedido`);
      console.log("Pedido actual:", productosSeleccionados);
      // pintar Carro
      pintarCarrito();
    });
  }

  // Function pintar carrito
  function pintarCarrito() {
    const carritoDiv = document.getElementById("resumenPedido");
    if (productosSeleccionados.length === 0) {
      carritoDiv.innerHTML =
        '<p class="text-muted">No hay productos seleccionados</p>';
      return;
    }

    let html = '<ul class="list-group">';
    productosSeleccionados.forEach((p) => {
      const subtotal = p.precio * p.cantidad;
      html += `
            <li class="list-group-item-action list-group-item-warning d-flex justify-content-between align-items-center">
                <div>
                    <strong>${p.nombre}</strong><br>
                    <small>${p.cantidad} x ${p.precio.toFixed(2)}€</small>
                </div>
                <span class="badge bg-warning text-dark rounded-pill">${subtotal.toFixed(
                  2
                )}€</span>
            </li>
        `;
    });
    html += "</ul>";
    carritoDiv.innerHTML = html;

    // Calcular total
    calcularTotal();
  }

  // Function calcular total
  function calcularTotal() {
    const total = productosSeleccionados.reduce((acumulador, p) => {
      return acumulador + p.precio * p.cantidad;
    }, 0);

    document.getElementById("totalPedido").textContent =
      total.toFixed(2);
  }

  // post
  function enviarPedido() {

     // 1. Validar que haya productos
    if (productosSeleccionados.length === 0) {
        alert("No hay productos en el pedido");
        return;
    }

    // 1.5. Obter usuario do localstorage
    const usuario = JSON.parse(localStorage.getItem("usuario"));
    if (!usuario || !usuario.id) {
        alert("No hay usuario logueado");
        return;
    }

    // 2. Construir obxeto producto
    const productos = {};
    productosSeleccionados.forEach((p) => {
      productos[p.id] = p.cantidad;
    });

    // 3. DTO pedido
    const pedidoDTO = {
      usuarioId: usuario.id,
      productos: productos
    };
    console.log("Enviando pedido:", pedidoDTO);

    // 4. Enviar post
    fetch("/pedidos", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(pedidoDTO)})
      .then((response) => {
        if (!response.ok) {
            throw new Error("Error al crear el pedido");
        }
        return response.json();
      })
      .then((data) => {
        alert("Pedido creado con éxito");
        console.log("Pedido creado:", data);

        // reset estado
        productosSeleccionados = [];
        pintarCarrito();

        // pechar modal
        setTimeout(() => {
          const modalElement = document.getElementById("modalCrearPedido");
          const modal = bootstrap.Modal.getInstance(modalElement);
          modal.hide();
        }, 400);

        // recargar pedidos
        cargarPedidos();
      })
      .catch((error) => {
        console.error("Error:", error);
        alert("Error al crear el pedido: " + error.message);
      });   
  }
    
  // function eliminarProducto
  function eliminarProducto(productoId) {
    productosSeleccionados = productosSeleccionados.filter(
      (p) => p.id !== productoId
    );
    pintarCarrito();
  }
  
});
