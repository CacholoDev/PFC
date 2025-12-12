# Guion Presentación PFC - Plataforma Web Panadería

---

## Diapositiva 1: Portada
**Título:** Plataforma web de pedidos para panadería  
**Subtítulo:** Digitalización de pequeños negocios  
**Autor:** Adrián Fábregas  
**Centro:** IES San Clemente  
**Fecha:** Diciembre 2025

**Speaker Notes:**
"Buenas. Hoy presento mi proyecto final de DAW: una plataforma web para gestionar pedidos en panaderías. El objetivo es digitalizar pequeños negocios que aún gestionan pedidos de forma manual."

**Visual:** Imagen de panadería moderna o interfaz de la app.

---

## Diapositiva 2: Objetivo y Problema
**Puntos clave:**
- Problema: panaderías gestionan pedidos presencial/teléfono
- Solución: plataforma web para catálogo + pedidos online
- Beneficio: modernización sin grandes costes ni dependencias externas

**Speaker Notes:**
"Muchas panaderías no tienen una solución digital porque las apps de delivery son caras o muy genéricas. Este proyecto ofrece una alternativa ligera y adaptada a pequeños negocios."

**Visual:** Icono de "problema vs solución" o comparativa antes/después.

---

## Diapositiva 3: Contexto y Alcance
**Puntos clave:**
- **Usuarios:** Clientes (ver catálogo, hacer pedidos) y Admin (gestionar stock/pedidos)
- **Funcionalidades:** Catálogo, carrito, pedidos, cambio de estado, CRUD productos
- **Límites (por tiempo):** Sin pasarela de pago, autenticación básica, frontend simple

**Speaker Notes:**
"El proyecto cubre lo esencial: clientes pueden hacer pedidos online y el admin gestiona stock y estados. Por tiempo de FCT, dejé pasarela de pago y seguridad avanzada como mejoras futuras."

**Visual:** Dos columnas: Cliente (catálogo, carrito, pedidos) / Admin (productos, stock, estados).

---

## Diapositiva 4: Arquitectura General
**Diagrama:**
```
Cliente → Frontend (HTML/JS/Bootstrap) → Backend (Spring Boot API REST) → MySQL
                    ↓
            Login en localStorage
```

**Speaker Notes:**
"La arquitectura es sencilla pero funcional: frontend estático servido por Nginx, backend Spring Boot sin sesiones (stateless), y MySQL con persistencia en volumen Docker. El login se guarda en el navegador por ahora."

**Visual:** Diagrama de bloques con flechas claras (usar Mermaid o replicar en Canva).

---

## Diapositiva 5: Tecnologías y Artefactos
**Backend:**
- Java 21, Spring Boot 3.5, JPA, Validation
- Swagger/OpenAPI para documentación API

**Frontend:**
- HTML5, CSS3, Bootstrap 5
- JavaScript Vanilla, DataTables

**Infraestructura:**
- Docker Compose (Nginx, backend, MySQL)
- Volumen persistente para BD

**Speaker Notes:**
"Usé Spring Boot para el backend con validaciones y logs; Bootstrap para el frontend; y Docker para despliegue rápido. Todo está documentado en README y doc.md."

**Visual:** Logos de tecnologías (Java, Spring, Bootstrap, Docker, MySQL).

---

## Diapositiva 6: Modelo de Datos
**Entidades principales:**
- Cliente (nombre, email, rol)
- Producto (nombre, precio, stock)
- Pedido (fecha, total, estado, cliente)
- LineaPedido (cantidad, subtotal, producto)

**Decisiones técnicas:**
- BigDecimal para precios (precisión exacta)
- @JsonIgnore para evitar recursión infinita
- Hooks @PrePersist para calcular totales

**Speaker Notes:**
"El modelo está normalizado: cada pedido tiene líneas que referencian productos. Usé BigDecimal para evitar errores de redondeo en precios y hooks para recalcular totales automáticamente."

**Visual:** Diagrama de clases simplificado (Cliente → Pedido → Línea → Producto).

---

## Diapositiva 7: Flujo de Usuario (Cliente)
**Pasos:**
1. Registrarse / Login (localStorage)
2. Consultar catálogo de productos
3. Añadir al carrito y confirmar pedido
4. Ver historial y detalles de pedidos

