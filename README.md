
##### Enlace a documentación: [doc](doc/doc.md)
##### Enlace repo GitLab: [RepoGitLab](https://gitlab.iessanclemente.net/dawd/a22adrianfh)

# Plataforma web de pedidos para panadería

## Índice

- [Índice](#índice)
- [Requisitos previos](#requisitos-previos)
- [Descripción](#descripción)
- [Estado del Proyecto](#estado-del-proyecto)
- [Instalación / Puesta en marcha](#instalación--puesta-en-marcha)
  - [Opción recomendada: Despliegue con Docker](#opción-recomendada-despliegue-con-docker)
  - [Opción alternativa: XAMPP/MySQL local](#opción-alternativa-xamppmysql-local)
- [FAQ - Preguntas frecuentes](#faq---preguntas-frecuentes)
  - [¿Por qué no arranca MySQL en Docker?](#por-qué-no-arranca-mysql-en-docker)
  - [¿Dónde se guardan los datos de la base de datos?](#dónde-se-guardan-los-datos-de-la-base-de-datos)
  - [¿Cómo cambio el puerto del backend o frontend?](#cómo-cambio-el-puerto-del-backend-o-frontend)
  - [¿Puedo usar la app sin Docker?](#puedo-usar-la-app-sin-docker)
  - [¿Cómo restauro la base de datos si borro el volumen?](#cómo-restauro-la-base-de-datos-si-borro-el-volumen)
- [Uso](#uso)
- [Sobre el autor](#sobre-el-autor)
- [Licencia](#licencia)
- [Documentación](#documentación)
- [Guía de contribución](#guía-de-contribución)

## Requisitos previos

| Herramienta         | Versión recomendada | ¿Para qué?                |
|---------------------|---------------------|---------------------------|
| Docker              | 20.10+              | Despliegue recomendado    |
| Docker Compose      | 1.29+ (o integrado) | Orquestar servicios       |
| Java (JDK)          | 21+                 | Solo si usas modo local   |
| XAMPP/MySQL         | 8+                  | Solo si usas modo local   |
| VSCode/IDE          | -                   | Edición de código         |


## Descripción

Este proyecto consiste en el desarrollo de una **aplicación web para la gestión de pedidos en una panadería aplicable también con pequeñas modificaciones a pequeños negocios en general que se quieran digitalizar**.

La idea principal es ofrecer a los clientes la posibilidad de consultar el catálogo de productos disponibles (panes, bollería y repostería), realizar pedidos online y permitir a la panadería gestionar dichos pedidos.

El objetivo es digitalizar empresas pequeñas en este caso en el sector panadero, simplificando tanto la experiencia de compra del cliente como los pedidos por parte del negocio, con posibilidad de ser ampliado en el futuro con más funcionalidades (como notificaciones, pasarela de pago). También me gustaría migrar el front a React cuando controle un poco más de la librería y tenga algo más de tiempo ya que con la FCT en Santiago y lo poco que dura la FCT + PFC no dispongo de mucho espacio de tiempo para hacer un proyecto como el que me gustaría desarrollar y el cual seguiré trabajándolo cuando finalice el ciclo.

**Diagrama de arquitectura general**: Muestra los actores principales (Clientes y Panadería), las funcionalidades disponibles para cada uno, y las mejoras futuras planificadas.

```mermaid
graph TD
    A[Plataforma Web Panadería] --> B[Clientes]
    A --> C[Panadería/Negocio]
    
    B --> B1[Catálogo Productos]
    B --> B2[Carrito Compra]
    B --> B3[Pedidos Online]
    
    C --> C1[Gestión Pedidos]
    C --> C2[Actualizar Disponibilidad]
    
    D[Futuras Mejoras] --> D1[Migración a React]
    D --> D2[Pasarela de Pago]
    D --> D3[Sistema Notificaciones]
    D --> D4[".@Version" en ProductosEntity: Optimistic locking]
    
    A --> D
```

## Estado del Proyecto

✅ **BACKEND COMPLETADO**
**(Spring Boot)**
- API REST funcional con 15+ endpoints
- Gestión completa de Clientes, Productos y Pedidos
- Sistema de stock automático con transacciones
- Validaciones en múltiples capas
- Precisión decimal exacta con BigDecimal

⏳ **FRONTEND EN DESARROLLO**
**(js+html+css+Bootstrap)**
- Pendiente: Catálogo de productos, carrito y formulario de pedido


## Instalación / Puesta en marcha

### Opción recomendada: Despliegue con Docker


1. **Clonar el repositorio**
2. **Levantar los servicios con Docker Compose**:
   ```bash
   cd PFC/pfcdaw
   docker-compose up
   ```
   Esto levantará automáticamente:
   - MySQL (con persistencia de datos)
   - Backend Spring Boot
   - Nginx (sirviendo el frontend y como proxy)

3. **Acceder a la aplicación:**
   - Frontend: [http://localhost:8081](http://localhost:8081)
   - Swagger UI: [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)
   - Backend: non accesible por seguridad solo 8081 abierto

4. **Comandos Docker útiles:**
   - Parar los servicios: `docker compose down`
   - Ver logs: `docker compose logs -f`
   - Reconstruir todo: `docker-compose up --build`

### Opción alternativa: XAMPP/MySQL local

Si prefieres usar XAMPP y MySQL local, debes ajustar la configuración en `application.properties`:

```properties
# CONFIGURACIÓN DE BASE DE DATOS //
#spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/panaderiaPFC?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true}
#spring.datasource.username=${DB_USERNAME:root}
#spring.datasource.password=${DB_PASSWORD:}
spring.datasource.url=jdbc:mysql://mysql:3306/panaderiaPFC?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true
spring.datasource.username=admin
spring.datasource.password=admin123
```

**Para usar XAMPP:**
- Descomenta las 3 primeras líneas y comenta las otras 3 (así la app usará tu MySQL local en vez del contenedor Docker).

1. **Clonar el repositorio**: clonar desde gitlab

2. **Acceder al directorio del proyecto y levantar el backend**: abrirlo en vscode y darle al run en el main de springBoot o `./mvnw spring-boot:run`

3. **Instalar XAMPP**, en phpMyAdmin crear base de datos `panaderiaPFC`:
   - Abrir XAMPP Control Panel → Start MySQL
   - Ir a `http://localhost/phpmyadmin`
   - Nueva base de datos: `panaderiaPFC` (cotejamiento: `utf8mb4_unicode_ci`)

4. **Configurar application.properties** (ya configurado con valores por defecto):
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/panaderiaPFC?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

5. **Levantar el backend**:
```bash
cd PFC/pfcdaw
./mvnw spring-boot:run
```
O desde VSCode: Run → Start Debugging (F5) en `PfcdawApplication.java`

El backend arrancará en `http://localhost:8080`

6. **Cargar datos de prueba** (opcional):
   - Abrir phpMyAdmin → Base de datos `panaderiaPFC` → pestaña SQL
   - Copiar y pegar contenido de `src/main/resources/data-sample.sql`
   - Click **Continuar**
   - Esto creará 3 clientes y 4 productos de ejemplo

7. **Probar la API** con Postman:
```http
GET http://localhost:8080/productos
GET http://localhost:8080/clientes
POST http://localhost:8080/pedidos
```

1. **Frontend**: VSCode + LiveServer / endpoints para funcionalidad completa con la app arrancada


## FAQ - Preguntas frecuentes

### ¿Por qué no arranca MySQL en Docker?
- Asegúrate de que el puerto 3306 no está ocupado por otro MySQL local.
- Si usastes XAMPP antes, para el servicio MySQL de XAMPP antes de levantar Docker.
- Comprueba los logs con `docker compose logs mysql`.

### ¿Dónde se guardan los datos de la base de datos?
- En Docker, los datos de MySQL se guardan en un volumen persistente llamado `mysql_panaderia`.
- Así, aunque borres los contenedores, los datos no se pierden.

### ¿Cómo cambio el puerto del backend o frontend?
- Cambia la variable `SERVER_PORT` en el archivo `docker-compose.yml` o en `application.properties` para el backend.
- Cambia el mapeo de puertos en `docker-compose.yml` para Nginx (por ejemplo, `8081:80`).

### ¿Puedo usar la app sin Docker?
- Sí, usando XAMPP/MySQL local y ajustando `application.properties` como se indica arriba.

### ¿Cómo restauro la base de datos si borro el volumen?
- Si borras el volumen de Docker, los datos se pierden. Haz backups periódicos si es importante.



## Uso
Se trata de una aplicación sencilla para cumplir los tiempos de entrega, enfatizar en que seguiré trabajando en la app y que aplicaré distintas funcionalidades y mejoras.

**Los clientes podrán:**
- Navegar por el catálogo de productos
- Añadir productos al carrito
- Realizar un pedido

**La panadería podrá:**
- Gestionar pedidos recibidos
- Actualizar disponibilidad de productos

**Diagrama de interacciones básicas**: Representa los casos de uso principales del sistema desde la perspectiva de los actores.

```mermaid
sequenceDiagram
    Cliente->>Sistema: Consultar catálogo
    Cliente->>Sistema: Realizar pedido
    Panaderia->>Sistema: Gestionar pedidos
```
## Sobre el autor

Soy Adrián Fábregas, estudiante de DAW, tengo un FP superior de Ed. Infantil pero la mayoría de mi vida laboral está relacionada con ir al mar y vivir gracias a él y sus recursos, siempre respetándolo. En la parte de programación la conocí en 2022 cuando un amigo cercano vio la situación que atravesamos en las rías gallegas sobre todo en la de Noia que es donde yo trabajo y que cada año está más débil en todos los sentidos, me aconsejó meterme en este mundo y de 2022 compaginándolo con mi trabajo comencé a aprender y a entender este mundillo. Me causó mucho interés java y luego más adelante empecé a aprender Spring Boot el verano antes de la FCT y el PFC, me gustaría desarrollar mi carrera de programador en ese ámbito aunque estoy abierto a todo, por ejemplo estoy aprendiendo React para la parte del front además de seguir aprendiendo Spring Boot que aún no llevo mucho tiempo con él.

Me decanté por este proyecto porque permite aplicar de forma práctica los conocimientos adquiridos en el ciclo, y además responde a una necesidad real de modernización en los pequeños negocios y aunque no dispongo de mucho tiempo debido a la FCT, que vivo en Noia y la tengo en Santiago ya pierdo 10h más o menos todos los días además de llegar a casa algo cansado, además tengo 31 años y tengo bastantes responsabilidades personales que también requieren algo de tiempo, por tanto en esta primera versión inicial será algo más sencilla para cumplir con el PFC y aprobar el ciclo y luego seguir trabajándola y mejorándola en todos los sentidos, desde el back hasta el front migrándolo a React, la gestión de errores, distintas mejoras en la app...

**Contacto**: adriannoia104@gmail.com

## Licencia

Este proyecto está licenciado bajo la [MIT License](LICENSE).

Usaré MIT por la libertad total que tiene a la hora del uso o de la modificación del código y documentación, siendo flexible a la hora de trabajar con open source. Además es de fácil de entender y fomenta la innovación.

## Documentación

Este proyecto dispone de [documentación extendida](doc/doc.md) con detalles técnicos y diseño.

## Guía de contribución

Las contribuciones son bienvenidas en forma de:
- Nuevas funcionalidades (ej.: notificaciones, mejora del carrito, mejora del FrontEnd migrándolo a React...)
- Corrección de errores
- Mejora del código o de la documentación

Para colaborar:
1. Haz un fork del repositorio
2. Crea una rama con tu mejora
3. Envía un pull request

