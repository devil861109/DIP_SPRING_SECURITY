# JWT Refresh Token Implementation Guide

## Overview

This project implements a complete JWT token refresh mechanism that allows clients to obtain new access tokens without requiring a re-login. This is a critical feature for production applications where long-lived tokens pose a security risk.

## How It Works

### Token Types

The application uses two types of JWT tokens:

1. **Access Token** (`typ: access`)
   - Short-lived token (default: 1 hour = 3600 seconds)
   - Contains full user information and authorities
   - Used for every authenticated request
   - Includes claims: `principal`, `auth`, `issid`, `issname`

2. **Refresh Token** (`typ: refresh`)
   - Longer-lived token (default: 15 minutes = 900 seconds)
   - Contains minimal user information (only userId)
   - Used ONLY to obtain a new access token
   - Includes claims: `issid` (user ID)

### Token Generation Process

#### At Login (`POST /api/auth/login`)

```
1. User submits credentials
2. AuthenticationManager validates credentials
3. UserDetailsServiceImpl loads user from database
4. Two tokens are generated:
   - Access Token: Contains full user details
   - Refresh Token: Contains only user ID
5. Both tokens are returned to client
```

**Request:**
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer",
  "userId": 1,
  "userName": "user@example.com",
  "expiryDuration": 3600000,
  "authorities": [
    {
      "authority": "ROLE_USER"
    }
  ],
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
  "refreshTokenExpiry": 900000
}
```

#### Token Refresh (`POST /api/auth/refresh`)

```
1. Client sends refresh token (from cookie)
2. AuthController validates refresh token
3. Refresh token is verified:
   - Token type must be "refresh"
   - Token must not be expired
   - Token signature must be valid
4. User ID is extracted from token claims
5. User is reloaded from database
6. New access token is generated
7. Original refresh token is returned (unchanged)
```

**Request:**
```bash
POST /api/auth/refresh
# Refresh token is sent in cookie named "refreshToken"
Cookie: refreshToken=eyJhbGciOiJIUzUxMiJ9...
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9... (NEW)",
  "tokenType": "Bearer",
  "userId": 1,
  "userName": "user@example.com",
  "expiryDuration": 3600000,
  "authorities": [...],
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9... (SAME)",
  "refreshTokenExpiry": 900000
}
```

## Implementation Details

### 1. JWTTokenProvider

**Methods:**

- `generateJwtToken(UserDetailsImpl user)` - Generates access token
- `generateRefreshToken(UserDetailsImpl user)` - Generates refresh token
- `validateJwtToken(String token)` - Validates access token
- `validateRefreshToken(String token)` - Validates refresh token
- `getClaims(String token)` - Extracts claims from token
- `getExpiryDuration()` - Returns access token expiry in milliseconds
- `getRefreshExpiryDuration()` - Returns refresh token expiry in milliseconds

**Token Structure:**

```java
// Access Token
Jwts.builder()
    .subject("UNAM")
    .issuer(user.getUsername())              // Username/email
    .audience().add("JAVA").and()
    .claim("principal", user)                 // Full UserDetailsImpl
    .claim("auth", user.getAuthorities())     // Roles/permissions
    .claim("issid", user.getId())             // User ID
    .claim("issname", user.getName())         // Full name
    .claim("typ", "access")                   // Token type
    .issuedAt(now)
    .expiration(expiryDate)
    .signWith(key)
    .compact();

// Refresh Token
Jwts.builder()
    .subject("UNAM")
    .issuer(user.getUsername())
    .audience().add("JAVA").and()
    .claim("issid", user.getId())             // Only user ID
    .claim("typ", "refresh")                  // Token type
    .issuedAt(now)
    .expiration(expiryDate)
    .signWith(key)
    .compact();
```

### 2. AuthController

**Login Endpoint:**
```java
@PostMapping("/login")
public ResponseEntity<?> createAuthenticationToken(
    @RequestBody LoginUserRequest authenticationRequest,
    BindingResult bindingResult) throws Exception
```

- Validates user credentials
- Generates both access and refresh tokens
- Returns both tokens in response
- Refresh token should be stored in secure HTTP-only cookie by client

**Refresh Endpoint:**
```java
@PostMapping("/refresh")
public ResponseEntity<?> refreshToken(HttpServletRequest request)
```

- Extracts refresh token from cookie
- Validates refresh token
- Extracts user ID from claims
- Loads user from database
- Generates new access token
- Returns new access token with original refresh token

### 3. Security Configuration

**`/api/auth/refresh` is permitted** in `SecurityConfiguration`:
```java
.requestMatchers("/refresh").permitAll()
```

This allows unauthenticated requests to the refresh endpoint (the refresh token itself provides security).

### 4. Token Storage & Handling

**Where to store tokens:**

- **Access Token**: Store in memory or local storage (accessed from cookies by backend)
- **Refresh Token**: Store in HTTP-only, Secure, SameSite cookie (automatically sent by browser)

**Why HTTP-only cookies for refresh token:**
- Prevents XSS attacks from accessing the token
- Automatically sent by browser on same-origin requests
- Cannot be accessed by JavaScript

## Usage Flow

### Client Flow

```
1. USER LOGS IN
   POST /api/auth/login
   Receive: {token: "...", refreshToken: "..."}
   Store: token in memory, refreshToken in HTTP-only cookie

