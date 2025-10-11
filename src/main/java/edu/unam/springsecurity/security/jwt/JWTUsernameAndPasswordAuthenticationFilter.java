package edu.unam.springsecurity.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.unam.springsecurity.auth.service.UserInfoService;
import edu.unam.springsecurity.auth.dto.UserInfoDTO;
import edu.unam.springsecurity.auth.exception.UserInfoNotFoundException;
import edu.unam.springsecurity.security.model.UserDetailsImpl;
import edu.unam.springsecurity.security.request.LoginUserRequest;
import edu.unam.springsecurity.security.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@AllArgsConstructor
public class JWTUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private AuthenticationManager authenticationManager;
    private JWTTokenProvider jwtTokenProvider;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            LoginUserRequest authenticationRequest = new ObjectMapper()
                    .readValue(request.getInputStream(), LoginUserRequest.class);
            //IDEAL!
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(),
                    authenticationRequest.getPassword()
            );
            //Este proveedor llama a tu implementación de UserDetailsServiceImpl.loadUserByUsername(...).
            Authentication authenticate = authenticationManager.authenticate(authentication);
            return authenticate;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException {
        UserDetailsImpl usuario = (UserDetailsImpl) authResult.getPrincipal();
        String token = jwtTokenProvider.generateJwtToken(usuario);
        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }
}
