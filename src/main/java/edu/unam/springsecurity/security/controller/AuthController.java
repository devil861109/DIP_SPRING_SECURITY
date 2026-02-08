package edu.unam.springsecurity.security.controller;

import edu.unam.springsecurity.auth.model.UserInfo;
import edu.unam.springsecurity.auth.repository.UserInfoRepository;
import edu.unam.springsecurity.security.exception.ExceptionResponse;
import edu.unam.springsecurity.security.jwt.JWTTokenProvider;
import edu.unam.springsecurity.security.model.UserDetailsImpl;
import edu.unam.springsecurity.security.request.JwtRequest;
import edu.unam.springsecurity.security.request.LoginUserRequest;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JWTTokenProvider jwtTokenProvider;
    private final UserInfoRepository userInfoRepository;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JWTTokenProvider jwtTokenProvider,
                         UserInfoRepository userInfoRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userInfoRepository = userInfoRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginUserRequest authenticationRequest,
                                                       BindingResult bindingResult) throws Exception {
        if(bindingResult.hasErrors())
            return new ResponseEntity<>(ExceptionResponse.builder()
                    .errorStatus(HttpStatus.BAD_REQUEST)
                    .errorCode(HttpStatus.BAD_REQUEST.value())
                    .errorMessage(bindingResult.toString())
                    .timestamp(LocalDateTime.now())
                    .build(), HttpStatus.BAD_REQUEST);

        Authentication authentication = authenticate(authenticationRequest.getUsername(),
                authenticationRequest.getPassword());
        log.info("authentication {}", authentication);
        UserDetailsImpl usuario = (UserDetailsImpl) authentication.getPrincipal();
        String jwtToken = jwtTokenProvider.generateJwtToken(usuario);
        String refreshToken = jwtTokenProvider.generateRefreshToken(usuario);
        JwtRequest jwtRequest = new JwtRequest(jwtToken, usuario.getId(), usuario.getEmail(),
                jwtTokenProvider.getExpiryDuration(), authentication.getAuthorities());
        // Add refresh token and expiry (using setters from Lombok)
        jwtRequest.setRefreshToken(refreshToken);
        jwtRequest.setRefreshTokenExpiry(jwtTokenProvider.getRefreshExpiryDuration());
        return new ResponseEntity<>(jwtRequest, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        try {
            String refreshToken = "";
            if(request.getCookies() != null) {
                for(Cookie cookie : request.getCookies()) {
                    if(cookie.getName().equals("refreshToken")) {
                        refreshToken = cookie.getValue();
                        break;
                    }
                }
            }

            if(refreshToken == null || refreshToken.isEmpty()) {
                return new ResponseEntity<>(ExceptionResponse.builder()
                        .errorStatus(HttpStatus.UNAUTHORIZED)
                        .errorCode(HttpStatus.UNAUTHORIZED.value())
                        .errorMessage("Refresh token not found")
                        .timestamp(LocalDateTime.now())
                        .build(), HttpStatus.UNAUTHORIZED);
            }

            // Validate refresh token
            if(!jwtTokenProvider.validateRefreshToken(refreshToken)) {
                return new ResponseEntity<>(ExceptionResponse.builder()
                        .errorStatus(HttpStatus.UNAUTHORIZED)
                        .errorCode(HttpStatus.UNAUTHORIZED.value())
                        .errorMessage("Invalid or expired refresh token")
                        .timestamp(LocalDateTime.now())
                        .build(), HttpStatus.UNAUTHORIZED);
            }

            // Extract user info from refresh token
            Claims claims = jwtTokenProvider.getClaims(refreshToken);
            String username = claims.getIssuer();
            Long userId = claims.get("issid", Long.class);

            // Load user from database
            UserInfo userInfo = userInfoRepository.findById(userId).orElse(null);
            if(userInfo == null) {
                return new ResponseEntity<>(ExceptionResponse.builder()
                        .errorStatus(HttpStatus.UNAUTHORIZED)
                        .errorCode(HttpStatus.UNAUTHORIZED.value())
                        .errorMessage("User not found")
                        .timestamp(LocalDateTime.now())
                        .build(), HttpStatus.UNAUTHORIZED);
            }

            // Create UserDetailsImpl from the loaded user
            UserDetailsImpl userDetails = new UserDetailsImpl(userInfo);
            String newAccessToken = jwtTokenProvider.generateJwtToken(userDetails);

            JwtRequest jwtRequest = new JwtRequest(newAccessToken, userDetails.getId(),
                    userDetails.getUsername(), jwtTokenProvider.getExpiryDuration(),
                    userDetails.getAuthorities());
            jwtRequest.setRefreshToken(refreshToken);
            jwtRequest.setRefreshTokenExpiry(jwtTokenProvider.getRefreshExpiryDuration());

            log.info("Token refreshed successfully for user: {}", username);
            return new ResponseEntity<>(jwtRequest, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error refreshing token: {}", e.getMessage());
            return new ResponseEntity<>(ExceptionResponse.builder()
                    .errorStatus(HttpStatus.UNAUTHORIZED)
                    .errorCode(HttpStatus.UNAUTHORIZED.value())
                    .errorMessage("Error refreshing token: " + e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build(), HttpStatus.UNAUTHORIZED);
        }
    }

    private Authentication authenticate(String username, String password) throws Exception {
        try {
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
