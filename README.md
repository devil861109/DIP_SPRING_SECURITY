# DIP_SPRING_SECURITY
Diplomado UNAM - Spring Security

A **Spring Boot 3.5.10** app demonstrating **OAuth2 authentication with GitHub**. Students learn modern authentication patterns using OpenID Connect and OAuth2 standards.

**Current implementation:**
- ✅ OAuth2 login via GitHub
- ✅ OpenID Connect (OIDC) provider
- ✅ Automatic user authentication without credentials
- ✅ Secure token-based session management
- ✅ Simple and minimal security configuration
- ✅ All routes require authentication

---

## Getting Your Development Environment Setup

### Recommended Versions

| Tool | Version | Download | Notes |
|------|---------|----------|-------|
| Oracle Java 17 JDK | 17+ | https://www.oracle.com/java/technologies/downloads/#java17 | Java 17+ required for Spring Boot 3 |
| IntelliJ IDEA | 2022+ | https://www.jetbrains.com/idea/download/ | Community Edition works fine |
| Maven | 3.9.0+ | https://maven.apache.org/download.cgi | Build tool |
| Git | 2.44+ | https://git-scm.com/downloads | Version control |
| GitHub Account | Free | https://github.com/signup | For OAuth2 login |

**Verify installation:**
```bash
java -version
mvn -version
git --version
```

---

## Quick Start (10 minutes)

### 1. Clone the repository
```bash
git clone <repository-url>
cd DIP_SPRING_SECURITY
```

### 2. Register OAuth2 App on GitHub

1. Go to https://github.com/settings/developers
2. Click **New OAuth App**
3. Fill in the form:
   - **Application name**: `DIP_SPRING_SECURITY` (or any name)
   - **Homepage URL**: `http://localhost:8090`
   - **Authorization callback URL**: `http://localhost:8090/login/oauth2/code/github`
4. Copy the **Client ID** and **Client Secret**

### 3. Configure credentials

Update `src/main/resources/application.properties`:
```properties
spring.security.oauth2.client.registration.github.client-id=YOUR_CLIENT_ID
spring.security.oauth2.client.registration.github.client-secret=YOUR_CLIENT_SECRET
```

Or use environment variables:
```bash
export SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GITHUB_CLIENT_ID=your_client_id
export SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GITHUB_CLIENT_SECRET=your_client_secret
```

### 4. Build & Run
```bash
mvn clean install
mvn spring-boot:run
```

### 5. Test
1. Open http://localhost:8090/
2. You'll be redirected to GitHub login
3. Authorize the app
4. You'll see `secure.html` with your GitHub user info

---

## Project Structure

```
DIP_SPRING_SECURITY/
├── src/
│   ├── main/
│   │   ├── java/edu/unam/springsecurity/
│   │   │   ├── SpringSecurityApplication.java     ← Entry point
│   │   │   ├── config/
│   │   │   │   └── SecurityConfiguration.java     ← OAuth2 config
│   │   │   └── controller/
│   │   │       └── SecureController.java          ← Route handler
│   │   └── resources/
│   │       ├── application.properties             ← OAuth2 credentials
│   │       └── static/
│   │           └── secure.html                    ← Main page
│   └── test/
├── pom.xml                                         ← Maven config
├── README.md                                       ← This file
└── HELP.md
```

---

## The Code

### SecurityConfiguration.java
```java
@Configuration
public class SecurityConfiguration {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests)->requests.anyRequest().authenticated())
                .oauth2Login(Customizer.withDefaults());
        return http.build();
    }
}
```

**What it does:**
- `authorizeHttpRequests()` - All routes require authentication
- `oauth2Login()` - Enable OAuth2 login flow with GitHub
- Auto-redirects to GitHub login if user not authenticated

### SecureController.java
```java
@Controller
public class SecureController {
    @GetMapping("/")
    public String main(OAuth2AuthenticationToken token) {
        System.out.println(token.getPrincipal());
        return "secure.html";
    }
}
```

**What it does:**
- Handles the home page route `/`
- Receives `OAuth2AuthenticationToken` with GitHub user info
- Returns `secure.html` template
- Token contains user's GitHub profile data

### secure.html
The main page that users see after authentication.

---

## How OAuth2 Login Works

```
1. USER VISITS http://localhost:8090/
   ↓
2. SECURITY FILTER CHECKS AUTHENTICATION
   → Not authenticated → Redirect to OAuth2 login
   ↓
3. REDIRECT TO GITHUB
   → Send: client_id, redirect_uri, scope=openid
   ↓
4. USER AUTHORIZES ON GITHUB
   → User logs in to GitHub
   → Grants app permission to read profile
   ↓
5. GITHUB REDIRECTS BACK
   → URL: http://localhost:8090/login/oauth2/code/github?code=xxxxx
   ↓
6. SPRING EXCHANGES CODE FOR TOKEN
   → Sends: code, client_id, client_secret
   → Gets back: access_token, id_token
   ↓
7. USER AUTHENTICATED
   → Spring creates session
   → OAuth2AuthenticationToken available
   → Calls controller with token
   ↓
8. USER SEES SECURE PAGE
   → Logged in as: GitHub username
   → Can access protected resources
```

