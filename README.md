# DIP_SPRING_SECURITY
Diplomado UNAM - Spring Security

A **Spring Boot 3.5.10** app to learn Spring Security with authentication, role-based access control, and Thymeleaf templates.

**Current behavior:**
- Home page (`/`) is open to everyone
- User page (`/user`) requires USER role
- Admin page (`/admin`) requires ADMIN role
- Login required to access protected pages
- Custom login form with form-based authentication

---

## What You Need

| Tool | Version | Download |
|------|---------|----------|
| Java | 17+ | https://www.oracle.com/java/technologies/downloads/#java17 |
| Maven | 3.9.0+ | https://maven.apache.org/download.cgi |
| Git | 2.44+ | https://git-scm.com/downloads |
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

### 2. Build
```bash
mvn clean install
```

### 3. Run
```bash
mvn spring-boot:run
```

### 4. Test
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
└── security/
    └── SecurityConfiguration.java      ← Security config with roles

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

**application.properties** - App configuration
```properties
server.port=8090
spring.security.user.name=jonathan
spring.security.user.password=1234
```

**Note:** These properties are not used by the application. The actual users are defined in `SecurityConfiguration.java` with `{noop}` password encoding (see credentials table below).

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
                .invalidateHttpSession(true)
            );

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    UserDetailsManager inMemoryUserDetailsManager() {
        var user1 = User.withUsername("user")
            .password("{noop}password")
            .roles("USER")
            .build();
            
        var user2 = User.withUsername("admin")
            .password("{noop}password")
            .roles("USER", "ADMIN")
            .build();
            
        return new InMemoryUserDetailsManager(user1, user2);
    }
}
```
**What it does:**
- Allows home page (`/`) for everyone
- Permits static assets: CSS, images, build files, vendor libraries, favicon
- Requires USER role for `/user`
- Requires ADMIN role for `/admin`
- Custom login page at `/login`
- Custom logout URL at `/doLogout` (instead of default `/logout`)
- Login success handler logs the event
- Session is invalidated on logout
- Two in-memory users: `user` (USER) and `admin` (USER, ADMIN)

---

## How It Works

```
1. User opens http://localhost:8090/
2. Home page displays (no login required)
3. User clicks on /user or /admin
4. Spring Security redirects to /login (not authenticated yet)
5. User enters credentials:
   - user / password (USER role)
   - admin / password (USER and ADMIN roles)
6. Spring Security validates and creates session
7. loginSuccessHandler logs the authentication
8. User can now access pages matching their role
9. User clicks logout (/doLogout)
10. Session is invalidated
11. User is redirected to home page
```

---

## Credentials

Two default users are configured with **no-op password encoding** (plain text):

| Username | Password | Role |
|----------|----------|------|
| user | password | USER |
| admin | password | USER, ADMIN |

- **user** can access: `/`, `/user`
- **admin** can access: `/`, `/user`, `/admin`

To add or change users, edit `SecurityConfiguration.java` in the `inMemoryUserDetailsManager()` method.

**Note:** The `{noop}` prefix indicates plain text passwords. In production, use `BCryptPasswordEncoder` instead.

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
| Home | `http://localhost:8090/` | Everyone | Always accessible |
| User Page | `http://localhost:8090/user` | USER role | Requires login as jonathan or admin |
| Admin Page | `http://localhost:8090/admin` | ADMIN role | Requires login as admin |
| Login | `http://localhost:8090/login` | Everyone | Custom login form |
| REST Endpoint | `http://localhost:8090/auth/welcome` | Everyone | Returns plain text |

### Option 1: Browser (Recommended)
1. Open `http://localhost:8090/`
2. Click on User or Admin page
3. You'll be redirected to login
4. Try different credentials:
    - **user / password** (can access User page)
    - **admin / password** (can access Admin and User pages)
5. See what each user can access
6. Click logout to end session (redirects to /doLogout)

### Option 2: cURL (with session cookies)
```bash
# Login as user
curl -c cookies.txt -X POST \
  -d "username=user&password=password" \
  http://localhost:8090/login

# Access user page with session
curl -b cookies.txt http://localhost:8090/user

# Access admin page (will fail - not ADMIN)
curl -b cookies.txt http://localhost:8090/admin

# Logout (custom logout URL)
curl -X POST http://localhost:8090/doLogout
```

### Option 3: REST Endpoint
```bash
# No authentication required for /auth/welcome
curl http://localhost:8090/auth/welcome
```

---

## Project Configuration

### pom.xml
Defines dependencies:
- Spring Boot Starter Web
- Spring Boot Starter Security
- Spring Boot Starter Thymeleaf
- Spring Boot DevTools

### application.properties
```properties
server.port=8090
spring.security.user.name=jonathan
spring.security.user.password=1234
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

1. **Request protected page** → User requests `/user` or `/admin`
2. **No authentication** → Spring Security redirects to `/login`
3. **Login form** → User enters credentials
4. **Validation** → `UserDetailsManager` checks username/password
5. **Authentication success** → Session created, user authenticated
6. **Role check** → Spring Security checks if user has required role
7. **Access granted/denied** → User either sees the page or gets 403 Forbidden
8. **Logout** → Session destroyed, user must login again

---

## Role-Based Access Control (RBAC)

The `@RequestMatchers` in SecurityConfiguration define:
- `/` → `permitAll()` (anyone can access)
- `/user` → `hasAnyRole("USER")` (USER or ADMIN)
- `/admin` → `hasAnyRole("ADMIN")` (ADMIN only)

If a USER tries to access `/admin`, they get a **403 Forbidden** error.

---

## Common Issues

### "Port 8090 already in use"
```bash
mvn spring-boot:run -Dspring-boot.run.arguments='--server.port=8091'
```

### "403 Forbidden when accessing /admin"
- You don't have the ADMIN role
- Login as **admin / password** instead

### "401 Unauthorized / Redirected to login"
- You need to be authenticated to access `/user` or `/admin`
- Visit `http://localhost:8090/login` and enter credentials

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
- [ ] Open http://localhost:8090/ (home - no login needed)
- [ ] Try to access http://localhost:8090/user
- [ ] Login with user / password
- [ ] Try to access http://localhost:8090/admin (should fail)
- [ ] Logout using /doLogout
- [ ] Login as admin / password
- [ ] Access all pages successfully

---

## License

See [LICENSE](LICENSE)

---

**Welcome to the Spring Security course!**

_Last updated: February 8, 2026_  
_Version: 3.2 (With Role-Based Access Control)_  
_Status: Ready to learn_