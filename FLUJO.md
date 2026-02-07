# Flujo de Autenticación en Spring MVC con Thymeleaf y DaoAuthenticationProvider

Este documento describe el flujo completo de autenticación en una aplicación Spring MVC con vistas Thymeleaf, utilizando `DaoAuthenticationProvider`, `UserDetailsService` personalizado y el manejo implícito del `SecurityContextHolder`.

---

## Arquitectura involucrada

| Capa         | Elemento clave                                                                                                       |
|--------------|----------------------------------------------------------------------------------------------------------------------|
| **Vista (Thymeleaf)** | Formulario HTML de login                                                                                             |
| **Controlador (Spring MVC)** | No maneja login directamente (Spring Security intercepta)                                                            |
| **Modelo** | Entidad `UserInfo`, repositorio JPA                                                                                  |
| **Seguridad** | Filtro de login, `AuthenticationManager`, `DaoAuthenticationProvider`, `UserDetailsService`, `SecurityContextHolder` |


## Flujo paso a paso

---

### 1. Formulario Thymeleaf envía credenciales

```html
<form th:action="@{/login}" method="post">
  <input type="text" name="username" />
  <input type="password" name="password" />
  <button type="submit">Iniciar sesión</button>
</form>
```

El formulario envía un POST a /login.

Spring Security intercepta esta ruta automáticamente mediante el filtro `UsernamePasswordAuthenticationFilter`.

NOTA: Cuando pasas por los filtros de Spring Security, como en el flujo clásico con formulario de login (Thymeleaf o no), el filtro `UsernamePasswordAuthenticationFilter` es el responsable de crear el UsernamePasswordAuthenticationToken con las credenciales que el usuario envía.
Sino, pasa en autom'atico al `AuthenticationProvider` y este crea el `UsernamePasswordAuthenticationToken`.

---

### 2. Spring Security crea un UsernamePasswordAuthenticationToken no autenticado

```java
new UsernamePasswordAuthenticationToken(username, password);
```

Contiene:

`principal`: nombre de usuario

`credentials`: contraseña en texto plano

`authenticated`: false

---

### Paso 3: `AuthenticationManager` delega al `DaoAuthenticationProvider`

Este paso ocurre dentro del flujo de autenticación de Spring Security cuando se utiliza el formulario de login clásico (por ejemplo, con Thymeleaf). Aquí se explica cómo el `AuthenticationManager` coordina el proceso y delega la validación de credenciales al `DaoAuthenticationProvider`.
Aqui usamos `DaoAuthenticationProvider`.

---

#### ¿Qué es el `AuthenticationManager`?

Es el componente central de Spring Security que **coordina la autenticación**. No valida credenciales directamente, sino que **delegará esa tarea a uno o más `AuthenticationProvider`s** registrados en su configuración.

#### ¿Qué es un `AuthenticationProvider`?

Es una interfaz que define cómo autenticar un tipo específico de `Authentication`. Spring incluye varias implementaciones, y la más común es:

- **`DaoAuthenticationProvider`**: valida credenciales contra una base de datos usando un `UserDetailsService`.

---

### 4. `DaoAuthenticationProvider` llama al `UserDetailsService` personalizado

Este paso ocurre cuando el `DaoAuthenticationProvider` recibe un `UsernamePasswordAuthenticationToken` con las credenciales del usuario. Para validar si el usuario existe y obtener sus datos, el proveedor delega la carga del usuario al componente `UserDetailsService`.

#### ¿Qué es `UserDetailsService`?

Es una interfaz de Spring Security que define un único método:

```java
UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
```

Su propósito es recuperar los datos del usuario (nombre, contraseña, roles, estado de cuenta) desde una fuente externa — normalmente una base de datos.

---

### 5. Validación de credenciales

Este paso ocurre dentro del `DaoAuthenticationProvider`, después de que se ha obtenido el objeto `UserDetails` desde el `UserDetailsService`. Aquí se realiza la comparación entre la contraseña enviada por el usuario y la contraseña almacenada en la base de

```java
if (passwordEncoder.matches(rawPassword, userDetails.getPassword())) {
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        } else {
        throw new BadCredentialsException("Credenciales inválidas");
}
```

---

### 6. Contexto y sesión

#### ¿Qué se guarda en la sesión?

Si la aplicación tiene sesiones habilitadas (por ejemplo, en aplicaciones MVC con Thymeleaf), Spring Security guarda automáticamente:

- El `SecurityContext` completo.
- Esto incluye el `Authentication` autenticado.
- Por lo tanto, los datos del `UserDetails` también están disponibles indirectamente.
- Esto se hace **implícitamente** por el framework.

---

### 7. Persistencia en sesión y el rol de `JSESSIONID`