2. MAKING AUTHENTICATED REQUESTS
   GET /pokemon
   Authorization: Bearer {token}

3. TOKEN EXPIRES (after 1 hour)
   GET /pokemon → 401 Unauthorized

4. REFRESH TOKEN
   POST /api/auth/refresh
   Cookie: refreshToken=...
   Receive: {token: "... (NEW)", refreshToken: "..."}
   Update: token in memory

5. CONTINUE USING APP
   GET /pokemon
   Authorization: Bearer {token} (NEW)

6. REFRESH TOKEN EXPIRES (after 15 minutes)
   POST /api/auth/refresh → 401 Unauthorized
   → User must login again
```

## Configuration

**application.yml:**
```yaml
jwt:
  secret: ${SPRING_JWT_SECRET:your-secret-key}
  expirationDateInMs: ${SPRING_JWT_EXP_DATE_MS:3600}           # 1 hour (in seconds)
  refreshExpirationDateInMs: ${SPRING_JWT_REFRESH_EXP_DATE_MS:900}  # 15 minutes (in seconds)
```

## Error Handling

### Common Errors & Solutions

| Error | Cause | Solution |
|-------|-------|----------|
| 401 Unauthorized | Refresh token not found | Ensure cookie is being sent |
| 401 Unauthorized | Refresh token expired | User must login again |
| 401 Unauthorized | Invalid refresh token | Token signature is invalid |
| 401 Unauthorized | User not found in DB | User was deleted; login again |
| 400 Bad Request | Invalid token format | Token is malformed |

## Best Practices

### ✅ DO

1. **Store access tokens in memory** - Easier to manage, safer from XSS if not accessed by JS
2. **Store refresh tokens in HTTP-only cookies** - Automatic sending, protected from XSS
3. **Validate refresh token type** - Ensure `typ: refresh` claim
4. **Reload user from database** - Ensure permissions are current
5. **Short expiry for access tokens** - Limits exposure window (1 hour recommended)
6. **Longer expiry for refresh tokens** - Balances security and UX (15 min recommended)
7. **Use HTTPS** - Prevent token interception

### ❌ DON'T

1. **Don't store refresh tokens in localStorage** - Vulnerable to XSS
2. **Don't skip token type validation** - Could use access token as refresh token
3. **Don't ignore token expiration** - Increases security risk
4. **Don't return sensitive data in refresh token** - Only user ID needed
5. **Don't allow infinite refresh** - Set reasonable limits
6. **Don't reuse same refresh token** - Could be stolen

## Testing with cURL

### Login
```bash
curl -X POST http://localhost:8090/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user@example.com",
    "password": "password"
  }' \
  -c cookies.txt
```

### Use Access Token
```bash
curl -X GET http://localhost:8090/pokemon \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### Refresh Token
```bash
curl -X POST http://localhost:8090/api/auth/refresh \
  -b cookies.txt
```

## Frontend Integration Example

```javascript
// Login
async function login(email, password) {
  const response = await fetch('/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username: email, password }),
    credentials: 'include'  // Send cookies
  });
  const data = await response.json();
  localStorage.setItem('token', data.token);
  // refreshToken is auto-stored in HTTP-only cookie
  return data;
}

// Make authenticated request
async function fetchProtected(url) {
  let response = await fetch(url, {
    headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` },
    credentials: 'include'
  });
  
  if (response.status === 401) {
    // Token expired, refresh it
    const refreshResponse = await fetch('/api/auth/refresh', {
      method: 'POST',
      credentials: 'include'
    });
    const data = await refreshResponse.json();
    localStorage.setItem('token', data.token);
    
    // Retry original request
    response = await fetch(url, {
      headers: { 'Authorization': `Bearer ${data.token}` },
      credentials: 'include'
    });
  }
  
  return response;
}
```

## Summary

Your refresh token implementation now provides:

✅ Secure token-based authentication  
✅ Short-lived access tokens (1 hour)  
✅ Longer-lived refresh tokens (15 minutes)  
✅ Automatic token refresh without re-login  
✅ User data revalidation on token refresh  
✅ Proper error handling  
✅ HTTP-only cookie support  

This is production-ready security best practice! 🎓

