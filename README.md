# DIP_SPRING_SECURITY
Diplomado UNAM - Spring Security

A simple **Spring Boot 3.5.10** app to learn Spring Security.

**Current behavior:** all endpoints are open (no login required yet).

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
Open your browser: `http://localhost:8090/auth/welcome`

You should see: `Hello World Spring Security!`

---

## Project Structure

```
src/main/java/edu/unam/springsecurity/
├── SpringSecurityApplication.java      ← Entry point
├── controller/
│   └── HelloWorldController.java       ← Endpoints
└── security/
    └── SecurityConfiguration.java      ← Security config

src/main/resources/
├── application.properties               ← Port and credentials
├── static/                              ← Static assets (CSS, images, JS)
│   └── css/
│       └── style.css
└── templates/                           ← Thymeleaf views
    ├── admin.html
    ├── index.html
    ├── page-templates.html
    └── user.html
```

### Important Files

**application.properties** - App configuration
```properties
server.port=8090
spring.security.user.name=jonathan
spring.security.user.password=1234
```

**templates/** - Thymeleaf HTML views (rendered by controllers)

**static/** - Public files served directly (e.g., `/css/style.css`)

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
**What it does:** Defines `/auth/welcome`.

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
1. Open http://localhost:8090/auth/welcome
2. Spring Security allows the request (permitAll)
3. You see: "Hello World Spring Security!"
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

### Option 1: Browser
1. Open `http://localhost:8090/auth/welcome`
2. See: "Hello World Spring Security!"

### Option 2: cURL
```bash
curl http://localhost:8090/auth/welcome
```

### Option 3: Static assets
If the app is running, this file should be available:
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

## Contributing

If you find issues or want improvements:
1. Open an issue
2. Create a pull request
3. Contact your instructor

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
- [ ] Open http://localhost:8090/auth/welcome
- [ ] See the welcome message

---

## License

See [LICENSE](LICENSE)

---

**Welcome to the Spring Security course!**

_Last updated: February 7, 2026_  
_Version: 3.0 (Simplified)_  
_Status: Ready to learn_