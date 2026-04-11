# DIP_SPRING_SECURITY

Proyecto del Diplomado UNAM para practicar integracion entre Spring Boot, Spring Security y Thymeleaf.

## Estado actual

- Framework: Spring Boot `3.5.10`
- Java: `17`
- Security: configurada, pero con acceso abierto (`permitAll`) para todas las rutas
- UI: vistas Thymeleaf (`/`, `/user`, `/admin`)
- API demo: endpoint REST en `/auth/welcome`

> Importante: aunque existen propiedades de usuario/password, hoy no se requiere login porque la configuracion permite todo.

---

## Requisitos

| Herramienta | Version sugerida |
|---|---|
| Java | 17+ |
| Maven | 3.9+ |
| Git | 2.44+ |

Validacion rapida:

```bash
java -version
mvn -version
git --version
```

---

## Ejecucion local

Desde la raiz del proyecto:

```bash
mvn clean install
mvn spring-boot:run
```

La aplicacion arranca en `http://localhost:8090` por defecto.

Tambien puedes usar el wrapper Maven:

```bash
./mvnw clean install
./mvnw spring-boot:run
```

---

## Endpoints y vistas

### Controlador REST

| Ruta | Metodo | Controlador | Descripcion |
|---|---|---|---|
| `/auth/welcome` | GET | `HelloWorldController` | Responde texto plano: `Hello World Spring Security!` |

### Controlador MVC (Thymeleaf)

| Ruta | Metodo | Controlador | Servicio usado | Vista |
|---|---|---|---|---|
| `/` | GET | `HomeController.home` | `HomeService` | `index.html` |
| `/index` | GET | `HomeController.index` | - | Redirecciona a `/` |
| `/user` | GET | `HomeController.user` | `UserService` | `user.html` |
| `/admin` | GET | `HomeController.admin` | `AdminService` | `admin.html` |

---

## Servicios (no documentados antes)

Los siguientes servicios estan en `src/main/java/edu/unam/springsecurity/service/` y se inyectan en `HomeController`:

| Servicio | Metodo | Retorno actual | Uso |
|---|---|---|---|
| `HomeService` | `getText()` | `"Home"` | Se pinta en `index.html` |
| `UserService` | `getText()` | `"User"` | Se pinta en `user.html` |
| `AdminService` | `getText()` | `"Admin"` | Se pinta en `admin.html` |

En esta version son servicios demo (texto fijo) para mostrar inyeccion de dependencias y separacion por capas.

---

## Seguridad

Archivo: `src/main/java/edu/unam/springsecurity/security/SecurityConfiguration.java`

Reglas activas:

- `requestMatchers("/auth/**").permitAll()`
- `requestMatchers("/**").permitAll()`
- `anyRequest().authenticated()`

Debido a `"/**".permitAll()`, hoy todas las rutas quedan publicas.

---

## Configuracion (`application.properties`)

Archivo: `src/main/resources/application.properties`

```properties
spring.application.name=${SPRING_APP_NAME:DIP_SPRING_SECURITY}
server.port=${SERVER_PORT:8090}

spring.security.user.name=${SECURITY_USERNAME:jonathan}
spring.security.user.password=${SECURITY_PASSWORD:1234}

logging.level.org.springframework.security=${SPRING_SECURITY_LOG_LEVEL:TRACE}
```

Variables de entorno soportadas:

- `SPRING_APP_NAME`
- `SERVER_PORT`
- `SECURITY_USERNAME`
- `SECURITY_PASSWORD`
- `SPRING_SECURITY_LOG_LEVEL`

Ejemplo de ejecucion cambiando puerto y credenciales:

```bash
SERVER_PORT=8091 SECURITY_USERNAME=demo SECURITY_PASSWORD=demo123 mvn spring-boot:run
```

---

## Estructura del proyecto

```text
src/main/java/edu/unam/springsecurity/
|-- SpringSecurityApplication.java
|-- controller/
|   |-- HelloWorldController.java
|   `-- HomeController.java
|-- security/
|   `-- SecurityConfiguration.java
`-- service/
    |-- AdminService.java
    |-- HomeService.java
    `-- UserService.java

src/main/resources/
|-- application.properties
|-- static/css/style.css
`-- templates/
    |-- admin.html
    |-- index.html
    |-- page-templates.html
    `-- user.html
```

---

## Pruebas y comandos utiles

```bash
mvn test
mvn clean package
java -jar target/DIP_SPRING_SECURITY-0.0.1-SNAPSHOT.jar
curl http://localhost:8090/auth/welcome
curl http://localhost:8090/user
curl http://localhost:8090/admin
```

---

## Dependencias principales (`pom.xml`)

- `spring-boot-starter`
- `spring-boot-starter-web`
- `spring-boot-starter-security`
- `spring-boot-starter-thymeleaf`
- `spring-boot-devtools` (runtime, opcional)
- `spring-boot-starter-test` (test)

---

## Proximo paso recomendado

Si el objetivo del curso es aplicar autorizacion por rol, puedes evolucionar la seguridad asi:

1. Definir usuarios/roles (`USER`, `ADMIN`).
2. Permitir publico solo en `/auth/**` y proteger `/user` y `/admin`.
3. Activar formulario de login y logout.
4. Usar `hasRole("USER")` y `hasRole("ADMIN")` en rutas.

---

## Licencia

Ver `LICENSE`.
