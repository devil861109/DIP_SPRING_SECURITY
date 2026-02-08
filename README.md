# DIP_SPRING_SECURITY
Diplomado UNAM - Spring Security

A **Spring Boot 3.5.10** app to learn Spring Security with **JWT filter-based authentication**, role-based access control, and Thymeleaf templates.

**Current behavior (based on `SecurityConfiguration.java`):**
- **Stateless JWT authentication via filters** - tokens extracted from requests
- **Custom JWT login filter** (`JWTUsernameAndPasswordAuthenticationFilter`) handles credentials
- **JWT validation filter** validates tokens on every request
- No form login - all authentication through JWT filters
- `/user` requires USER role
- `/admin` requires ADMIN role  
- `/api/**` routes are publicly accessible (no authentication required)
- `/v1/**` routes require authentication
- Session policy: STATELESS (no server-side session storage)

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
```bash
# Login via JWT filter
curl -X POST http://localhost:8090/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user@example.com","password":"password"}'
```

---

## Project Structure

```
src/main/java/edu/unam/springsecurity/
├── SpringSecurityApplication.java      ← Entry point
│
├── auth/                                ← Authentication module
│   ├── controller/
│   ├── dto/
│   ├── exception/
│   ├── model/
│   ├── repository/
│   └── service/
│
├── system/                              ← System/Core module
│   ├── controller/
│   │   ├── CatContactTypeController.java
│   │   └── HelloWorldController.java
│   └── service/
│
└── security/                            ← Security module
    ├── SecurityConfiguration.java      ← Security config with JWT filters
    ├── controller/
    │   └── AuthController.java         ← REST auth endpoints
    ├── jwt/
    │   ├── JWTTokenProvider.java       ← Token generation/validation
    │   ├── JWTAuthenticationFilter.java ← Token validation filter
    │   └── JWTUsernameAndPasswordAuthenticationFilter.java ← Login filter
    ├── logout/
    ├── service/
    │   └── UserDetailsServiceImpl.java  ← Loads users from DB
    ├── model/
    ├── dto/
    └── request/

src/main/resources/
├── application.yml                      ← Configuration (YAML format)
├── static/                              ← Static assets
└── templates/                           ← Thymeleaf views
```

---

## The Code (Essential Only)

### SecurityConfiguration.java
```java
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Autowired
    private UserDetailsServiceImpl uds;
    @Autowired
    private JWTTokenProvider tokenProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, 
                                                   AuthenticationManager authenticationManager) 
            throws Exception {
        JWTAuthenticationFilter jwtFilter = new JWTAuthenticationFilter(tokenProvider, uds);
        http
            .authorizeHttpRequests((authz) -> authz
                .requestMatchers("/user").hasAnyRole("USER")
                .requestMatchers("/admin").hasAnyRole("ADMIN")
                .requestMatchers("/api/**").permitAll()
                .requestMatchers("/v1/**").authenticated()
                .anyRequest().authenticated()
            )
            .addFilter(new JWTUsernameAndPasswordAuthenticationFilter(authenticationManager, tokenProvider))
            .addFilterAfter(jwtFilter, JWTUsernameAndPasswordAuthenticationFilter.class)
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11, new SecureRandom());
    }

    // ...existing code...
}
```
**What it does:**
- No form login - uses JWT filters instead
- `JWTUsernameAndPasswordAuthenticationFilter` intercepts login requests
- `JWTAuthenticationFilter` validates tokens on all requests
- `/user` and `/admin` are role-protected
- `/api/**` is publicly accessible (no authentication)
- `/v1/**` requires authentication (any valid token)
- Session policy is STATELESS

---

## Authentication Flow (NEW - Filter-Based)

```
1. CLIENT SUBMITS CREDENTIALS
   POST /login
   Body: {"username":"user@example.com","password":"password"}
   ↓
2. JWTUsernameAndPasswordAuthenticationFilter INTERCEPTS
   ├─ Extracts credentials from request body
   ├─ Calls authenticationManager.authenticate()
   ├─ Validates against UserDetailsServiceImpl
   ├─ On success: generates JWT token
   └─ Returns token in response
   ↓
3. CLIENT STORES TOKEN
   └─ Store token locally for future requests
   ↓
4. CLIENT MAKES PROTECTED REQUEST
   GET /user
   Header: Authorization: Bearer {token}
   ↓
5. JWTAuthenticationFilter INTERCEPTS
   ├─ Extracts token from Authorization header
   ├─ Validates token signature and expiry
   ├─ Creates authentication from claims
   ├─ Sets SecurityContext
   └─ Request proceeds if valid (401 if invalid)
   ↓
6. RESPONSE RETURNED
   ← 200 OK with data (if authorized)
   ← 401 Unauthorized (if token missing/invalid)
   ← 403 Forbidden (if insufficient permissions)
```

---

## Key Endpoints

| URL | Method | Purpose | Authentication | Notes |
|-----|--------|---------|-----------------|-------|
| `/login` | POST | JWT login | None | Submit credentials, get token |
| `/user` | GET | User page | USER role | Role-protected |
| `/admin` | GET | Admin page | ADMIN role | Admin-only |
| `/api/**` | ANY | Public API | None | Open to everyone |
| `/v1/**` | ANY | Versioned API | Required | Any valid token |

