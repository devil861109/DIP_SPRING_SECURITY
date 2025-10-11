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

