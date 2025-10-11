package edu.unam.springsecurity.security.controller;

import edu.unam.springsecurity.auth.dto.UserInfoDTO;
import edu.unam.springsecurity.auth.service.UserInfoService;
import edu.unam.springsecurity.security.exception.ExceptionResponse;
import edu.unam.springsecurity.security.jwt.JWTTokenProvider;
import edu.unam.springsecurity.security.model.UserDetailsImpl;
import edu.unam.springsecurity.security.request.JwtRequest;
import edu.unam.springsecurity.security.request.LoginUserRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JWTTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
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
        JwtRequest jwtRequest = new JwtRequest(jwtToken, usuario.getId(), usuario.getEmail(),
                jwtTokenProvider.getExpiryDuration(), authentication.getAuthorities());
        return new ResponseEntity<>(jwtRequest, HttpStatus.OK);
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
