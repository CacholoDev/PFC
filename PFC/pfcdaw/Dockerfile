# ===== ETAPA 1: COMPILACIÓN (BUILD) =====
# Usa imagen con JDK 21 (necesario para compilar + jar)
FROM eclipse-temurin:21-jdk AS build

# Establece directorio de traballo dentro do contedor
WORKDIR /app

# Copia arquivos necesarios para compilar
# Configuración de Maven (pom.xml)
COPY pom.xml .
# Código fuente Java
COPY src ./src

# Instala Maven para compilar o proxecto
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*     # Limpa caché para reducir tamaño
# Variables de entorno para Maven
ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"

# Compila o proxecto + .jar
# -ntp: non mostra barra de progreso
#RUN mvn -ntp -DskipTests package
# sin ntp pa ver como vai a construccion do jar
RUN mvn -DskipTests package

# ===== ETAPA 2: IMAGEN FINAL (RUNTIME) =====
# Usa imaxe con JRE 21 (+ lixeira, solo executa, non compila)
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copia SOLO o .jar desde a etapa anterior (non copia Maven nin código fonte)
COPY --from=build /app/target/*.jar app.jar

# Crear directorio para imaxes subidas polo admin (uploads) e asegurar que existe
RUN mkdir -p /app/static/images/uploads

# Informa que a aplicación usa o porto 8080
EXPOSE 8080

# Comando que se executa o arrancar o contenedor
ENTRYPOINT ["java", "-jar", "app.jar"]

