# DIP_SPRING_SECURITY
Diplomado UNAM - Spring Security

A **Spring Boot 3.5.10** app to learn Spring Security with authentication, role-based access control, and Thymeleaf templates.

**Current behavior (based on `SecurityConfiguration.java`):**
- All routes are currently open because `"/**"` is permitted in the matcher list
- `/user` and `/admin` are protected at method level via `@PreAuthorize`
- Custom login page still exists at `/login`
- Custom logout URL is `/doLogout`

---

## What You Need

| Tool | Version | Download |
|------|---------|----------|
| Java | 17+ | https://www.oracle.com/java/technologies/downloads/#java17 |
| Maven | 3.9.0+ | https://maven.apache.org/download.cgi |
| Git | 2.44+ | https://git-scm.com/downloads |
| MariaDB | Local instance | https://mariadb.org/download/ |
| IDE | IntelliJ or VS Code | https://www.jetbrains.com/idea/download/ |

**Verify installation:**
```bash
java -version
mvn -version
git --version
```

---

## Quick Start (5 minutes)

### 1. Clone the repository
```bash
git clone <repository-url>
cd DIP_SPRING_SECURITY
```

### 2. (Optional) Update DB settings
If you use a different database or credentials, set env vars:
```bash
export SPRING_DATASOURCE_URL=jdbc:mariadb://localhost:3306/springsecurity
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWD=your_password
```

### 3. Build
```bash
mvn clean install
```

### 4. Run
```bash
mvn spring-boot:run
```

### 5. Test
Open your browser: `http://localhost:8090/`

You should see the home page (open to everyone).

---

## Project Structure

```
src/main/java/edu/unam/springsecurity/
├── SpringSecurityApplication.java      ← Entry point
│
├── auth/                                ← Authentication module
│   ├── controller/
│   │   └── HomeController.java         ← Home, user, admin, login pages
│   ├── dto/
│   ├── exception/
│   ├── model/
│   ├── repository/
│   │   └── UserInfoRepository.java     ← User lookup by email
│   └── service/
│
├── system/                              ← System/Core module
│   ├── controller/
│   │   └── HelloWorldController.java   ← REST endpoint
│   └── service/
│       ├── HomeService.java            ← Home page data
│       ├── UserService.java            ← User page data
│       ├── AdminService.java           ← Admin page data
│       ├── UserInfoService.java        ← User information
│       └── UserInfoRoleService.java    ← User role info
│
├── security/                            ← Security module
│   ├── SecurityConfiguration.java      ← Security config (note: `"/**"` is permitted)
│   └── service/
│       ├── UserDetailsServiceImpl.java ← Loads users from DB
│       └── AuthenticationProviderImpl.java

src/main/resources/
├── application.properties               ← Port and credentials
├── static/                              ← Static assets (CSS, images, JS)
│   ├── css/
│   │   └── style.css
│   ├── build/                           ← Build output assets
│   ├── images/                          ← Image files
│   ├── vendors/                         ← Third-party libraries
│   └── favicon.ico
└── templates/                           ← Thymeleaf views
    ├── index.html                       ← Home page (open)
    ├── user.html                        ← User page (requires USER role)
    ├── admin.html                       ← Admin page (requires ADMIN role)
    ├── login.html                       ← Custom login page
    └── page-templates.html              ← Reusable fragments (navbar, footer)
```

### Important Files

**application.properties** - App configuration (uses environment variables with defaults)
```properties
spring.application.name=${SPRING_APP_NAME:DIP_SPRING_SECURITY}
server.port=${SERVER_PORT:8090}

spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:mariadb://localhost:3306/springsecurity}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:root}
spring.datasource.password=${SPRING_DATASOURCE_PASSWD:d1p10m4d0j4v4}

spring.jpa.hibernate.ddl-auto=${SPRING_DDL_AUTO:none}
```

**Note:** The commented Spring Security user properties are not used. Users are loaded from the database via `UserDetailsServiceImpl`.