Cuando Spring Security autentica exitosamente a un usuario en una aplicación web con sesiones habilitadas (como en Spring MVC con Thymeleaf), el contexto de seguridad se guarda en la sesión HTTP. El identificador clave que representa esa sesión es el **`JSESSIONID`**.

#### ¿Qué es `JSESSIONID`?

- Es una **cookie generada por el servidor** (por defecto en aplicaciones Java EE/Spring).
- Identifica de forma única la sesión HTTP del usuario en el servidor.
- Se envía al navegador del cliente como parte de la respuesta HTTP después del login.

Ejemplo de cabecera HTTP:

Set-Cookie: JSESSIONID=ABC123XYZ456; Path=/; HttpOnly

#### ¿Cómo se usa `JSESSIONID`?

1. El usuario inicia sesión correctamente.
2. El servidor crea una sesión HTTP y asigna un `JSESSIONID`.
3. El navegador guarda esa cookie.
4. En cada solicitud posterior, el navegador envía el `JSESSIONID` automáticamente.
5. El servidor usa ese ID para recuperar el `SecurityContext` y mantener al usuario autenticado.

---

### 8. Redirección automática

- Spring Security redirige al usuario a la URL configurada (`/dashboard`, `/home`, etc.).
- Desde cualquier controlador o vista, puedes acceder al usuario autenticado:

```java
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
UserDetails user = (UserDetails) auth.getPrincipal();
String username = user.getUsername();
```

---

## JWT (JSON Web Tokens) - Alternativa a Sesiones

Además del flujo clásico con sesiones y `JSESSIONID`, tu aplicación también utiliza **JWT** para autenticación stateless. Esta sección explica cómo funciona.

---

### ¿Por qué JWT en lugar de sesiones?

| Aspecto | Sesiones (JSESSIONID) | JWT |
|--------|----------------------|-----|
| **Estado** | Stateful (servidor almacena sesión) | Stateless (cliente almacena token) |
| **Escalabilidad** | Difícil en múltiples servidores | Ideal para microservicios |
| **Almacenamiento** | Cookie HTTP automática | Cookie, localStorage, etc. |
| **Tamaño** | Pequeño (solo ID) | Más grande (contiene datos) |
| **Expiración** | Manejada por servidor | Autónoma en el token |

---

### Flujo de JWT en tu aplicación

#### 1. Generación del JWT

Cuando el usuario inicia sesión correctamente en `/token`:

```java
// Desde HomeController
Authentication authentication = authenticate(loginUserRequest.getUsername(),
        loginUserRequest.getPassword());
UserDetailsImpl usuario = (UserDetailsImpl) authentication.getPrincipal();
String jwtToken = jwtTokenProvider.generateJwtToken(usuario);
```

El método `generateJwtToken()` crea un token JWT con:

```java
public String generateJwtToken(UserDetailsImpl user) {
    key = Keys.hmacShaKeyFor(secret.getBytes());
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + jwtExpirationInMs * 1000L);
    
    return Jwts.builder()
            .subject("UNAM")                          // Asunto
            .issuer(user.getUsername())               // Emisor
            .audience().add("JAVA").and()            // Audiencia (JJWT 0.12.5: Set<String>)
            .claim("principal", user)                 // Datos personalizados
            .claim("auth", user.getAuthorities())     // Autoridades/roles
            .claim("issid", user.getId())             // ID del usuario
            .claim("issname", user.getName())         // Nombre del usuario
            .issuedAt(now)                           // Emitido en
            .expiration(expiryDate)                   // Expira en
            .signWith(key)                            // Firma con clave HMAC-SHA512 (JJWT 0.12.5)
            .compact();                               // Genera token
}
```

**Cambios en JJWT 0.12.5:**
- ✅ `Jwts.builder()` usa métodos sin prefijo `set` (fluent API)
- ✅ `audience().add().and()` devuelve `Set<String>` en lugar de `String`
- ✅ `signWith(key)` detecta automáticamente el algoritmo (HMAC-SHA512 por defecto con claves simétricas)
- ✅ No requiere importar `SignatureAlgorithm` (deprecado en 0.12.5)

---

#### 2. Respuesta al cliente

Se retorna un `JwtRequest` que incluye:

```java
JwtRequest jwtRequest = new JwtRequest(
    jwtToken,                              // El token JWT
    usuario.getId(),                       // ID del usuario
    usuario.getEmail(),                    // Email del usuario
    jwtTokenProvider.getExpiryDuration(),  // Duración en ms
    authentication.getAuthorities()        // Autoridades
);
```

El cliente recibe el token y lo almacena (típicamente en una cookie o localStorage).

---

#### 3. Envío del JWT en solicitudes posteriores

El cliente envía el JWT en las siguientes solicitudes (generalmente en la cookie `token`):

```http
GET /dashboard HTTP/1.1
Cookie: token=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJVTkFNIi...
```

---

#### 4. Validación y extracción del JWT