---

## How to Authenticate

### 1. Login and Get Token
```bash
curl -X POST http://localhost:8090/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user@example.com","password":"password"}'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

### 2. Use Token for Protected Requests
```bash
curl http://localhost:8090/user \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

### 3. Access Public API
```bash
curl http://localhost:8090/api/public-endpoint
```

---

## Credentials

Users are loaded from MariaDB database via `UserDetailsServiceImpl`:
- **Username**: Email (loaded from `UserInfo.useEmail`)
- **Password**: BCrypt-hashed (loaded from `UserInfo.usePasswd`)
- **Roles**: Loaded from related `UserInfoRole` entities

---

## Application Configuration

**application.yml:**
```yaml
jwt:
  secret: ${SPRING_JWT_SECRET:your-secret-key}
  expirationDateInMs: ${SPRING_JWT_EXP_DATE_MS:3600}           # 1 hour (seconds)
  refreshExpirationDateInMs: ${SPRING_JWT_REFRESH_EXP_DATE_MS:900}
```

**Set environment variables:**
```bash
export SPRING_JWT_SECRET=your-long-secret-key
export SPRING_JWT_EXP_DATE_MS=3600
export SPRING_DATASOURCE_URL=jdbc:mariadb://localhost:3306/springsecurity
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWD=your_password
```

---

## Security Features

✅ **No Session Storage** - STATELESS policy  
✅ **JWT Filter-Based Auth** - Not form login  
✅ **Custom Login Filter** - Intercepts /login requests  
✅ **Token Validation Filter** - Validates on every request  
✅ **BCrypt Password Hashing** - Secure password storage  
✅ **Role-Based Access Control** - USER, ADMIN roles  
✅ **CORS Enabled** - Cross-origin requests allowed  
✅ **CSRF Disabled** - Suitable for stateless JWT  

---

## Common Issues

### "401 Unauthorized - Invalid token"
- Ensure token is valid and not expired
- Check Authorization header format: `Bearer {token}`
- Verify `jwt.secret` is configured correctly
- Token must be from `/login` endpoint

### "Token not found in Authorization header"
- Include header in request: `Authorization: Bearer {token}`
- Some clients may need: `Authorization: {token}` (without "Bearer")

### "Cannot access /user without token"
- Login first to get a token from `/login`
- Include token in Authorization header of request

### "403 Forbidden - Insufficient permissions"
- Your token's role doesn't match endpoint requirements
- Login with a user that has the required role (USER for `/user`, ADMIN for `/admin`)

### "Login returns 400 Bad Request"
- Check JSON format is correct: `{"username":"email","password":"pass"}`
- Ensure credentials field names match filter expectations

### "Login returns 401 Unauthorized"
- Check credentials are correct
- Ensure user exists in MariaDB database
- Verify password is BCrypt-hashed in database

### "Token expired immediately"
- Check `jwt.expirationDateInMs` is in seconds, not milliseconds
- Default is 3600 (1 hour)

---

## License

See [LICENSE](LICENSE)

---

## Useful Commands

```bash
mvn clean install
mvn spring-boot:run
mvn test
mvn clean package
java -jar target/DIP_SPRING_SECURITY-0.0.1-SNAPSHOT.jar
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
- JJWT (JSON Web Token library)
- Lombok

---

## Services (Data Layer)

The app includes services organized in the `system` module:

**Key Services:**
- **UserDetailsServiceImpl** - Loads users by email from database
- **HelloWorldController** - REST endpoint
- **CatContactTypeController** - Cat contact type management

---

## Common Issues

### "401 Unauthorized - Invalid token"
- Ensure token is valid and not expired
- Check Authorization header format: `Bearer {token}`
- Verify `jwt.secret` is configured correctly
- Token must be from `/login` endpoint

### "Token not found in Authorization header"
- Include header in request: `Authorization: Bearer {token}`
- Some clients may need: `Authorization: {token}` (without "Bearer")

### "Cannot access /user without token"
- Login first to get a token from `/login`
- Include token in Authorization header of request

### "403 Forbidden - Insufficient permissions"
- Your token's role doesn't match endpoint requirements
- Login with a user that has the required role (USER for `/user`, ADMIN for `/admin`)

### "Login returns 400 Bad Request"
- Check JSON format is correct: `{"username":"email","password":"pass"}`
- Ensure credentials field names match filter expectations

### "Login returns 401 Unauthorized"
- Check credentials are correct
- Ensure user exists in MariaDB database
- Verify password is BCrypt-hashed in database

### "Token expired immediately"
- Check `jwt.expirationDateInMs` is in seconds, not milliseconds
- Default is 3600 (1 hour)

---

## License

See [LICENSE](LICENSE)

---

**Welcome to the Spring Security course!**

_Last updated: February 8, 2026_  
_Version: 3.6 (JWT Filter-Based Authentication - STATELESS)_  
_Status: Ready to learn_
