# Plataforma web de pedidos para panadería

## Descripción

Este proyecto consiste en el desarrollo de una **aplicación web para la gestión de pedidos en una panadería**.  

La idea principal es ofrecer a los clientes la posibilidad de consultar el catálogo de productos disponibles (panes, bollería y repostería), realizar pedidos online  y permitir a la panadería gestionar dichos pedidos.

El objetivo es digitalizarle la panaderia a mi prima, simplificando tanto la experiencia de compra del cliente como los pedidos por parte del negocio, con posibilidad de ser ampliado en el futuro con más funcionalidades (como notificaciones, pasarela de pago),también me gustaría migrar el front a React cuando controle un poco mas de la librería

## Instalación / Puesta en marcha

1. Clonar el repositorio: Aquí tengo pensado con el repo que nos dais en el GitLab poner enlace al repo del Spring initalizer en github o en el gitlab mismo para que lo podais clonar 

2. Acceder al directorio del proyecto y levantar el backend: abrirlo en vscode y darle al run en el main de springBoot o  ./mvnw spring-boot:run

3. Instalar xamp, en phpMyAdmin creamos bbdd panaderia y configurar el application.properties con usuario root y una contraseña vacia

 `spring.datasource.url=jdbc:mysql://localhost:3306/panaderia`
    
 `spring.datasource.username=root`
    
 `spring.datasource.password=`
    
 `spring.jpa.hibernate.ddl-auto=update`
    
 `spring.jpa.show-sql=true`
    
 `spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect`

4. Frontend:

    - VSCode + LiveServer
## Uso

- Los clientes podrán:

    - Navegar por el catálogo de productos.

    - Añadir productos al carrito.

    - Realizar un pedido.

- La panadería podrá:

    - Gestionar pedidos recibidos.

    - Actualizar disponibilidad de productos.

## Sobre el autor


Soy Adrián Fábregas, estudiante de DAW, además de tener un FP superior de Ed. Infantil pero la mayoría de mi vida laboral está relacionada con el mar. En la parte de promagación la conocí en 2022 y me causó mucho interés java y luego mas adelante empecé a aprender Spring Boot el verano antes de la FCT y el PFC, me gustaría desarrollar mi carrera de programador en ese ámbito aunque estoy abierto a todo, por ejemplo estoy aprendiendo React para la parte del front end además de seguir aprendiendo Spring Boot que aún no llevo mucho tiempo con el.  

Me decanté por este proyecto porque permite aplicar de forma práctica los conocimientos adquiridos en el ciclo, y además responde a una necesidad real de modernización en los pequeños negocios.

Contacto: adriannoia104@gmail.com  

## Licencia 

Este proyecto está licenciado bajo la [MIT License](LICENSE).
## Documentación

Este proyecto dispone de [documentación extendida](doc/doc.md) con detalles técnicos y de diseño.
## Guía de contribución

Las contribuciones son bienvenidas en forma de:

- Nuevas funcionalidades (ej.: notificaciones,mejora del carrito,mejora del FrontEnd migrandolo a React...).

- Corrección de errores.

- Mejora del código o de la documentación. 

Para colaborar:

1. Haz un fork del repositorio.

2. Crea una rama con tu mejora.

3. Envía un pull request.