El filtro `JWTAuthenticationFilter` intercepta cada solicitud:

```java
@Override
protected void doFilterInternal(HttpServletRequest request, 
                               HttpServletResponse response,
                               FilterChain filterChain) throws ServletException, IOException {
    
    // Extraer token de la cookie
    String jwt = "";
    if(request.getCookies() != null)
        for(Cookie cookie: request.getCookies())
            if(cookie.getName().equals("token"))
                jwt = cookie.getValue();
    
    // Validar token
    if (tokenProvider.validateJwtToken(jwt)) {
        Claims body = tokenProvider.getClaims(jwt);
        // Procesar autoridades y usuario
    }
}
```

---

#### 5. Validación del JWT (JJWT 0.12.5)

El método `validateJwtToken()` ahora usa la nueva API:

```java
public boolean validateJwtToken(String authToken) {
    try {
        key = Keys.hmacShaKeyFor(secret.getBytes());
        Jwts.parser()                    // CAMBIO: parser() en lugar de parserBuilder()
                .verifyWith(key)         // CAMBIO: verifyWith() en lugar de setSigningKey()
                .build()
                .parseSignedClaims(authToken)  // CAMBIO: parseSignedClaims() en lugar de parseClaimsJws()
        return true;
    } catch (MalformedJwtException exception) {
        log.error("Invalid JWT token -> Message: {}", exception.getMessage());
    } catch (ExpiredJwtException exception) {
        log.error("Expired JWT token -> Message: {}", exception.getMessage());
    } catch (UnsupportedJwtException exception) {
        log.error("Unsupported JWT token -> Message: {}", exception.getMessage());
    } catch (IllegalArgumentException exception) {
        log.error("JWT claims string is empty -> Message: {}", exception.getMessage());
    }
    return false;
}
```

**Cambios en JJWT 0.12.5:**
- ✅ `Jwts.parser()` en lugar de `Jwts.parserBuilder()` (más directo)
- ✅ `verifyWith(key)` en lugar de `setSigningKey(key)` (más explícito)
- ✅ `parseSignedClaims()` en lugar de `parseClaimsJws()` (nombrado más claramente)
- ✅ `.getPayload()` en lugar de `.getBody()` (nombre más consistente)

---

#### 6. Extracción de claims del JWT

Métodos como `getClaims()`, `getSubject()`, `getIssuer()`, etc. siguen el mismo patrón:

```java
public Claims getClaims(String token) {
    key = Keys.hmacShaKeyFor(secret.getBytes());
    return Jwts.parser()              // CAMBIO en 0.12.5
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();            // CAMBIO: getPayload() en lugar de getBody()
}

public String getIssuer(String token) {
    key = Keys.hmacShaKeyFor(secret.getBytes());
    Claims claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    return claims.getIssuer();
}

public String getAudience(String token) {
    key = Keys.hmacShaKeyFor(secret.getBytes());
    Claims claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    // CAMBIO: getAudience() ahora devuelve Set<String> en lugar de String
    var audience = claims.getAudience();
    return audience != null && !audience.isEmpty() ? audience.iterator().next() : null;
}
```

---

### Resumen de cambios JJWT 0.11.5 → 0.12.5

| Aspecto | 0.11.5 | 0.12.5 |
|--------|--------|--------|
| **Builder** | `setSubject()`, `setIssuer()`, etc. | `subject()`, `issuer()`, etc. |
| **Parser** | `Jwts.parserBuilder()` | `Jwts.parser()` |
| **Signing Key** | `.setSigningKey(key)` | `.verifyWith(key)` |
| **Parse JWT** | `.parseClaimsJws()` | `.parseSignedClaims()` |
| **Get Body** | `.getBody()` | `.getPayload()` |
| **Sign** | `.signWith(key, SignatureAlgorithm.HS512)` | `.signWith(key)` (auto-detecta) |
| **Audience** | `String` | `Set<String>` |
| **Deprecations** | Ninguna | Métodos anteriores deprecados |

---

### Importancia de la actualización a 0.12.5

✅ **Seguridad mejorada**: Mejor validación de firmas y manejo de claves
✅ **API moderna**: Fluent API más intuitiva
✅ **Performance**: Mejor manejo de memoria
✅ **Soporte a futuro**: Compatibilidad con Java 11+ garantizada
✅ **Código limpio**: Menos deprecaciones y advertencias

---

### Integración JWT + Sesiones

Tu aplicación usa **ambos mecanismos**:

1. **Sesiones (JSESSIONID)**: Para el flujo clásico de login con formulario Thymeleaf
2. **JWT**: Para autenticación stateless en rutas protegidas (`/admin`, `/user`, etc.)

Esto permite:
- Flexibilidad en múltiples tipos de clientes (web tradicional, SPA, mobile)
- Mejor escalabilidad en arquitecturas distribuidas
- Seguridad en capas