---

## Configuration

### application.properties
```properties
# GitHub OAuth2 Credentials
spring.security.oauth2.client.registration.github.client-id=YOUR_CLIENT_ID
spring.security.oauth2.client.registration.github.client-secret=YOUR_CLIENT_SECRET

# Optional: Server port
server.port=8090
```

### Environment Variables (Alternative)
```bash
export SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GITHUB_CLIENT_ID=your_id
export SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GITHUB_CLIENT_SECRET=your_secret
```

---

## Key Endpoints

| Endpoint | Purpose | Notes |
|----------|---------|-------|
| `/` | Home page (secure) | Requires authentication |
| `/oauth2/authorization/github` | Start GitHub login | Auto-redirected by filter |
| `/login/oauth2/code/github` | GitHub callback (internal) | Handled by Spring automatically |

---

## Technologies Used

| Technology | Purpose | Version |
|------------|---------|---------|
| Spring Boot | Web framework | 3.5.10 |
| Spring Security | Authentication/Authorization | Latest |
| Spring Security OAuth2 | OAuth2 support | Latest |
| GitHub | OAuth2 provider | N/A |
| Maven | Build tool | 3.9.0+ |
| Java | Language | 17+ |

---

## Getting GitHub Credentials

### Steps to register OAuth2 app:

1. **Go to GitHub Settings**
   - https://github.com/settings/developers
   - Click **OAuth Apps** → **New OAuth App**

2. **Fill Application Form**
   ```
   Application name: DIP_SPRING_SECURITY
   Homepage URL: http://localhost:8090
   Authorization callback URL: http://localhost:8090/login/oauth2/code/github
   ```

3. **Copy Credentials**
   - Client ID (displayed on app page)
   - Client Secret (click **Generate a new client secret**)

4. **Add to application.properties**
   ```properties
   spring.security.oauth2.client.registration.github.client-id=xxxxxxxxxxxx
   spring.security.oauth2.client.registration.github.client-secret=yyyyyyyyyyyy
   ```

---

## Common Issues

### "Redirect URI mismatch"
- **Cause**: GitHub expects `http://localhost:8090/login/oauth2/code/github`
- **Solution**: Go to GitHub settings and update Authorization callback URL exactly

### "Client ID invalid"
- **Cause**: Credentials not set or incorrect
- **Solution**: Check `application.properties` - must use exact GitHub credentials

### "The application is not permitted to request the openid scope"
- **Cause**: GitHub app scope is too restrictive
- **Solution**: GitHub doesn't require explicit openid scope, Spring handles automatically

### "Port 8090 already in use"
- **Solution**: Change port in `application.properties`:
  ```properties
  server.port=8091
  ```

### "Cannot resolve symbol 'OAuth2AuthenticationToken'"
- **Solution**: Ensure Maven dependency installed - run `mvn clean install`

---

## What You Learn

✅ **OAuth2 Protocol** - Industry standard for authentication  
✅ **OpenID Connect** - Identity layer on top of OAuth2  
✅ **Social Login** - GitHub authentication  
✅ **Token Management** - Spring handles token exchange automatically  
✅ **Spring Security** - Modern authentication configuration  
✅ **Best Practices** - No passwords stored, secure redirects  

---

## Next Steps

1. **Run the application** - See OAuth2 login in action
2. **Understand the flow** - Review diagram above
3. **Modify secure.html** - Display user's GitHub info
4. **Add more providers** - Google, GitHub, GitLab OAuth2
5. **Implement logout** - Add logout endpoint
6. **Secure more routes** - Add role-based access

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
- **spring-boot-starter-oauth2-client** - OAuth2 client support
- **spring-boot-devtools** - Hot reload

---

## License

See [LICENSE](LICENSE)

---

## Notes for Students

This project demonstrates:
- ✅ Modern authentication (OAuth2, not basic auth)
- ✅ Social login integration
- ✅ Minimal security configuration
- ✅ Token-based authentication
- ✅ Third-party provider integration

**Why OAuth2?**
- No passwords to store or manage
- Users don't trust entering credentials to random apps
- Single sign-on across platforms
- Industry standard
- Widely supported

---

**Welcome to the Spring Security course!**

_Last updated: February 8, 2026_  
_Version: 4.0 (OAuth2 + GitHub)_  
_Status: Production-ready_

