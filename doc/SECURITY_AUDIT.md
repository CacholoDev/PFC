# 🔒 Auditoría de Seguridad - JWT + Spring Security

**Fecha:** 27 de enero de 2026  
**Estado:** ✅ JWT y Spring Security completamente implementados  
**Vulnerabilidades encontradas:** 6  
**Vulnerabilidades corregidas:** 6  

---

## ✅ IMPLEMENTACIONES COMPLETADAS

### 1. **JWT (JSON Web Tokens)**
- ✅ JwtTokenProvider: Genera, valida y extrae claims de tokens
- ✅ JwtTokenFilter: Intercepta requests y valida tokens
- ✅ Token contiene: email, role, id
- ✅ Expiración: 1 hora (configurable vía `app.jwt.expiration`)
- ✅ Secret key: 64+ caracteres (HMAC-SHA512)
- ✅ Tokens en localStorage del frontend

### 2. **Spring Security**
- ✅ BCrypt para contraseñas (10 rounds)
- ✅ Sesiones STATELESS (sin cookies de sesión)
- ✅ CSRF deshabilitado (API REST)
- ✅ CORS configurado correctamente
- ✅ @PreAuthorize en todos los endpoints
- ✅ Roles: USER, ADMIN

### 3. **Validaciones de Entrada**
- ✅ @Valid en todos los @RequestBody
- ✅ @NotNull, @NotBlank, @Email en DTOs
- ✅ @Size, @Min, @Max en campos numéricos
- ✅ @Pattern para teléfonos (9 dígitos)

### 4. **Protección de Datos**
- ✅ Passwords nunca en respuestas (@JsonProperty WRITE_ONLY)
- ✅ @ToString.Exclude en passwords y colecciones lazy
- ✅ Logs no incluyen contraseñas

---

## 🔴 VULNERABILIDADES ENCONTRADAS Y CORREGIDAS

### 🔴 CRÍTICA #1: Subida de archivos sin validación de tipo
**Ubicación:** `ProductoService.actualizarFoto()`  
**Problema:** Cualquier tipo de archivo podía subirse (.exe, .php, .jsp)  
**Impacto:** Ejecución remota de código, shell reversa  
**Solución aplicada:**
```java
// ANTES: Solo validaba nombre con ".."
if (nombreArchivo == null || nombreArchivo.contains("..")) { ... }

// AHORA: 4 niveles de validación
✅ Tamaño máximo: 5MB
✅ Content-Type: solo "image/*"
✅ Extensión: .jpg, .jpeg, .png, .gif, .webp
✅ Nombre sin path traversal (..)
```

### 🔴 CRÍTICA #2: Sin límite de tamaño de archivo
**Ubicación:** `application.properties`  
**Problema:** Límite de 15MB muy alto  
**Impacto:** DoS por agotamiento de disco/memoria  
**Solución aplicada:**
```properties
# ANTES
spring.servlet.multipart.max-file-size=15MB

# AHORA
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=5MB
spring.servlet.multipart.enabled=true
```

### 🟡 MEDIA #3: CORS deshabilitado incorrectamente
**Ubicación:** `SecurityConfig`  
**Problema:** `.cors(cors -> cors.disable())` deshabilitaba CORS completamente  
**Impacto:** Sin protección CORS real  
**Solución aplicada:**
```java
// ANTES
.cors(cors -> cors.disable()) // ❌

// AHORA
.cors(cors -> {}) // Usa configuración de WebConfig
```
Ahora usa correctamente `WebConfig.addCorsMappings()` que permite solo:
- http://localhost:5500, http://127.0.0.1:5500, http://localhost:8081

### 🟡 MEDIA #4: Sin límite máximo en cantidades
**Ubicación:** `StockUpdateDto`, `PedidoService`  
**Problema:** `@Min(1)` pero no `@Max`, permitía Integer.MAX_VALUE  
**Impacto:** Overflow de stock, DoS en base de datos  
**Solución aplicada:**
```java
// StockUpdateDto
@Max(value = 10000, message = "La cantidad no puede superar 10,000 unidades")

// PedidoService.createPedido()
if (cantidad > 1000) {
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        "La cantidad no puede superar 1000 unidades por pedido");
}
```

### 🟢 BAJA #5: Email duplicado sin mensaje claro
**Ubicación:** `ClienteController.updateCliente()`  
**Problema:** `ResponseEntity.status(409).build()` sin body  
**Impacto:** Frontend recibe 409 sin saber por qué  
**Solución aplicada:**
```java
// ANTES
return ResponseEntity.status(HttpStatus.CONFLICT).build();

// AHORA
throw new ResponseStatusException(HttpStatus.CONFLICT, 
    "El email '" + email + "' ya está en uso por otro cliente");
```

