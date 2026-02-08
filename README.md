# DIP_SPRING_SECURITY
Diplomado UNAM - Spring Security

A simple **Spring Boot 3.5.10** app to learn Spring Security with Thymeleaf templates.

**Current behavior:** all endpoints are open (no login required yet). Home page displays dynamic content via services.

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

You should see the home page with content from the HomeService.

---

## Project Structure

```
src/main/java/edu/unam/springsecurity/
├── SpringSecurityApplication.java      ← Entry point
├── controller/
│   ├── HomeController.java             ← Home, user, admin pages
│   └── HelloWorldController.java       ← REST endpoint
├── security/
│   └── SecurityConfiguration.java      ← Security config
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
    ├── index.html                       ← Home page
    ├── user.html                        ← User page
    ├── admin.html                       ← Admin page
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
}
```
**What it does:** Renders Thymeleaf templates with dynamic content from services.

---

### HelloWorldController.java
```java
@RestController
@RequestMapping("/auth")
public class HelloWorldController {
    @GetMapping("/welcome")
    public String welcome() {
        return "Hello World Spring Security!";
    }
}
```
**What it does:** REST endpoint that returns plain text.

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
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/**").permitAll()
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
```
**What it does:** Allows all requests (no login yet).

---

## How It Works

```
1. Open http://localhost:8090/
2. HomeController receives the request
3. Calls homeService.getText() to get content
4. Thymeleaf renders index.html with navbar, footer, and content
5. You see the home page with styled layout
```

---

## Credentials

Right now, no login is required because all endpoints are `permitAll()`.

If you later enable authentication, these properties can be used:
```properties
spring.security.user.name=jonathan
spring.security.user.password=1234
```

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
1. **Home** - `http://localhost:8090/` (or `http://localhost:8090/index`)
2. **User Page** - `http://localhost:8090/user`
3. **Admin Page** - `http://localhost:8090/admin`
4. **REST Endpoint** - `http://localhost:8090/auth/welcome`

### Option 1: Browser (Recommended)
1. Open `http://localhost:8090/`
2. Navigate between Home, User, and Admin pages
3. See dynamic content from services rendered by Thymeleaf

### Option 2: cURL
```bash
# REST endpoint
curl http://localhost:8090/auth/welcome

# HTML pages (will return full HTML)
curl http://localhost:8090/
curl http://localhost:8090/user
curl http://localhost:8090/admin
```

### Option 3: Static assets
If the app is running, these files are available:
- `http://localhost:8090/css/style.css`

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

## Contributing

If you find issues or want improvements:
1. Open an issue
2. Create a pull request
3. Contact your instructor

---

## Common Issues

### "Port 8090 already in use"
```bash
mvn spring-boot:run -Dspring-boot.run.arguments='--server.port=8091'
```

### "Maven not found"
```bash
./mvnw clean install
./mvnw spring-boot:run
```

### "Java 17 not installed"
Install from: https://www.oracle.com/java/technologies/downloads/#java17

### "404 Not Found"
- Check the URL (try `http://localhost:8090/` instead of just localhost)
- Make sure the app is running
- Check controller mappings in HomeController

---

## Need Help?

1. Check "Common Issues"
2. See `QUICK_REFERENCE.md`
3. Ask your instructor in Moodle
4. Stack Overflow: https://stackoverflow.com/questions/tagged/spring-security

---

## Checklist: First Steps

- [ ] Install Java 17+
- [ ] Install Maven
- [ ] Clone the repository
- [ ] Run `mvn clean install`
- [ ] Run `mvn spring-boot:run`
- [ ] Open http://localhost:8090/
- [ ] Navigate to user and admin pages
- [ ] See the styled pages with content

---

## License

See [LICENSE](LICENSE)

---

**Welcome to the Spring Security course!**

_Last updated: February 8, 2026_  
_Version: 3.1 (With Thymeleaf)_  
_Status: Ready to learn_