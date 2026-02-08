# DIP_SPRING_SECURITY
Diplomado UNAM - Spring Security

A **Spring Boot 3.5.10** app demonstrating **OAuth2 Resource Server with Keycloak**. Students learn how to build secure REST APIs protected by OAuth2 JWT tokens with role-based access control.

**Current implementation:**
- ✅ OAuth2 Resource Server (validates JWT tokens)
- ✅ Keycloak as Identity Provider (IdP)
- ✅ JWT token validation with JWK Set
- ✅ Custom role conversion from Keycloak realm roles
- ✅ Role-based access control (USER, ADMIN)
- ✅ Stateless authentication (no server-side sessions)
- ✅ Protected REST API endpoints
- ✅ MariaDB integration for data persistence

---

## Getting Your Development Environment Setup

### Recommended Versions

| Tool | Version | Download | Notes |
|------|---------|----------|-------|
| Oracle Java 17 JDK | 17+ | https://www.oracle.com/java/technologies/downloads/#java17 | Java 17+ required for Spring Boot 3 |
| IntelliJ IDEA | 2022+ | https://www.jetbrains.com/idea/download/ | Community Edition works fine |
| Maven | 3.9.0+ | https://maven.apache.org/download.cgi | Build tool |
| Git | 2.44+ | https://git-scm.com/downloads | Version control |
| MariaDB | 10.6+ | https://mariadb.org/download/ | Database server |
| Keycloak | 23+ | https://www.keycloak.org/downloads | Identity & Access Management |
| Postman/Insomnia | Latest | https://www.postman.com/ | API testing |

**Verify installation:**
```bash
java -version
mvn -version
git --version
mysql --version  # MariaDB
```

---

## Quick Start (15 minutes)

### 1. Clone the repository
```bash
git clone <repository-url>
cd DIP_SPRING_SECURITY
```

### 2. Start Keycloak Server

Download and run Keycloak:
```bash
# Download Keycloak
wget https://github.com/keycloak/keycloak/releases/download/23.0.0/keycloak-23.0.0.zip
unzip keycloak-23.0.0.zip
cd keycloak-23.0.0

# Start Keycloak (creates admin user)
bin/kc.sh start-dev
```

Keycloak will run on `http://localhost:8080`

### 3. Configure Keycloak

1. **Access Keycloak Admin Console**
   - URL: http://localhost:8080
   - Create admin user if prompted (e.g., admin/admin)

2. **Create a Realm**
   - Click "Create Realm"
   - Name: `SpringBootAppTest`
   - Click "Create"

3. **Create a Client**
   - Go to Clients → Create Client
   - Client ID: `spring-boot-app`
   - Client Protocol: `openid-connect`
   - Root URL: `http://localhost:8090`
   - Click "Save"
   - Set "Access Type": `confidential`
   - Enable "Service Accounts Enabled"
   - Save and note the client secret from Credentials tab

4. **Create Roles**
   - Go to Realm Roles → Create Role
   - Create roles: `USER`, `ADMIN`

5. **Create Users**
   - Go to Users → Add User
   - Username: `testuser`
   - Email: `test@test.com`
   - Click "Create"
   - Go to Credentials tab → Set password (e.g., "password")
   - Disable "Temporary"
   - Go to Role Mappings → Assign `USER` role

6. **Get JWK Set URI**
   - Your JWK Set URI will be:
   ```
   http://localhost:8080/realms/SpringBootAppTest/protocol/openid-connect/certs
   ```

### 4. Configure Application

Update `src/main/resources/application.yml`:
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          jwk-set-uri: http://localhost:8080/realms/SpringBootAppTest/protocol/openid-connect/certs
```

Or use environment variable:
```bash
export SPRING_SECURITY_OAUTH_URI=http://localhost:8080/realms/SpringBootAppTest/protocol/openid-connect/certs
```

### 5. Setup Database (MariaDB)

```bash
# Create database
mysql -u root -p
CREATE DATABASE springsecurity;
USE springsecurity;
SOURCE src/main/resources/agenda.sql;
EXIT;
```

### 6. Build & Run
```bash
mvn clean install
mvn spring-boot:run
```

### 7. Get Access Token from Keycloak

```bash
curl -X POST http://localhost:8080/realms/SpringBootAppTest/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=testuser" \
  -d "password=password" \
  -d "grant_type=password" \
  -d "client_id=spring-boot-app" \
  -d "client_secret=YOUR_CLIENT_SECRET"
```

Copy the `access_token` from the response.

### 8. Test the API

```bash
curl http://localhost:8090/v1/contact-type/get-contact-types \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

---

## Project Structure

