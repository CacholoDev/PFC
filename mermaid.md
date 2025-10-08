<pre class="mermaid">
---
title: Sistema de Panadería
---
flowchart TD
    Cliente[Cliente] -->|Consulta| Consultar_Catalogo[Consultar catálogo]
    Cliente -->|Hace| Realizar_Pedido[Realizar pedido]
    Panadero[Panadero] -->|Gestiona| Gestionar_Pedidos[Gestionar pedidos]
    Panadero -->|Actualiza| Actualizar_Productos[Actualizar productos]
</pre>

<!-- Preguntar si escollo, si ta mellor un que outro ou si poden ir 2 -->

<pre class="mermaid">
sequenceDiagram
    Cliente->>Sistema: Consultar catálogo
    Cliente->>Sistema: Realizar pedido
    Panadero->>Sistema: Gestionar pedidos
</pre>