### 🟢 BAJA #6: Lazy collections en toString()
**Ubicación:** Entidades (ClienteEntity, ProductoEntity, PedidoEntity)  
**Problema:** Lombok incluía colecciones lazy en `toString()`  
**Impacto:** LazyInitializationException → doble JSON en respuesta  
**Solución aplicada:**
```java
@ToString.Exclude
private List<PedidoEntity> pedidos;

@ToString.Exclude
private String password; // Seguridad adicional
```

---

## 🛡️ BUENAS PRÁCTICAS IMPLEMENTADAS

### Validación de entrada
- ✅ Todos los endpoints usan `@Valid @RequestBody`
- ✅ DTOs tienen validaciones Bean Validation
- ✅ Límites realistas (@Max, @Size)
- ✅ Sanitización de nombres de archivo

### Autorización granular
```java
@PreAuthorize("hasRole('ADMIN')")    // Solo admins
@PreAuthorize("isAuthenticated()")    // Cualquier usuario logueado
// GET /pedidos/cliente/{id} → Validar que solo vea sus propios pedidos
```

### Logging seguro
```java
log.info("Login exitoso: {}", email);  // ✅ OK
// NUNCA:
// log.info("Password: {}", password);  // ❌ NUNCA
```

### Respuestas HTTP semánticas
- 401 Unauthorized → Token inválido/expirado
- 403 Forbidden → Token válido pero sin permisos
- 409 Conflict → Email duplicado
- 400 Bad Request → Validación fallida

---

## ⚠️ PENDIENTES / RECOMENDACIONES FUTURAS

### 1. Rate Limiting
**Problema:** Sin límite de peticiones por IP/usuario  
**Riesgo:** Fuerza bruta en /auth/login  
**Solución:** Implementar Bucket4j o Spring Security rate limiting
```java
// Ejemplo con Bucket4j
@RateLimiter(name = "loginLimiter", fallbackMethod = "rateLimitFallback")
@PostMapping("/auth/login")
```

### 2. Refresh Tokens
**Problema:** Token expira en 1 hora, usuario debe re-loguear  
**Solución:** Implementar refresh tokens (JWT de larga duración)
```
accessToken: 1h
refreshToken: 7 días
```

### 3. Token Blacklist (logout)
**Problema:** No hay logout real, token sigue válido hasta expirar  
**Solución:** Redis con blacklist de tokens revocados
```java
redisTemplate.opsForValue().set("blacklist:" + token, "1", 1, TimeUnit.HOURS);
```

### 4. Auditoría de cambios
**Problema:** No se registran modificaciones de datos sensibles  
**Solución:** Spring Data Envers o tabla de auditoría
```java
@CreatedBy, @LastModifiedBy, @CreatedDate, @LastModifiedDate
```

### 5. HTTPS obligatorio
**Problema:** En producción debe forzar HTTPS  
**Solución:** Nginx con redirect 301, HSTS headers
```nginx
if ($scheme = http) {
    return 301 https://$server_name$request_uri;
}
add_header Strict-Transport-Security "max-age=31536000" always;
```

### 6. Sanitización XSS
**Problema:** Campos de texto no sanitizan HTML/JS  
**Solución:** OWASP Java Encoder en DTOs
```java
String nombreSanitizado = Encode.forHtml(nombre);
```

### 7. SQL Injection
**Estado:** ✅ JPA protege automáticamente con prepared statements  
**Pero:** Cuidado con `@Query` con concatenación manual
```java
// ❌ MAL
@Query("SELECT c FROM Cliente c WHERE email = " + email)

// ✅ BIEN
@Query("SELECT c FROM Cliente c WHERE email = :email")
```

---

## 📋 CHECKLIST FINAL

- [x] JWT implementado y funcionando
- [x] Spring Security con roles
- [x] BCrypt en passwords
- [x] @PreAuthorize en endpoints
- [x] Validaciones @Valid en todos los @RequestBody
- [x] Subida de archivos segura
- [x] Límites de tamaño
- [x] CORS configurado
- [x] Logs sin contraseñas
- [x] @ToString.Exclude en datos sensibles
- [x] Mensajes de error claros
- [ ] Rate limiting (futuro)
- [ ] Refresh tokens (futuro)
- [ ] Token blacklist (futuro)
- [ ] HTTPS en producción (futuro)

---

## 🎯 CONCLUSIÓN

El sistema tiene **JWT + Spring Security completamente funcional y seguro** para un proyecto académico/pequeña empresa.

Las vulnerabilidades críticas han sido **todas corregidas**. Las recomendaciones futuras son para escalabilidad y producción enterprise.

