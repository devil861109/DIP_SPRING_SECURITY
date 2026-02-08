# DIP_SPRING_SECURITY
Diplomado UNAM - Spring Security

A **Spring Boot 3.5.10** app to learn Spring Security with Thymeleaf templates and database-backed authentication.

**Current behavior (from `SecurityConfiguration.java`):**
- All routes are currently open because `"/**"` is permitted in the matcher list
- `/user` and `/admin` role rules are present but effectively bypassed
- Custom login page at `/login`
- Custom logout URL at `/doLogout`

## Getting Your Development Environment Setup

### Recommended Versions
| Recommended             | Reference                                                                 | Notes                                                   |
|-------------------------|---------------------------------------------------------------------------|---------------------------------------------------------|
| Oracle Java 17 JDK      | https://www.oracle.com/java/technologies/downloads/#java17                | Java 17+ required for Spring Boot 3                     |
| IntelliJ 2022 or Higher | https://www.jetbrains.com/idea/download/                                  | Ultimate recommended; Community also works             |
| Maven 3.9.0 or higher   | https://maven.apache.org/download.cgi                                     |                                                         |
| Git 2.44 or higher      | https://git-scm.com/downloads                                             |                                                         |
| MariaDB (local)         | https://mariadb.org/download/                                             | Used by JPA repositories                                |

### Verify Installation
```bash
java -version
mvn -version
git --version
```

## Quick Start

### 1) Clone
```bash
git clone <repository-url>
cd DIP_SPRING_SECURITY
```

### 2) Configure DB (optional)
You can override the default MariaDB settings via env vars:
```bash
export SPRING_DATASOURCE_URL=jdbc:mariadb://localhost:3306/springsecurity
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWD=your_password
```

### 3) Build & Run
```bash
mvn clean install
mvn spring-boot:run
```

### 4) Open the app
- Home page: `http://localhost:8090/`
- Login page: `http://localhost:8090/login`

## Project Structure
```
src/main/java/edu/unam/springsecurity/
├── SpringSecurityApplication.java
├── auth/                                ← Auth module (controllers, models, repos)
├── system/                              ← System module (controllers, services)
└── security/                            ← Security config + auth services

src/main/resources/
├── application.properties
├── static/                              ← /css, /images, /vendors, /build
└── templates/                           ← index.html, user.html, admin.html, login.html
```

## Key Endpoints

| URL | Purpose | Notes |
|-----|---------|-------|
| `/` | Home page | Open (because `"/**"` is permitted) |
| `/user` | User page | Role rule exists but currently bypassed |
| `/admin` | Admin page | Role rule exists but currently bypassed |
| `/login` | Custom login page | Form login |
| `/doLogout` | Custom logout | Invalidates session + clears JSESSIONID |
| `/auth/welcome` | REST endpoint | Plain text response |

## Authentication Details

- Users are loaded from the database using `UserDetailsServiceImpl`
- Lookup uses **email**: `UserInfoRepository.findByUseEmail(...)`
- Passwords are stored as **BCrypt** hashes
- Authentication is wired through a `DaoAuthenticationProvider` and a custom `AuthenticationManager`
- A custom `AuthenticationProviderImpl` class exists in `security/service`, but the active provider is the DAO-based one above

To enforce roles, remove `"/**".permitAll()` from `SecurityConfiguration.java`.

## Configuration Notes

`application.properties` uses env vars with defaults:
```properties
spring.application.name=${SPRING_APP_NAME:DIP_SPRING_SECURITY}
server.port=${SERVER_PORT:8090}

spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:mariadb://localhost:3306/springsecurity}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:root}
spring.datasource.password=${SPRING_DATASOURCE_PASSWD:d1p10m4d0j4v4}

spring.jpa.hibernate.ddl-auto=${SPRING_DDL_AUTO:none}
```

## Dependencies (pom.xml)
- Spring Boot Starter Web
- Spring Boot Starter Security
- Spring Boot Starter Thymeleaf
- Thymeleaf Extras (Spring Security)
- Spring Boot Starter Data JPA
- MariaDB JDBC Driver
- Lombok

## Common Issues

- **403 on /admin**: will occur only after removing `"/**".permitAll()`; login with an ADMIN user.
- **Login fails**: ensure the user exists in MariaDB and the password is BCrypt-hashed.
- **DB connection errors**: verify `spring.datasource.*` values.

## License
See [LICENSE](LICENSE).

---

_Last updated: February 8, 2026_