```
DIP_SPRING_SECURITY/
├── src/
│   ├── main/
│   │   ├── java/edu/unam/springsecurity/
│   │   │   ├── SpringSecurityApplication.java           ← Entry point
│   │   │   ├── security/
│   │   │   │   ├── SecurityConfiguration.java          ← OAuth2 Resource Server config
│   │   │   │   └── KeyCloakRoleConverter.java          ← Keycloak role mapper
│   │   │   └── system/
│   │   │       ├── controller/
│   │   │       │   ├── CatContactTypeController.java   ← REST API endpoint
│   │   │       │   └── HelloWorldController.java
│   │   │       ├── dto/
│   │   │       ├── model/
│   │   │       ├── repository/
│   │   │       └── service/
│   │   └── resources/
│   │       ├── application.yml                          ← Config (Keycloak JWK URI)
│   │       └── agenda.sql                               ← Database schema
│   └── test/
├── pom.xml                                               ← Maven dependencies
├── README.md                                             ← This file
└── HELP.md
```

---

## The Code

### SecurityConfiguration.java
```java
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeyCloakRoleConverter());
        http
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/v1/contact-type/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2ResourceServerCustomizer ->
                        oauth2ResourceServerCustomizer.jwt(jwtCustomizer -> jwtCustomizer
                                .jwtAuthenticationConverter(jwtAuthenticationConverter)))
        ;
        return http.build();
    }
}
```

**What it does:**
- `oauth2ResourceServer()` - Configures app as OAuth2 Resource Server
- `jwt()` - Validates incoming JWT tokens against Keycloak JWK Set
- `jwtAuthenticationConverter` - Converts Keycloak roles to Spring Security authorities
- `/v1/contact-type/**` - Requires USER or ADMIN role
- `SessionCreationPolicy.STATELESS` - No server-side session storage

### KeyCloakRoleConverter.java
```java
public class KeyCloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");
        if (realmAccess == null || realmAccess.isEmpty()) {
            return new ArrayList<>();
        }
        Collection<GrantedAuthority> returnValue = ((List<String>) realmAccess.get("roles"))
                .stream().map(roleName -> "ROLE_" + roleName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return returnValue;
    }
}
```

**What it does:**
- Extracts `realm_access.roles` from Keycloak JWT token
- Converts to Spring Security `GrantedAuthority` format
- Adds "ROLE_" prefix (e.g., "USER" becomes "ROLE_USER")
- Returns collection of authorities for access control

### CatContactTypeController.java
```java
@RestController
@AllArgsConstructor
@RequestMapping("v1/contact-type/")
public class CatContactTypeController {
    private final CatContactTypeService catContactTypeService;

    @GetMapping("/get-contact-types")
    public ResponseEntity<?> getContactTypes(HttpServletRequest request) {
        List<CatContactTypeDTO> list = catContactTypeService.findAll();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
```

**What it does:**
- Protected REST endpoint at `/v1/contact-type/get-contact-types`
- Requires valid JWT token with USER or ADMIN role
- Returns list of contact types from database
- No session management - fully stateless

---

## How OAuth2 Resource Server Works

```
1. USER GETS TOKEN FROM KEYCLOAK
   POST http://localhost:8080/realms/SpringBootAppTest/protocol/openid-connect/token
   Body: username, password, grant_type, client_id, client_secret
   ← Receives: access_token (JWT)
   ↓
2. CLIENT MAKES API REQUEST
   GET http://localhost:8090/v1/contact-type/get-contact-types
   Header: Authorization: Bearer {access_token}
   ↓
3. SECURITY FILTER INTERCEPTS REQUEST
   → Extracts JWT from Authorization header
   → Validates token signature using Keycloak JWK Set
   ↓
4. JWT VALIDATION
   → Downloads public keys from JWK Set URI
   → Verifies JWT signature (RS256 algorithm)
   → Checks token expiration
   → Validates issuer and audience
   ↓
5. ROLE CONVERSION
   → KeyCloakRoleConverter extracts realm_access.roles
   → Converts: ["USER"] → ["ROLE_USER"]
   → Creates GrantedAuthority collection
   ↓
6. AUTHORIZATION CHECK
   → Checks if user has required role (USER or ADMIN)
   → /v1/contact-type/** requires USER or ADMIN
   ↓
7. RESPONSE
   ← 200 OK with data (if authorized)
   ← 401 Unauthorized (if token invalid/expired)
   ← 403 Forbidden (if insufficient permissions)
```

**Key Differences from OAuth2 Login:**
- ✅ **Resource Server** - Validates tokens, doesn't issue them
- ✅ **Stateless** - No session cookies or server-side storage
- ✅ **JWT-based** - All authentication info in token
- ✅ **Keycloak** - External IdP manages users and tokens

---

## Configuration

### application.yml
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          jwk-set-uri: http://localhost:8080/realms/SpringBootAppTest/protocol/openid-connect/certs
  datasource:
    url: jdbc:mariadb://localhost:3306/springsecurity
    username: root
    password: your_password
```

### Environment Variables (Alternative)
```bash
export SPRING_SECURITY_OAUTH_URI=http://localhost:8080/realms/SpringBootAppTest/protocol/openid-connect/certs
export SPRING_DATASOURCE_URL=jdbc:mariadb://localhost:3306/springsecurity
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWD=your_password
```

---

## Key Endpoints

| Endpoint | Method | Auth Required | Role Required | Notes |
|----------|--------|---------------|---------------|-------|
| `/v1/contact-type/get-contact-types` | GET | Yes | USER or ADMIN | Returns contact types list |
| Any other | ANY | Yes | Any | All other routes require authentication |

**Keycloak Endpoints (External):**
| Endpoint | Purpose |
|----------|---------|
| `http://localhost:8080/realms/SpringBootAppTest/protocol/openid-connect/token` | Get access token |
| `http://localhost:8080/realms/SpringBootAppTest/protocol/openid-connect/certs` | JWK Set (public keys) |