**Speaker Notes:**
"El cliente se registra, hace login, ve el catálogo, añade productos al carrito y confirma el pedido. Luego puede revisar su historial con detalles de cada pedido."

**Visual:** Capturas: pantalla login → catálogo → carrito → "pedido creado".

**Capturas recomendadas:**
- `login.html` con formulario
- Catálogo con productos y botón "Añadir al carrito"
- Modal de carrito con productos seleccionados
- Pantalla "Pedido confirmado" o tabla en `mis-pedidos.html`

---

## Diapositiva 8: Flujo de Administración
**Pasos:**
1. Login como admin (rol ADMIN)
2. Dashboard con tabs: Clientes, Productos, Pedidos
3. CRUD productos (nombre, precio, stock, imagen)
4. Gestionar pedidos: ver detalles, cambiar estado
5. Exportar datos (CSV, Excel, PDF)

**Speaker Notes:**
"El admin accede al dashboard y puede gestionar clientes, productos y pedidos. Puede actualizar stock, cambiar estados de pedidos y exportar datos a distintos formatos."

**Visual:** Capturas del dashboard con tabs y tabla de productos/pedidos.

**Capturas recomendadas:**
- Dashboard con tabs visibles
- Tabla de productos con botones de edición/stock
- Modal de detalles de pedido con cambio de estado
- Botones de exportación (CSV, Excel, PDF)

---

## Diapositiva 9: Codificación y Pruebas
**Pruebas implementadas:**
- Swagger UI para probar endpoints (Try it out)
- Flujos E2E manuales: registro → pedido → cambio estado
- Validaciones frontend (campos obligatorios, email)
- Validaciones backend (DTOs, lógica de negocio)
- Logs estructurados (SLF4J)

**Speaker Notes:**
"Para probar la app usé Swagger para testear la API, y flujos completos desde el navegador. Hay validaciones en frontend y backend, y logs para depurar."

**Visual:** Captura de Swagger UI con endpoints visibles.

**Captura recomendada:**
- Swagger UI en `http://localhost:8081/swagger-ui/index.html` con endpoints expandidos

---

## Diapositiva 10: Seguridad y Estado
**Estado actual:**
- Backend **stateless** (sin sesiones en servidor)
- Login solo en cliente (localStorage)
- Endpoints REST públicos (sin autenticación backend)

**Riesgos conocidos:**
- Contraseñas en texto plano
- Vulnerable a XSS (localStorage accesible)
- Sin expiración de sesión

**Roadmap de seguridad:**
- Spring Security + sesiones/cookies httpOnly o JWT
- BCrypt para contraseñas
- CSRF protection, roles por endpoint (@PreAuthorize)

**Speaker Notes:**
"Por tiempo, la seguridad es básica: el login se guarda en el navegador y el backend no valida nada. Es un riesgo conocido y documentado. El plan es migrar a Spring Security con sesiones reales o JWT."

**Visual:** Icono de candado abierto → candado cerrado; tabla de mejoras de seguridad.

---

## Diapositiva 11: Despliegue
**Opción recomendada: Docker**
```bash
cd PFC/pfcdaw
docker compose up
```
- Levanta Nginx, backend y MySQL automáticamente
- Frontend: http://localhost:8081
- Swagger: http://localhost:8081/swagger-ui/index.html
- Persistencia en volumen `mysql_panaderia`

**Alternativa local:**
- XAMPP + MySQL manual
- `./mvnw spring-boot:run` para backend
- Frontend con LiveServer

**Speaker Notes:**
"El despliegue es muy simple con Docker: un solo comando levanta todo. Para desarrollo local también funciona con XAMPP y Maven."

**Visual:** Terminal con comando `docker compose up` o logo Docker.

---

## Diapositiva 12: Planificación y Coste
**Estimación:**
- Duración: 9 semanas efectivas (~75-85h)
- Metodología: Kanban (Trello)
- Coste simulado: 1600€ (20€/h)
- Retorno estimado: 3000€

**Comparativa real vs estimado:**
- Estimado: 9 semanas
- Real: ~10 semanas (solape con FCT en Santiago, +1 semana documentación)

**Speaker Notes:**
"Planifiqué 9 semanas usando Kanban. En realidad tardé 10 porque la FCT en Santiago me quitaba tiempo. El coste simulado refleja el esfuerzo invertido."

