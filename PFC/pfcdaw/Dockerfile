# ===== ETAPA 1: COMPILACIÓN (BUILD) =====
# Usa imagen con JDK 21 (necesario para compilar)
FROM eclipse-temurin:21-jdk AS build

# Establece directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia archivos necesarios para compilar
# Configuración de Maven (pom.xml)
COPY pom.xml .
# Código fuente Java
COPY src ./src

# Instala Maven para compilar el proyecto
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*     # Limpia caché para reducir tamaño

# Variables de entorno para Maven
ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"

# Compila el proyecto: genera el archivo .jar
# -ntp: no muestra barra de progreso | -DskipTests: omite tests
RUN mvn -ntp -DskipTests package

# ===== ETAPA 2: IMAGEN FINAL (RUNTIME) =====
# Usa imagen con JRE 21 (más ligera, solo ejecuta, no compila)
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copia SOLO el .jar desde la etapa anterior (no copia Maven ni código fuente)
COPY --from=build /app/target/*.jar app.jar

# Informa que la aplicación usa el puerto 8080
EXPOSE 8080

# Comando que se ejecuta al arrancar el contenedor
ENTRYPOINT ["java", "-jar", "app.jar"]

