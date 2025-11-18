### front

FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Crear directorio para páginas de error y copiar la página 404
RUN mkdir -p /usr/share/nginx/error_pages
RUN mkdir -p /usr/share/nginx/html/assets/images
 
COPY error_pages/404.html /usr/share/nginx/error_pages/404.html
COPY error_pages/assets/images/ /usr/share/nginx/html/assets/images/

### file
dsg_app_seguimiento_almacen_sql:
        container_name: dsg_app_seguimiento_almacen_sql
        image: mysql:8
        ports:
            - "33217:3306"
        command: --default-authentication-plugin=mysql_native_password
        environment:
            - MYSQL_ROOT_PASSWORD=root
            - MYSQL_USER=admin
            - MYSQL_PASSWORD=adminpass
            - MYSQL_DATABASE=seguimiento_almacen
            - DOCKER_TIMEZONE=Europe/Madrid
        volumes:
            - dsg_app_seguimiento_almacen_sqlFiles:/var/lib/mysql
            - '/etc/timezone:/etc/timezone:ro'
            - '/usr/share/zoneinfo/Europe/Madrid:/etc/localtime:ro'
        restart: always
 
volumes:
    dsg_app_seguimiento_almacen_sqlFiles: