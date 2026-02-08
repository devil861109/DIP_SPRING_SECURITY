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
├── controller/
│   ├── HomeController.java             ← Home, user, admin, login pages
│   └── HelloWorldController.java       ← REST endpoint
├── security/
│   └── SecurityConfiguration.java      ← Security config with roles
└── service/
    ├── HomeService.java                ← Home page data
    ├── UserService.java                ← User page data
    └── AdminService.java               ← Admin page data

src/main/resources/
├── application.properties               ← Port and credentials
├── static/                              ← Static assets (CSS, images, JS)
│   └── css/
│       └── style.css
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
}
```
**What it does:** Renders pages with dynamic content from services. Custom `/login` endpoint serves the login page.

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
                .requestMatchers("/css/**", "/favicon.ico", "/**", "/index").permitAll()
                .requestMatchers("/user").hasAnyRole("USER")
                .requestMatchers("/admin").hasAnyRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .permitAll()
            )
            .logout(logout -> logout
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public UserDetailsManager userDetailsManager() {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        
        UserDetails user = User.builder()
            .username("jonathan")
            .password(encoder.encode("1234"))
            .roles("USER")
            .build();

        UserDetails admin = User.builder()
            .username("admin")
            .password(encoder.encode("admin123"))
            .roles("ADMIN")
            .build();

        return new InMemoryUserDetailsManager(user, admin);
    }
}
```
**What it does:** 
- Allows home page (`/`) for everyone
- Requires USER role for `/user`
- Requires ADMIN role for `/admin`
- Custom login page at `/login`
- Two in-memory users: `jonathan` (USER) and `admin` (ADMIN)

---

## How It Works

```
1. User opens http://localhost:8090/
2. Home page displays (no login required)
3. User clicks on /user or /admin
4. Spring Security redirects to /login (not authenticated yet)
5. User enters credentials:
   - jonathan / 1234 (USER role)
   - admin / admin123 (ADMIN role)
6. Spring Security validates and creates session
7. User can now access pages matching their role
8. User logs out and session ends
```

---

## Credentials

Two default users are configured:

| Username | Password | Role |
|----------|----------|------|
| jonathan | 1234 | USER |
| admin | admin123 | ADMIN |

- **jonathan** can access: `/`, `/user`
- **admin** can access: `/`, `/user`, `/admin`

To add or change users, edit `SecurityConfiguration.java` in the `userDetailsManager()` method.

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
   - **jonathan / 1234** (can access User page)
   - **admin / admin123** (can access Admin page)
5. See what each user can access
6. Click logout to end session

### Option 2: cURL (with session cookies)
```bash
# Login as jonathan
curl -c cookies.txt -X POST \
  -d "username=jonathan&password=1234" \
  http://localhost:8090/login

# Access user page with session
curl -b cookies.txt http://localhost:8090/user

# Access admin page (will fail - not ADMIN)
curl -b cookies.txt http://localhost:8090/admin
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

The app includes three services that provide content to the pages:

- **HomeService** - Returns text for home page
- **UserService** - Returns text for user page
- **AdminService** - Returns text for admin page

Each service is injected into HomeController and called when rendering pages.

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
- Login as **admin / admin123** instead

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
- [ ] Login with jonathan / 1234
- [ ] Try to access http://localhost:8090/admin (should fail)
- [ ] Logout and login as admin / admin123
- [ ] Access all pages successfully

---

## License

See [LICENSE](LICENSE)

---

**Welcome to the Spring Security course!**

_Last updated: February 8, 2026_  
_Version: 3.2 (With Role-Based Access Control)_  
_Status: Ready to learn_