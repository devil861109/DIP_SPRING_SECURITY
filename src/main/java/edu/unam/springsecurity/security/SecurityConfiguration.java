package edu.unam.springsecurity.security;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMINISTRADORES")
                        .requestMatchers("/profesores/**").hasRole("PROFESORES")
                        .requestMatchers("/alumnos/**").hasAnyRole("ALUMNOS", "PROFESORES")
                        .anyRequest().fullyAuthenticated())
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());   // para curl y Postman
        return http.build();
    }

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .ldapAuthentication()
                .userDnPatterns("uid={0},ou=usuarios")
                .groupSearchBase("ou=grupos")
                .groupSearchFilter("(member={0})")          // groupOfNames usa member
                .contextSource()
                .url("ldap://localhost:8389/dc=unam,dc=org")
                .and()
                .passwordCompare()
                .passwordEncoder(ldapPasswordEncoder())
                .passwordAttribute("userPassword");
    }

    /**
     * Elige el algoritmo segun el prefijo guardado en userPassword:
     *   {CRYPT}$2a$...  -> BCrypt
     *   {SHA256}...     -> SHA-256 en base64
     *   sin prefijo     -> texto plano
     */
    private PasswordEncoder ldapPasswordEncoder() {
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("CRYPT", new BCryptPasswordEncoder());
        encoders.put("SHA256", new LdapSha256PasswordEncoder());
        // NoOpPasswordEncoder va a marcar una advertencia de deprecated.
        // Es intencional: solo existe para validar a los usuarios con contraseña en texto plano de tu LDIF.
        DelegatingPasswordEncoder delegating = new DelegatingPasswordEncoder("CRYPT", encoders);
        delegating.setDefaultPasswordEncoderForMatches(NoOpPasswordEncoder.getInstance());
        return delegating;
    }
}