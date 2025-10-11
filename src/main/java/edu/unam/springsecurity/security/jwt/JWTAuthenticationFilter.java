package edu.unam.springsecurity.security.jwt;

import edu.unam.springsecurity.security.dto.CredentialsDTO;
import edu.unam.springsecurity.security.service.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private final JWTTokenProvider tokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    public JWTAuthenticationFilter(JWTTokenProvider tokenProvider, UserDetailsServiceImpl userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = getJwt(request);
            if (jwt != null && tokenProvider.validateJwtToken(jwt)) {
                Claims body = tokenProvider.getClaims(jwt);
                var authorities = (List<Map<String, String>>) body.get("auth");
                Set<SimpleGrantedAuthority> simpleGrantedAuthorities = authorities.stream()
                        .map(m -> new SimpleGrantedAuthority(m.get("authority")))
                        .collect(Collectors.toSet());
                String username = tokenProvider.getIssuer(jwt);
                //String username = tokenProvider.getFullName(jwt);
                CredentialsDTO credentials = CredentialsDTO.builder()
                        .sub(tokenProvider.getSubject(jwt)).aud(tokenProvider.getAudience(jwt))
                        .exp(tokenProvider.getTokenExpiryFromJWT(jwt).getTime())
                        .iat(tokenProvider.getTokenIatFromJWT(jwt).getTime())
                        .build();

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                /*UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, credentials, userDetails.getAuthorities());*/

                //Enriquece el Authentication con metadatos de la solicitud.
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception exception) {
            log.error("Can NOT set user authentication -> Message: {}", exception.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private String getJwt(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.replace("Bearer ", "");
        }
        return null;
    }
}
