# DIP_SPRING_SECURITY
Diplomado UNAM - Spring Security

A simple **Spring Boot 3.5.9** app to learn Spring Security.

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

**Username:** `jonathan`  
**Password:** `1234`

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
└── application.properties               ← Port and credentials
```

### Important Files

**application.properties** - App configuration
```properties
server.port=8090
spring.security.user.name=jonathan
spring.security.user.password=1234
```

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
            .authorizeHttpRequests((authorize) -> authorize
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .formLogin(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
            .username("jonathan")
            .password("1234")
            .build();

        return new InMemoryUserDetailsManager(user);
    }
}
```
**What it does:** Protects endpoints and defines an in-memory user.

---

## How It Works

```
1. Open http://localhost:8090/auth/welcome
2. Spring Security redirects you to /login
3. You enter: jonathan / 1234
4. A session is created
5. You can access /auth/welcome
6. You see: "Hello World Spring Security!"
```

---

## Credentials

Default credentials:
- **Username:** jonathan
- **Password:** 1234

To change them, edit `application.properties`:
```properties
spring.security.user.name=your_user
spring.security.user.password=your_password
```

Then restart the app.

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
2. Login with jonathan/1234
3. See: "Hello World Spring Security!"

### Option 2: cURL
```bash
curl -u jonathan:1234 http://localhost:8090/auth/welcome
```

### Option 3: Postman
1. New GET request
2. URL: `http://localhost:8090/auth/welcome`
3. Authorization → Basic Auth → jonathan/1234
4. Send

---

## Common Issues

### "Port 8090 already in use"
```bash
mvn spring-boot:run -Dspring-boot.run.arguments='--server.port=8091'
```

### "401 Unauthorized"
- Check username: `jonathan`
- Check password: `1234`
- No extra spaces

### "Maven not found"
```bash
./mvnw clean install
./mvnw spring-boot:run
```

### "Java 17 not installed"
Install from: https://www.oracle.com/java/technologies/downloads/#java17

---

## Additional Docs

For more info:
- `GETTING_STARTED.md`
- `QUICK_REFERENCE.md`
- `INDEX.md`

---

## Next Steps

1. Read "How It Works"
2. Review the three main classes
3. Change credentials and test
4. Ask your instructor if you get stuck

---

## Key Concepts

### Authentication
Who you are (username/password).

### Authorization
What you can access (roles/permissions).

### SecurityConfiguration
Where security rules are defined.

### UserDetailsService
Where users are stored and validated.

---

## Project Configuration

### pom.xml
Defines dependencies:
- Spring Boot Starter Web
- Spring Boot Starter Security
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
- [ ] Use credentials: jonathan/1234
- [ ] See the welcome message

---

## License

See [LICENSE](LICENSE)

---

**Welcome to the Spring Security course!**

_Last updated: February 7, 2026_  
_Version: 3.0 (Simplified)_  
_Status: Ready to learn_
