# Refresh Token JWT (Implementacion y flujo)

## Cambios implementados

### 1) `JWTTokenProvider`

- Se agrego el claim `typ` para distinguir entre token de acceso y refresh.
- Se agrego `generateRefreshToken(...)`.
- Se agrego `validateRefreshToken(...)`.
- Se agrego `getRefreshExpiryDuration()`.

**Nuevo comportamiento:**
- El token de acceso se emite con `typ=access`.
- El refresh token se emite con `typ=refresh`.
- `validateJwtToken(...)` ahora valida que el token sea de tipo `access`.

### 2) `HomeController`

- En `/token` ahora se emiten **dos cookies**:
  - `token` (access token)
  - `refresh_token` (refresh token)
- Se agrego endpoint `POST /refresh` que:
  - Lee la cookie `refresh_token`.
  - Valida que sea un refresh token valido.
  - Genera un **nuevo access token** y un **nuevo refresh token**.
  - Rota las cookies con los tokens nuevos.

### 3) `SecurityConfiguration`

- Se permite el endpoint `/refresh` sin requerir un access token valido.

### 4) `CustomLogoutSuccessHandler`

- Ahora limpia **ambas cookies**: `token` y `refresh_token`.

---

## Flujo de ejecucion esperado

### A) Login (`POST /token`)

1. El usuario envia usuario + password.
2. Se autentica con `AuthenticationManager`.
3. Se generan dos tokens:
   - Access token (corta duracion).
   - Refresh token (duracion mayor).
4. El servidor envia ambas cookies:
   - `token`
   - `refresh_token`
5. El navegador las guarda y las manda en cada request.

### B) Uso normal de la app

- Las rutas protegidas siguen validando **solo** el access token (`token`).
- Si expira el access token, las peticiones fallaran (401 o redireccion al login, dependiendo del flujo).

### C) Renovacion (`POST /refresh`)

1. El cliente llama `/refresh`.
2. El servidor valida el `refresh_token`.
3. Si es valido:
   - Genera un nuevo access token.
   - Genera un nuevo refresh token.
   - Rota las cookies.
4. El usuario sigue autenticado sin reingresar credenciales.

---

## Que esperar al ejecutar el proyecto

- En el login se crean **dos cookies**.
- El access token expira segun `jwt.expirationDateInMs`.
- El refresh token expira segun `jwt.refreshExpirationDateInMs`.
- Si el access token expira pero el refresh sigue valido, `/refresh` permite continuar sin relogin.
- Si el refresh token expira o es invalido, el usuario debe volver a loguearse.

---

## Notas importantes

- El refresh token esta almacenado en cookie HttpOnly, por lo que no es accesible desde JavaScript.
- Esta implementacion es stateless: no guarda refresh tokens en BD.
- Para un nivel mayor de seguridad, puedes:
  - Persistir refresh tokens en BD y revocarlos.
  - Usar `Secure` y `SameSite=None` si hay HTTPS y cross-site.

---

## Configuracion usada

En `application.yml`:

```yaml
jwt:
  expirationDateInMs: 3600
  refreshExpirationDateInMs: 900
```

`expirationDateInMs` = 1 hora

`refreshExpirationDateInMs` = 15 minutos