---

## Technologies Used

| Technology | Purpose | Version |
|------------|---------|---------|
| Spring Boot | Web framework | 3.5.10 |
| Spring Security | Authentication/Authorization | Latest |
| Spring Security OAuth2 Resource Server | JWT validation | Latest |
| Keycloak | Identity & Access Management | 23+ |
| MariaDB | Database | 10.6+ |
| JPA/Hibernate | ORM | Latest |
| Lombok | Code generation | Latest |
| Maven | Build tool | 3.9.0+ |
| Java | Language | 17+ |

---

## Common Issues

### "401 Unauthorized - Invalid JWT token"
- **Cause**: Token expired, invalid signature, or wrong issuer
- **Solution**: 
  - Get a new token from Keycloak
  - Verify `jwk-set-uri` points to correct Keycloak realm
  - Check token expiration time

### "403 Forbidden - Insufficient permissions"
- **Cause**: User doesn't have required role (USER or ADMIN)
- **Solution**: 
  - Check user's role mappings in Keycloak
  - Verify `KeyCloakRoleConverter` is extracting roles correctly
  - Ensure realm_access.roles contains USER or ADMIN

### "Keycloak connection refused"
- **Cause**: Keycloak server not running
- **Solution**: Start Keycloak: `bin/kc.sh start-dev`

### "JWK Set URI not found"
- **Cause**: Wrong realm name or Keycloak not accessible
- **Solution**: 
  - Verify realm name is `SpringBootAppTest`
  - Check Keycloak is running on `http://localhost:8080`
  - Test JWK URI in browser

### "Database connection error"
- **Cause**: MariaDB not running or wrong credentials
- **Solution**:
  - Start MariaDB service
  - Verify credentials in `application.yml`
  - Run `agenda.sql` to create tables

### "Bearer token not found"
- **Cause**: Authorization header missing or malformed
- **Solution**: Include header: `Authorization: Bearer {token}`

---

## What You Learn

✅ **OAuth2 Resource Server** - Validating JWT tokens  
✅ **Keycloak Integration** - Enterprise-grade Identity Provider  
✅ **JWT Token Validation** - RS256 signature verification with JWK Set  
✅ **Custom Role Conversion** - Mapping Keycloak realm roles to Spring authorities  
✅ **Role-Based Access Control** - Securing endpoints by role  
✅ **Stateless Authentication** - No server-side sessions  
✅ **REST API Security** - Modern API authentication patterns  
✅ **Database Integration** - MariaDB with JPA

---

## Next Steps

1. **Test with Postman** - Get token from Keycloak and call API
2. **Add more roles** - Create MANAGER, GUEST roles in Keycloak
3. **Implement logout** - Token blacklisting or short expiration
4. **Add more endpoints** - Secure additional REST APIs
5. **Custom claims** - Extract user info from JWT
6. **Error handling** - Custom exception handling for auth errors
7. **Unit tests** - Test security configuration and role conversion

---

## Useful Commands

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run

# Run on different port
mvn spring-boot:run -Dspring-boot.run.arguments='--server.port=8091'

# Test
mvn test

# Package
mvn clean package
java -jar target/DIP_SPRING_SECURITY-0.0.1-SNAPSHOT.jar
```

---

## Dependencies (pom.xml)

- **spring-boot-starter-web** - Web framework
- **spring-boot-starter-security** - Security
- **spring-boot-starter-oauth2-resource-server** - OAuth2 Resource Server support
- **spring-boot-starter-data-jpa** - JPA/Hibernate
- **spring-boot-starter-thymeleaf** - Template engine
- **mariadb-java-client** - MariaDB JDBC driver
- **lombok** - Code generation
- **jjwt** - JWT library (for custom use if needed)
- **spring-boot-devtools** - Hot reload

---

## License

See [LICENSE](LICENSE)

---

## Notes for Students

This project demonstrates:
- ✅ OAuth2 Resource Server architecture
- ✅ Enterprise Identity Provider integration (Keycloak)
- ✅ JWT token validation with public key cryptography
- ✅ Custom role mapping from external IdP
- ✅ Stateless REST API security
- ✅ Role-based access control
- ✅ Production-ready authentication patterns

**Why OAuth2 Resource Server?**
- Separates authentication from authorization
- Centralized user management in Keycloak
- No password storage in application
- Scalable across microservices
- Industry standard for APIs
- Tokens can be used across multiple services

**Why Keycloak?**
- Open-source Identity & Access Management
- Full-featured IdP with user management
- Supports multiple authentication protocols
- Built-in user federation
- Production-ready and enterprise-grade

---

**Welcome to the Spring Security course!**

_Last updated: February 8, 2026_  
_Version: 5.0 (OAuth2 Resource Server + Keycloak)_  
_Status: Production-ready_