**Visual:** Diagrama Gantt simplificado o tabla de fases.

---

## Diapositiva 13: Roadmap y Mejoras Futuras
**Mejoras de Seguridad (Alta prioridad):**
- Spring Security, JWT, BCrypt, CSRF

**Funcionales:**
- Migración a React
- Categorías de productos
- Sistema de notificaciones (email)
- Pasarela de pago

**Técnicas:**
- Tests automatizados (JUnit, Mockito)
- CI/CD (GitLab CI)
- @Version para optimistic locking

**Negocio:**
- Panel de estadísticas (ventas, productos más pedidos)

**Speaker Notes:**
"Como mejoras futuras, priorizo seguridad con Spring Security. También quiero migrar el frontend a React, añadir categorías, notificaciones y pasarela de pago. Y tests automatizados para producción."

**Visual:** Roadmap visual con prioridades (Alta, Media, Baja).

---

## Diapositiva 14: Conclusiones
**Logros:**
- Prototipo funcional completo en Docker
- API REST documentada (Swagger)
- Frontend responsive con Bootstrap/DataTables
- Base sólida para evolución

**Aprendizajes:**
- Arquitectura multicapa (Controller-Service-Repository)
- Gestión de estado stateless vs stateful
- Trade-offs entre rapidez y seguridad

**Aplicabilidad:**
- Solución real para pequeños negocios
- Código abierto (MIT), adaptable a otros sectores

**Speaker Notes:**
"Como conclusión, el proyecto cumple su objetivo: digitalizar una panadería con una solución funcional y escalable. Aprendí mucho sobre arquitectura, trade-offs y documentación. Y el código está disponible para que cualquiera lo adapte."

**Visual:** Imagen final: logo o captura de la app funcionando.

---

## Diapositiva 15: Agradecimientos y Contacto
**Agradecimientos:**
- Tutores, compañeros, recursos (Spring Docs, StackOverflow)

**Contacto:**
- Email: adriannoia104@gmail.com
- GitLab: [RepoGitLab](https://gitlab.iessanclemente.net/dawd/a22adrianfh)
- README y documentación completa en `doc/doc.md`

**Speaker Notes:**
"Gracias por vuestra atención. Si tenéis preguntas, adelante. Dejo mi contacto para cualquier duda."

**Visual:** Logo IES San Clemente, email y enlace GitLab.

---

## Lista de Capturas Recomendadas

1. **Login:** `login.html` con formulario de email/contraseña
2. **Catálogo cliente:** Vista de productos con botón "Añadir al carrito"
3. **Carrito:** Modal con productos seleccionados y botón "Realizar Pedido"
4. **Pedido confirmado:** Mensaje de éxito o tabla en `mis-pedidos.html`
5. **Dashboard admin:** Vista con tabs (Clientes, Productos, Pedidos)
6. **Tabla productos:** Con botones de edición, stock, imagen, eliminar
7. **Modal detalles pedido:** Vista de líneas de pedido y cambio de estado
8. **Swagger UI:** Pantalla con endpoints visibles y botón "Try it out"
9. **Terminal Docker:** Comando `docker compose up` ejecutándose
10. **Diagrama arquitectura:** Exportar el diagrama Mermaid de README como imagen

---

## Tiempo estimado por sección (7-10 minutos total)

- Portada + Objetivo: 1 min
- Contexto + Arquitectura: 1.5 min
- Tecnologías + Modelo: 1 min
- Flujos (Cliente + Admin): 2 min (lo más visual)
- Pruebas + Seguridad: 1.5 min
- Despliegue + Planificación: 1 min
- Roadmap + Conclusiones: 1.5 min
- Preguntas: ~2 min

**Total:** ~9 minutos de presentación + preguntas.

---

## Consejos finales

- **Paleta de colores:** Bootstrap-like (azul, blanco, gris)
- **Poco texto por slide:** Usa bullets cortos, no párrafos
- **Capturas grandes:** Que se vean bien desde lejos
- **Practica flujo:** Cliente → Admin → Seguridad → Futuro
- **Demuestra algo en vivo (opcional):** Login + crear pedido en 30 seg

¡Listo para Canva! Exporta a PowerPoint y ajusta tamaños/fuentes según necesites.
