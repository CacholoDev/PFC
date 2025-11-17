# Plataforma web de pedidos para panadería
### [RepoGitLab](https://gitlab.iessanclemente.net/dawd/a22adrianfh)

- [Introducción](#introducción)
- [Estado de arte o análisis del contexto](#estado-de-arte-o-análisis-del-contexto)
- [Propósito](#propósito)
- [Objetivos](#objetivos)
- [Alcance](#alcance)
  - [Funcionalidades incluidas:](#funcionalidades-incluidas)
  - [Límites: debido al tiempo que tengo para realizar el PFC](#límites-debido-al-tiempo-que-tengo-para-realizar-el-pfc)
  - [Contexto de uso:](#contexto-de-uso)
- [Conclusiones](#conclusiones)
- [Referencias, Fuentes consultadas y Recursos externos: Webgrafía](#referencias-fuentes-consultadas-y-recursos-externos-webgrafía)
      - [fin 1ª entrega(PFC)](#fin-1ª-entregapfc)
- [1.Análisis](#1análisis)
    - [-Diagrama de caso de uso](#-diagrama-de-caso-de-uso)
- [2. Diseño](#2-diseño)
  - [Arquitectura general](#arquitectura-general)
  - [Estructura básica del backend](#estructura-básica-del-backend)
  - [Diagrama de clases (Modelo de datos)](#diagrama-de-clases-modelo-de-datos)
  - [Diagrama de secuencia: Crear Pedido](#diagrama-de-secuencia-crear-pedido)
    - [Decisiones de diseño](#decisiones-de-diseño)
    - [Documentación de Endpoints API con Swagger/OpenAPI](#documentación-de-endpoints-api-con-swaggeropenapi)
- [3.Planificación](#3planificación)
  - [Fases del proyecto](#fases-del-proyecto)
  - [Diagrama de Gantt](#diagrama-de-gantt)
  - [Estimación de recursos y costes](#estimación-de-recursos-y-costes)
      - [fin 2º entrega(PFC)](#fin-2º-entregapfc)
- [4. Implementación Técnica del Backend](#4-implementación-técnica-del-backend)
  - [4.1. Tecnologías Utilizadas](#41-tecnologías-utilizadas)
  - [4.2. Decisiones de Arquitectura](#42-decisiones-de-arquitectura)
    - [**Uso de BigDecimal en vez de Double**](#uso-de-bigdecimal-en-vez-de-double)
    - [**LineaPedido como entidad separada**](#lineapedido-como-entidad-separada)
    - [**Lifecycle Hooks para recalcular totales**](#lifecycle-hooks-para-recalcular-totales)
    - [**Prevención de recursión infinita en JSON**](#prevención-de-recursión-infinita-en-json)
    - [**Gestión de stock transaccional**](#gestión-de-stock-transaccional)
  - [4.3. Validaciones Implementadas](#43-validaciones-implementadas)
    - [**Nivel DTO** (entrada de datos)](#nivel-dto-entrada-de-datos)
    - [**Nivel Entity** (persistencia)](#nivel-entity-persistencia)
    - [**Nivel Service** (lógica de negocio)](#nivel-service-lógica-de-negocio)
  - [4.4. Logs Implementados](#44-logs-implementados)
  - [4.5. Testing Realizado](#45-testing-realizado)
    - [**Tests manuales con Postman**](#tests-manuales-con-postman)
  - [4.6. Problemas Resueltos Durante Desarrollo](#46-problemas-resueltos-durante-desarrollo)
      - [fin 3ª entrega (Implementación Backend)](#fin-3ª-entrega-implementación-backend)
- [5. Implementación Técnica del Frontend](#5-implementación-técnica-del-frontend)
  - [5.1. Tecnologías y Bibliotecas](#51-tecnologías-y-bibliotecas)
  - [5.2. Estructura de Archivos](#52-estructura-de-archivos)
  - [5.3. Patrones de Arquitectura Frontend](#53-patrones-de-arquitectura-frontend)
    - [**Comunicación con API REST mediante Fetch**](#comunicación-con-api-rest-mediante-fetch)
    - [**Modales Reutilizables con Estado**](#modales-reutilizables-con-estado)
    - [**Renderizado Dinámico con Template Literals**](#renderizado-dinámico-con-template-literals)
  - [5.4. Funcionalidades Implementadas](#54-funcionalidades-implementadas)
    - [**Sistema de Tabs (dashboard.html)**](#sistema-de-tabs-dashboardhtml)
    - [**DataTables: Búsqueda, Ordenación, Paginación**](#datatables-búsqueda-ordenación-paginación)
    - [**Sistema de Badges de Color**](#sistema-de-badges-de-color)
    - [**Alertas de Stock Bajo**](#alertas-de-stock-bajo)
    - [**Gestión de Stock Separada**](#gestión-de-stock-separada)
    - [**Validaciones Frontend**](#validaciones-frontend)
  - [5.5. Decisiones Técnicas](#55-decisiones-técnicas)
    - [**Vanilla JavaScript vs Frameworks**](#vanilla-javascript-vs-frameworks)
    - [**Bootstrap como Framework CSS**](#bootstrap-como-framework-css)
    - [**DataTables para Tablas Interactivas**](#datatables-para-tablas-interactivas)
    - [**Separación de Concerns**](#separación-de-concerns)
      - [fin 4ª entrega (Implementación Frontend)](#fin-4ª-entrega-implementación-frontend)

## Introducción

El presente proyecto tiene como finalidad el diseño y desarrollo de una aplicación web orientada a la gestión de pedidos en una panadería. La motivación surge de la necesidad de digitalizar procesos tradicionales en pequeños comercios, permitiendo que clientes y negocio interactúen de una forma más eficiente y moderna.

El sistema constará de un **backend desarrollado con Spring Boot** y persistencia en **MySQL**, junto con un **frontend sencillo en HTML, CSS y JavaScript**. Se busca crear un **prototipo funcional** que facilite el registro de productos, la consulta de catálogo y la realización de pedidos, constituyendo una base sólida que podría evolucionar en el futuro hacia un sistema más completo.

## Estado de arte o análisis del contexto

En la actualidad, la digitalización en pequeños negocios de alimentación sigue siendo desigual. Mientras que grandes cadenas cuentan con aplicaciones móviles o webs personalizadas, muchas panaderías y negocios locales continúan gestionando pedidos únicamente de manera presencial o telefónica.

La aplicación se orienta principalmente a:

- **Clientes** habituales que buscan comodidad y rapidez al realizar sus pedidos.
- **Propietarios de panaderías** que necesitan un método simple para organizar encargos sin recurrir a herramientas complejas o costosas.

Existen soluciones en el mercado como aplicaciones de delivery (Glovo, Uber Eats), pero resultan demasiado generales o implican costes elevados para pequeños comercios. Nuestro enfoque propone una solución ligera, adaptable y pensada **específicamente para un negocio pequeño**, sin dependencias externas.

El desarrollo abre una oportunidad de modernización para estos negocios, con un prototipo que podría evolucionar hacia una solución más robusta e incluso comercializable.

## Propósito

El propósito de este proyecto es **crear una aplicación web que facilite la gestión de pedidos en una panadería**, permitiendo a los clientes visualizar un catálogo online y realizar pedidos, y al admin gestionar dichos pedidos de forma sencilla.

El objetivo principal es la **digitalización de la panader´ia**, con una solución ligera, práctica y de fácil implementación.

## Objetivos

- Desarrollar un **backend en Spring Boot** con una API REST que gestione productos,pedidos e clientes.
- Implementar la **persistencia de datos en MySQL**.
- Diseñar un **frontend sencillo con HTML, CSS y JavaScript**, que permita al cliente navegar por el catálogo y realizar pedidos.
- Crear una interfaz básica para la **gestión de pedidos por parte del negocio**.
- Documentar el proyecto en GitLab/GitHub con instrucciones claras de instalación y uso.
- Desplegar un prototipo funcional que pueda ser probado en entorno local.

## Alcance

### Funcionalidades incluidas:

- Visualización del catálogo de productos.
- Carrito básico
- Realización de pedidos.
- Gestión interna de pedidos recibidos.
- En principio la haré sin roles pero si me da tiempo a llegar al PFC haré admin / user, si no será futura implementación.
- Persistencia en base de datos MySQL.

### Límites: debido al tiempo que tengo para realizar el PFC

- No incluirá pasarela de pago en esta primera versión.
- La autenticación será básica.
- El carrito sera básico.
- El frontend será simple (HTML/CSS/JS).
- Se desarrollará como un **prototipo funcional** para entorno local, con futura implementacion de por ejemplo un docker.

### Contexto de uso:

- Proyecto académico de fin de ciclo (DAW).
- Aplicación de ejemplo para un negocio local.
- Base para **futuras ampliaciones** (ej.: mejora del despliegue(implementacion de un Docker), integración de notificaciones,mejora del carrito,mejora del FrontEnd migrandolo a React,mejora en vez de texto plano por password usar el spring security con encryptado(BCryptPasswordEncoder) hasheando las password al guardar y comparandolascon .matches al logearse, añadir distintas funcionalidades que pueda pedir el negocio.
- Se desarrollará un prototipo funcional con datos de prueba, no una versión en producción.).  
    

## Conclusiones

Este anteproyecto propone una solución concreta a la falta de digitalización en pequeños comercios, ofreciendo una aplicación sencilla pero funcional que puede servir como prototipo y base para futuras mejoras.

A través de este desarrollo se pondrán en práctica conocimientos de **backend con Java Spring Boot, bases de datos MySQL, y desarrollo frontend básico**, además de aplicar buenas prácticas en documentación y control de versiones con Git.

El proyecto permitirá afianzar competencias clave en desarrollo web y servirá como muestra de aplicación práctica de lo aprendido en el ciclo formativo.

## Referencias, Fuentes consultadas y Recursos externos: Webgrafía

- [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [MySQL Documentation](https://dev.mysql.com/doc/)
- [MDN Web Docs – HTML, CSS y JavaScript](https://developer.mozilla.org/)
- [GitLab Documentation](https://docs.gitlab.com/)
- [StackOverflow](https://stackoverflow.com/)

##### fin 1ª entrega(PFC)

# TODO
## 1.Análisis

Para el desarrollo de este proyecto se ha optado por una **metodología Kanban**, ya que permite organizar las tareas de forma visual y flexible. Dado que se trata de un proyecto individual y con tiempo limitado, además de tener que estar haciendo a la par la FCT en Santiago 8h(09:00-17:00) siendo de Noia y me consume mucho tiempo para hacer un buen PFC.

El enfoque consiste en dividir el trabajo en pequeñas tareas o fases visibles en un tablero (por ejemplo, “Do”, “Doing”, “Done”), lo que ayuda a mantener un control del avance del proyecto de forma sencilla.

Se usa un tablero Trello donde se registran las tareas principales del proyecto:

* Configuración del entorno de desarrollo (Spring Boot, MySQL, VSCode).
* Creación de la base de datos y conexión desde el backend.
* Implementación de la API REST.
* Desarrollo del frontend con HTML, CSS y JavaScript.
* Pruebas locales y documentación.

Enlace a Trello para ver el Kanban: [KanbanTrello](https://trello.com/b/DpZTdW2t/client-workflow-management) - visibilidad publica.

#### -Diagrama de caso de uso

El siguiente diagrama muestra de forma general las **interacciones principales** en la aplicación web de pedidos para panadería.

Se refleja las principales funciones del sistema sin entrar aún en detalle de roles avanzados que haré si me da tiempo antes del fin de fecha del PFC, si no lo continuaré después de ello, ya que en esta primera versión no se implementan usuarios diferenciados (admin/user), sino que se centra en el flujo básico de pedidos y del funcionamiento de la API REST con Spring Boot Java.

**Diagrama de secuencia simplificado**: Muestra las tres operaciones principales que realizan los actores en el sistema (consultar catálogo, realizar pedido, gestionar pedidos).

```mermaid
---
title: Sistema de Panadería
---
sequenceDiagram
    Cliente->>Sistema: Consultar catálogo
    Cliente->>Sistema: Realizar pedido
    Panadero->>Sistema: Gestionar pedidos
```


## 2. Diseño

El proyecto está dividido en dos partes principales:

**Backend**: Desarrollado con Spring Boot, ofrece una API REST para gestionar productos,pedidos y clientes, almacenando los datos en MySQL mediante JPA, se podrán ver logs en consola mediante el uso del Logger de SpringBoot.

**Frontend**: Página web sencilla hecha con HTML, CSS y JavaScript, que permite listar productos y realizar pedidos.

### Arquitectura general
[Cliente (HTML/JS)] --> [Spring Boot API REST] --> [Base de datos MySQL]


### Estructura básica del backend

```
com.pfcdaw.pfcdaw
 ├─ model/
 │   ├─ ClienteEntity.java
 │   ├─ ProductoEntity.java
 │   ├─ PedidoEntity.java
 │   ├─ LineaPedido.java
 │   └─ EstadoPedidoEnum.java
 ├─ dto/
 │   ├─ PedidoCreateDto.java
 │   └─ StockUpdateDto.java
 ├─ repository/
 │   ├─ ClienteRepository.java
 │   ├─ ProductoRepository.java
 │   └─ PedidoRepository.java
 ├─ service/
 │   ├─ ProductoService.java
 │   └─ PedidoService.java
 ├─ controller/
 │   ├─ ClienteController.java
 │   ├─ ProductoController.java
 │   └─ PedidoController.java
 ├─ config/
 │   └─ WebConfig.java (CORS)
 └─ PfcdawApplication.java (Main)
```

### Diagrama de clases (Modelo de datos)

Este diagrama muestra las **entidades principales** del sistema y sus **relaciones**. Cada cliente puede tener múltiples pedidos, cada pedido contiene varias líneas (LineaPedido), y cada línea referencia un producto específico con su cantidad y subtotal.

```mermaid
classDiagram
    class ClienteEntity {
      +Long id
      +String nombre
      +String apellido
      +String email (unique)
      +String direccion
      +String telefono
      +List~PedidoEntity~ pedidos
    }
    
    class ProductoEntity {
      +Long id
      +String nombre
      +String descripcion
      +BigDecimal precio
      +Integer stock
      +List~LineaPedido~ lineasPedido
      +aumentarStock(cantidad)
      +reducirStock(cantidad)
    }
    
    class PedidoEntity {
      +Long id
      +LocalDateTime fechaPedido
      +BigDecimal total
      +EstadoPedidoEnum estado
      +ClienteEntity cliente
      +List~LineaPedido~ lineasPedido
      +recalcularTotal()
    }
    
    class LineaPedido {
      +Long id
      +PedidoEntity pedido
      +ProductoEntity producto
      +Integer cantidad
      +BigDecimal pTotal
    }
    
    class EstadoPedidoEnum {
      <<enumeration>>
      PENDIENTE
      EN_PREPARACION
      COMPLETADO
      ENTREGADO
      CANCELADO
    }
    
    ClienteEntity "1" --> "*" PedidoEntity : tiene
    PedidoEntity "1" --> "*" LineaPedido : contiene
    ProductoEntity "1" --> "*" LineaPedido : aparece en
    PedidoEntity --> EstadoPedidoEnum : estado
```

### Diagrama de secuencia: Crear Pedido

Este diagrama ilustra el **flujo completo** de creación de un pedido, desde que el cliente envía la petición HTTP hasta que se persiste en la base de datos. Muestra las **validaciones**, el **cálculo de totales** con BigDecimal, la **creación de líneas de pedido** y la **reducción automática de stock** de forma transaccional.

```mermaid
sequenceDiagram
    actor Cliente
    participant Controller as PedidoController
    participant Service as PedidoService
    participant ProdService as ProductoService
    participant Repository as PedidoRepository
    participant DB as MySQL
    
    Cliente->>Controller: POST /pedidos<br/>{clienteId, productos}
    Controller->>Service: createPedido(dto)
    
    Service->>DB: Validar cliente existe
    Service->>DB: Validar productos existen
    Service->>Service: Validar cantidades > 0
    Service->>Service: Calcular subtotales (BigDecimal)
    Service->>Service: Crear PedidoEntity + LineaPedido
    
    Service->>Repository: save(pedido)
    Repository->>DB: INSERT pedidos, lineas_pedido
    DB-->>Repository: OK (cascade)
    
    loop Por cada línea
        Service->>ProdService: reducirStock(productoId, cantidad)
        ProdService->>DB: UPDATE productos SET stock = stock - cantidad
        DB-->>ProdService: OK
    end
    
    Service-->>Controller: PedidoEntity guardado
    Controller-->>Cliente: 201 Created<br/>{pedido con líneas}
```
#### Decisiones de diseño
-Uso de de Logger para ver la info de lo que está pasando en la app por consola

- No se implementan roles ni autenticación en esta versión (MVP)

- Los datos de conexión a la base de datos se guardan en un archivo .env (Seguridad adicional)(en este caso los subiremos al github, no pondremos gitignore para mostrar el 100% en el PFC y cuando lo termine, poner el .gitignore con el .env cambiando los datos del user/pass).

- El frontend se comunica con el backend mediante fetch() con peticiones REST

- Es un prototipo funcional para ejecución local

#### Documentación de Endpoints API con Swagger/OpenAPI

Se ha integrado **Swagger UI** para documentación interactiva de la API REST. Permite visualizar todos los endpoints, sus parámetros, y probar peticiones directamente desde el navegador.

**Acceso a Swagger UI**: `http://localhost:8080/swagger-ui.html`

**Probar Swagger**: Usar el boton al lado de cada metodo de "Try It OUT"

**Ventajas**:
- Documentación automática generada desde los controllers
- Interfaz visual para probar endpoints sin Postman
- Exportación de especificación OpenAPI 3.0 (JSON/YAML)
- Actualización automática al modificar código

**Recursos principales documentados**:
- **Clientes**: GET/POST/DELETE (`/clientes`)
- **Productos**: CRUD completo + gestión de stock (`/productos`, `/productos/{id}/AumStock`, `/productos/{id}/RedStock`)
- **Pedidos**: GET/POST/PUT para gestión de estados (`/pedidos`, `/pedidos/cliente/{id}`, `/pedidos/{id}/estado`)

**Nota**: El stock se gestiona mediante endpoints dedicados o automáticamente al crear pedidos.


## 3.Planificación

Para la planificación del desarrollo se empleará una **metodología Kanban**, ya que permite una organización visual y flexible del trabajo sin una estructura rígida para gestionar las tareas del proyecto (por ejemplo: desarrollo backend, frontend, pruebas y documentación)..

### Fases del proyecto

1. **Configuración del entorno y base de datos** – 1/2 semana
2. **Desarrollo del backend (API REST y persistencia)** – 4 semanas
3. **Desarrollo del frontend (HTML, CSS, JS)** – 4 semanas
4. **Integración y pruebas locales** – 1/2 semana
5. **Documentación y entrega final** – 1 semana

### Diagrama de Gantt

```mermaid
gantt
    title Desarrollo Plataforma Panadería
    dateFormat  YYYY-MM-DD
    axisFormat %d/%m
    section Fase 1
    Configuración entorno :2024-10-01, 4d
    section Fase 2
    Backend (API REST) :crit, 2024-10-05, 34d
    section Fase 3
    Frontend :crit, 2024-11-5, 28d
    section Fase 4
    Integración :2024-12-03, 3d
    section Fase 5
    Documentación :2024-12-06, 3d
```

### Estimación de recursos y costes

* **Duración:** 9 semanas (~75-85 horas)
* **Coste simulado:** 20 €/h * 80h → 1600€
* **Retorno estimado(fictio):** 3000€
* **Recursos:** ordenador personal, VSCode, MySQL, Spring Boot, conexión a internet


##### fin 2º entrega(PFC)

---

## 4. Implementación Técnica del Backend

### 4.1. Tecnologías Utilizadas

| Tecnología | Versión | Uso |
|------------|---------|-----|
| Java | 21 | Lenguaje base |
| Spring Boot | 3.5.7 | Framework backend |
| Spring Data JPA | 3.5.7 | Persistencia ORM |
| MySQL | 8.x | Base de datos |
| Lombok | Latest | Reducción boilerplate |
| Jakarta Validation | Latest | Validaciones |
| SLF4J | Latest | Logging |
| SpringDoc OpenAPI | Documentación API (Swagger UI) |

### 4.2. Decisiones de Arquitectura

#### **Uso de BigDecimal en vez de Double**
**Problema detectado**: Al usar `Double` para precios, operaciones como `1.80 * 3` daban `3.5999999999...` por la representación binaria.

**Solución implementada**: Migración a `BigDecimal` en todos los campos monetarios:
- `ProductoEntity.precio`: `BigDecimal`
- `LineaPedido.pTotal`: `BigDecimal`
- `PedidoEntity.total`: `BigDecimal`
- Base de datos: `DECIMAL(19,2)`

**Resultado**: Precisión exacta en cálculos monetarios.

#### **LineaPedido como entidad separada**
En vez de guardar solo IDs de productos en un pedido, se creó una entidad `LineaPedido` que actúa como tabla intermedia entre `PedidoEntity` y `ProductoEntity`.

**Ventajas**:
- Permite cantidad variable por producto
- Guarda precio histórico (si el precio cambia después, el pedido mantiene el precio original)
- Permite calcular subtotales por línea

#### **Lifecycle Hooks para recalcular totales**
Se implementaron los métodos `@PrePersist` y `@PreUpdate` en `PedidoEntity` para recalcular automáticamente el total sumando las líneas:

```java
@PrePersist
@PreUpdate
public void recalcularTotal() {
    if (lineasPedido != null && !lineasPedido.isEmpty()) {
        this.total = lineasPedido.stream()
            .map(LineaPedido::getPTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    } else {
        this.total = BigDecimal.ZERO;
    }
}
```

**Ventaja**: Aunque se eliminen productos (cascade) o se modifiquen líneas, el total siempre está sincronizado.

#### **Prevención de recursión infinita en JSON**
**Problema**: Al serializar `PedidoEntity` con Jackson, se generaba:
```
Pedido → lineasPedido → LineaPedido → pedido → lineasPedido → ... (∞)
```

**Solución**: Añadir `@JsonIgnore` en la referencia inversa:
```java
@ManyToOne
@JsonIgnore  // Corta la recursión
private PedidoEntity pedido;
```

#### **Gestión de stock transaccional**
El servicio `PedidoService` está anotado con `@Transactional`, lo que garantiza:
- Si falla la reducción de stock de algún producto → rollback completo
- Si falla guardar el pedido → no se reduce stock
- Atomicidad: o se completa todo o nada

### 4.3. Validaciones Implementadas

#### **Nivel DTO** (entrada de datos)
```java
@NotNull
@Size(min = 1, message = "El pedido debe tener al menos un producto")
private Map<Long, Integer> productos;
```

#### **Nivel Entity** (persistencia)
```java
@NotBlank(message = "El nombre del producto es obligatorio")
private String nombre;

@DecimalMin(value = "0.0", message = "El precio debe ser positivo")
private BigDecimal precio;
```

#### **Nivel Service** (lógica de negocio)
- Validación de cliente existente
- Validación de productos existentes
- Validación de stock suficiente
- Validación de cantidades > 0

### 4.4. Logs Implementados

Todos los controllers y services tienen logging estructurado:

**Ejemplo en ProductoController**:
```java
log.info("[POST /productos/{}] Creando nuevo producto: {}", producto.getNombre());
log.debug("[PUT /productos/{}] Antes: nombre={}, precio={}", id, producto.getNombre(), producto.getPrecio());
log.warn("[DELETE /productos/{}] Producto no encontrado", id);
```

**Nivel configurado**: `DEBUG` en desarrollo, permite ver:
- Peticiones HTTP entrantes
- Queries SQL ejecutadas
- Operaciones de negocio (creación pedido, reducción stock)

### 4.5. Testing Realizado

#### **Tests manuales con Postman**

| Test | Resultado |
|------|-----------|
| Crear cliente | ✅ OK |
| Crear producto | ✅ OK |
| Crear pedido con 2 productos | ✅ OK (stock reduce correctamente) |
| Crear pedido con stock insuficiente | ✅ OK (rechaza con error) |
| Aumentar stock manualmente | ✅ OK |
| Eliminar producto usado en pedidos | ✅ OK (cascade elimina líneas, recalcula total) |
| Obtener pedidos de un cliente | ✅ OK |
| JSON sin recursión infinita | ✅ OK |
| Decimales exactos en totales | ✅ OK (BigDecimal funciona) |

### 4.6. Problemas Resueltos Durante Desarrollo

1. **Recursión infinita en JSON** → Solucionado con `@JsonIgnore`
2. **Decimales imprecisos** → Migrado de `Double` a `BigDecimal`
3. **Totales desincronizados** → Añadidos lifecycle hooks

##### fin 3ª entrega (Implementación Backend)

---

## 5. Implementación Técnica del Frontend

### 5.1. Tecnologías y Bibliotecas

| Tecnología | Versión | Uso |
|------------|---------|-----|
| HTML5 | - | Estructura semántica |
| CSS3 + Bootstrap | 5.3.8 | Estilos y componentes UI |
| JavaScript (Vanilla) | ES6+ | Lógica cliente, fetch API |
| jQuery | 3.7.0 | Requerido por DataTables |
| DataTables | 2.3.5 | Tablas interactivas |
| DataTables Buttons | 3.2.5 | Exportación datos |
| Bootstrap Icons | 1.11.3 | Iconografía |
| JSZip | 3.10.1 | Exportación Excel |
| pdfmake | 0.2.7 | Exportación PDF |

### 5.2. Estructura de Archivos

```
resources/static/
├── login.html              # Autenticación básica
├── dashboard.html          # Panel admin (tabs)
├── mis-pedidos.html        # Vista cliente
├── css/
│   ├── stylesLogin.css
│   ├── dashboard.css
│   └── mis-pedidos.css
├── js/
│   ├── login.js            # Lógica autenticación
│   ├── clientes.js         # CRUD clientes
│   ├── productos.js        # CRUD productos + stock
│   ├── pedidos.js          # Gestión pedidos admin
│   ├── pedidosClientes.js  # Vista pedidos usuario
│   └── crearPedidoCliente.js  # Carrito y creación pedido
└── img/
```

### 5.3. Patrones de Arquitectura Frontend

#### **Comunicación con API REST mediante Fetch**
Todas las operaciones con el backend usan `fetch()` con manejo de promesas:

```javascript
fetch('/productos')
    .then(response => response.json())
    .then(data => cargarTablaProductos(data))
    .catch(error => console.error('Error:', error));
```

#### **Modales Reutilizables con Estado**
Se usan variables globales (`modoEdicion`, `idActual`) para reutilizar modales en crear/editar:

```javascript
let modoEdicion = false;
let productoIdActual = null;

function modalCrearProducto() {
    modoEdicion = false;
    document.getElementById('formProducto').reset();
    // Abrir modal...
}

function modalEditarProducto(id) {
    modoEdicion = true;
    productoIdActual = id;
    // Cargar datos y abrir modal...
}
```

**Ventaja**: Reduce duplicación de HTML, un solo modal maneja crear y editar.

#### **Renderizado Dinámico con Template Literals**
Las tablas se construyen dinámicamente usando template strings de ES6:

```javascript
data.forEach(producto => {
    tablaHTML += `
        <tr>
            <td>${producto.id}</td>
            <td>${producto.nombre}</td>
            <td${producto.stock < 10 ? ' class="bg-danger text-dark fw-bold"' : ''}>
                ${producto.stock < 10 ? '⚠️ ' : ''}${producto.stock}
            </td>
        </tr>
    `;
});
```

**Técnica destacada**: Operador ternario dentro de `${}` para renderizado condicional de clases CSS y contenido.

### 5.4. Funcionalidades Implementadas

#### **Sistema de Tabs (dashboard.html)**
Panel admin con navegación por pestañas sin recarga de página:
- **Tab Clientes**: Crear/eliminar clientes, badges de rol (ADMIN/USER)
- **Tab Productos**: CRUD completo + gestión stock independiente
- **Tab Pedidos**: Ver todos los pedidos, cambiar estado, ver detalles

#### **DataTables: Búsqueda, Ordenación, Paginación**
Integración de DataTables en todas las tablas con configuración española:

```javascript
$('#tablaProductos table').DataTable({
    language: {
        url: '//cdn.datatables.net/plug-ins/2.3.5/i18n/es-ES.json'
    },
    dom: 'Bfrtip',
    buttons: [
        { extend: 'copy', text: 'Copiar' },
        { extend: 'csv', text: 'CSV' },
        { extend: 'excel', text: 'Excel', title: 'Lista de Productos' },
        { extend: 'pdf', text: 'PDF', orientation: 'landscape' },
        { extend: 'print', text: 'Imprimir' }
    ]
});
```

**Funciones incluidas**:
- Búsqueda en tiempo real
- Ordenación por columnas
- Paginación configurable
- Exportación a CSV, Excel, PDF, Copiar, Imprimir

#### **Sistema de Badges de Color**
Indicadores visuales para estados y roles usando clases Bootstrap:

**Estados de pedido** (pedidos.js):
```javascript
function getBadgeClass(estado) {
    switch(estado) {
        case 'PENDIENTE': return 'bg-warning';
        case 'EN_PREPARACION': return 'bg-info';
        case 'LISTO': return 'bg-primary';
        case 'ENTREGADO': return 'bg-success';
        case 'CANCELADO': return 'bg-danger';
    }
}
```

**Roles de usuario** (clientes.js):
```javascript
function getBadgeRoleClass(role) {
    return role === 'ADMIN' ? 'bg-warning' : 'bg-info';
}
```

#### **Alertas de Stock Bajo**
Renderizado condicional con operador ternario cuando stock < 10:

```javascript
<td${producto.stock < 10 ? ' class="bg-danger text-dark fw-bold"' : ''}>
    ${producto.stock < 10 ? '⚠️ ' : ''}${producto.stock}
</td>
```

**Resultado visual**: Celda roja con emoji de advertencia para productos con stock crítico.

#### **Gestión de Stock Separada**
Modal independiente para aumentar/reducir stock con validaciones frontend:

```javascript
function reducirStock() {
    let stockActual = parseInt(document.getElementById('stockActualProducto').value, 10);
    let cantidadReducir = parseInt(document.getElementById('cantidadAnadirStock').value, 10);
    
    if (cantidadReducir > stockActual) {
        alert('No puedes reducir más stock del disponible');
        return;
    }
    
    fetch(`/productos/${productoIdActual}/RedStock`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ cantidad: cantidadReducir })
    })...
}
```

**Ventaja**: Evita modificar stock accidentalmente al editar otros datos del producto.

#### **Validaciones Frontend**
Antes de enviar datos al backend, se validan:
- Campos obligatorios no vacíos
- Cantidades positivas
- Formato email con regex
- Stock suficiente antes de reducir

### 5.5. Decisiones Técnicas

#### **Vanilla JavaScript vs Frameworks**
Se optó por JavaScript puro sin React porque:
- Proyecto de alcance limitado debido al tiempo disponible
**Consideración futura**: Migración a React

#### **Bootstrap como Framework CSS**
Elección de Bootstrap 5 por:
- Componentes prediseñados (modals, badges, alerts, forms)
- Responsive design automático
- Grid system para layouts
- Documentación extensa
- Compatibilidad con DataTables

#### **DataTables para Tablas Interactivas**
Integración de DataTables en vez de implementación manual porque:
- Ahorra +-30 minutos de desarrollo por tabla
- Funcionalidad robusta y probada (usado por Netflix, Google)
- Exportación incluida en extensiones oficiales

#### **Separación de Concerns**
Cada vista tiene su propio archivo JS:
- `clientes.js` → Gestión de clientes
- `productos.js` → Gestión de productos
- `pedidos.js` → Vista admin de pedidos
- `pedidosClientes.js` → Vista usuario de pedidos
- `crearPedidoCliente.js` → Lógica del carrito

**Ventaja**: Mantenibilidad, evita conflictos de nombres, carga bajo demanda.

##### fin 4ª entrega (Implementación Frontend)