**templates/** - Thymeleaf HTML views (server-side rendered by controllers)
- Uses fragments from `page-templates.html` (navbar, footer)
- Dynamic content from services via Model attributes

**static/** - Public files served directly (CSS, images, JavaScript)

---

## The Code (Essential Only)

### SpringSecurityApplication.java
```java
@SpringBootApplication
public class SpringSecurityApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityApplication.class, args);
    }
}
```
**What it does:** Starts the Spring Boot app.

---

### HomeController.java
```java
package edu.unam.springsecurity.auth.controller;

import edu.unam.springsecurity.system.service.AdminService;
import edu.unam.springsecurity.system.service.HomeService;
import edu.unam.springsecurity.system.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {
    private final HomeService homeService;
    private final UserService userService;
    private final AdminService adminService;

    public HomeController(HomeService homeService, UserService userService, AdminService adminService) {
        this.homeService = homeService;
        this.userService = userService;
        this.adminService = adminService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("text", homeService.getText());
        return "index";
    }

    @GetMapping("/user")
    public String user(Model model) {
        model.addAttribute("text", userService.getText());
        return "user";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("text", adminService.getText());
        return "admin";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login_success_handler")
    public String loginSuccessHandler() {
        System.out.println("Logging user login success...");
        return "index";
    }

    @PostMapping("/login_failure_handler")
    public String loginFailureHandler() {
        System.out.println("Login failure handler....");
        return "login";
    }
}
```
**What it does:**
- Located in `auth` module (authentication)
- Injects services from `system` module
- Renders pages with dynamic content from services
- Custom `/login` endpoint serves the login page
- `loginSuccessHandler` logs successful authentication
- `loginFailureHandler` logs failed authentication attempts

---

### SecurityConfiguration.java
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((authz) -> authz
                .requestMatchers("/css/**", "/favicon.ico", "/**", "/index", "/build/**", "/images/**", "/vendors/**").permitAll()
                .requestMatchers("/user").hasAnyRole("USER")
                .requestMatchers("/admin").hasAnyRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .successForwardUrl("/login_success_handler")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/doLogout")
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
            )
            .csrf(Customizer.withDefaults())
            .cors(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11, new SecureRandom());
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(uds);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return new ProviderManager(authenticationProvider());
    }
}
```
**What it does:**
- Permits all static assets and **all routes** due to `"/**"`
- Enables method-level checks with `@EnableMethodSecurity`
- `/user` and `/admin` are guarded by `@PreAuthorize` in `HomeController`
- Custom login page at `/login`
- Custom logout URL at `/doLogout`
- Clears `JSESSIONID` and invalidates session on logout
- Uses BCrypt password hashing and database-backed `UserDetailsServiceImpl`

#### Method Security (explained for diplomado)

`@EnableMethodSecurity` turns on annotations like `@PreAuthorize`, `@PostAuthorize`, and `@Secured`.

Current project example in `src/main/java/edu/unam/springsecurity/auth/controller/HomeController.java`:

```java
@GetMapping("/user")
@PreAuthorize("hasRole('USER')")
public String user(Model model) { ... }

@GetMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public String admin(Model model) { ... }
```

How to teach this clearly:

- `requestMatchers(...)`: protects by URL pattern.
- `@PreAuthorize(...)`: protects by method execution.
- Best practice in real systems: use both (defense in depth).
- In this project, URL rules remain open because of `"/**".permitAll()`, so method security is what effectively enforces roles for `/user` and `/admin`.

---

## How It Works

```
1. User opens http://localhost:8090/
2. Request is allowed because "/**" is permitted
3. `/user` and `/admin` execute `@PreAuthorize` checks before controller logic
4. If not authenticated, user is redirected to `/login`
5. If authenticated without role, Spring returns `403 Forbidden`
```

---

## Credentials

Users are loaded from the database (MariaDB) using email as the username:
- `UserDetailsServiceImpl` calls `UserInfoRepository.findByUseEmail(...)`
- Passwords are stored as BCrypt hashes

Even with `"/**".permitAll()`, roles are currently enforced at method level via `@PreAuthorize`.

---

## Useful Commands

```bash
mvn clean install
mvn spring-boot:run
mvn test
mvn clean package
java -jar target/DIP_SPRING_SECURITY-0.0.1-SNAPSHOT.jar
mvn spring-boot:run -Dspring-boot.run.arguments='--server.port=8091'
```

---

## Test the App

### Available Pages

| Page | URL | Access | Notes |
|------|-----|--------|-------|
| Home | `http://localhost:8090/` | Everyone | Always accessible (/** is permitAll) |
| User Page | `http://localhost:8090/user` | Authenticated USER/ADMIN | Enforced by `@PreAuthorize("hasRole('USER')")` |
| Admin Page | `http://localhost:8090/admin` | Authenticated ADMIN | Enforced by `@PreAuthorize("hasRole('ADMIN')")` |
| Login | `http://localhost:8090/login` | Everyone | Custom login form |
| REST Endpoint | `http://localhost:8090/auth/welcome` | Everyone | Returns plain text |

### Option 1: Browser (Recommended)
1. Open `http://localhost:8090/`
2. Visit `/user` and `/admin` (you will be challenged by method security)
3. Open `http://localhost:8090/login` to see the login form
4. Logout endpoint is available at `/doLogout`

### Option 2: cURL
```bash
# Public page
curl http://localhost:8090/

# Protected by method security (usually redirects to login if anonymous)
curl http://localhost:8090/user
curl http://localhost:8090/admin

# REST endpoint
curl http://localhost:8090/auth/welcome
```

---

## Project Configuration

### pom.xml
Defines dependencies:
- Spring Boot Starter Web
- Spring Boot Starter Security
- Spring Boot Starter Thymeleaf
- Thymeleaf Extras (Spring Security)
- Spring Boot Starter Data JPA
- MariaDB JDBC Driver
- Lombok

### application.properties
```properties
spring.application.name=${SPRING_APP_NAME:DIP_SPRING_SECURITY}
server.port=${SERVER_PORT:8090}

spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:mariadb://localhost:3306/springsecurity}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:root}
spring.datasource.password=${SPRING_DATASOURCE_PASSWD:d1p10m4d0j4v4}

spring.jpa.hibernate.ddl-auto=${SPRING_DDL_AUTO:none}
```

---

## Services (Data Layer)

The app includes services organized in the `system` module:

**Page Content Services:**
- **HomeService** - Returns text for home page
- **UserService** - Returns text for user page
- **AdminService** - Returns text for admin page

**User Information Services:**
- **UserInfoService** - Provides user information
- **UserInfoRoleService** - Provides user role information

Each service is injected into `HomeController` and called when rendering pages. This modular approach separates authentication logic (in `auth` module) from core business logic (in `system` module).

---

## Project Architecture

The project follows a **modular design** with clear separation of concerns:

### Auth Module (`auth/`)
Handles all authentication-related functionality:
- **Controllers** - Form rendering and request handling
- **DTOs** - Data transfer objects for auth
- **Exceptions** - Authentication-specific exceptions
- **Models** - Domain models for auth
- **Repository** - Auth data persistence
- **Services** - Auth business logic

### System Module (`system/`)
Handles core business logic and system functionality:
- **Controllers** - REST endpoints and business logic
- **Services** - Core business services (data providers)

### Security Module (`security/`)
Global security configuration:
- **SecurityConfiguration** - Spring Security filters, rules, and user management

This architecture makes the codebase:
- **Scalable** - Easy to add new features
- **Testable** - Clear dependencies and responsibilities
- **Maintainable** - Clear module boundaries
- **Reusable** - System services can be used by multiple modules

---

## Authentication Flow

1. **Request home page** → Allowed by URL matcher (`"/**".permitAll()`).
2. **Request `/user` or `/admin`** → `@PreAuthorize` runs before method execution.
3. **If anonymous** → redirected to `/login`.
4. **If authenticated without role** → `403 Forbidden`.
5. **If role matches** → page renders.

---

## Role-Based Access Control (RBAC)

This project demonstrates RBAC in two layers:

- URL layer (`requestMatchers`) in `SecurityConfiguration`
- Method layer (`@PreAuthorize`) in `HomeController`

For production-style hardening, remove `"/**".permitAll()` and keep only explicit public routes.

---

## Common Issues

### "Login fails even with correct email/password"
- Ensure the user exists in the MariaDB database
- Ensure the password is BCrypt-hashed
- Check `spring.datasource.*` settings

### "403 Forbidden when accessing /admin"
- This can happen if you are authenticated but missing `ADMIN` role
- Login with an account that has `ADMIN`

### "401 Unauthorized / Redirected to login"
- Expected when Method Security blocks anonymous access to protected methods
- Login at `http://localhost:8090/login`

### "Maven not found"
```bash
./mvnw clean install
./mvnw spring-boot:run
```

### "Java 17 not installed"
Install from: https://www.oracle.com/java/technologies/downloads/#java17

---

## Need Help?

1. Check "Common Issues"
2. Review "How It Works"
3. Ask your instructor in Moodle
4. Stack Overflow: https://stackoverflow.com/questions/tagged/spring-security

---

## Checklist: First Steps

- [ ] Install Java 17+
- [ ] Install Maven
- [ ] Clone the repository
- [ ] Run `mvn clean install`
- [ ] Run `mvn spring-boot:run`
- [ ] Open http://localhost:8090/ (home)
- [ ] Open http://localhost:8090/user and /admin (verify Method Security behavior)
- [ ] Open http://localhost:8090/login (login page)
- [ ] Optional: remove `"/**".permitAll()` to harden URL-level security

---

## License

See [LICENSE](LICENSE)

---

**Welcome to the Spring Security course!**

_Last updated: February 8, 2026_  
_Version: 3.4 (DB-backed Auth + Modular Structure)_  
_Status: Ready to learn_