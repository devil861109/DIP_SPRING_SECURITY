package edu.unam.springsecurity.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.ldap.LdapBindAuthenticationManagerFactory;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.InetOrgPersonContextMapper;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMINISTRADORES")
                        .requestMatchers("/profesores/**").hasRole("PROFESORES")
                        .requestMatchers("/alumnos/**").hasAnyRole("ALUMNOS", "PROFESORES")
                        .anyRequest().authenticated())
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())                          // ← permite curl -u y Postman Basic Auth
                .csrf(csrf -> csrf.ignoringRequestMatchers("/admin/api/**"))   // ← permite el POST sin token
                .logout(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public LdapContextSource contextSource(
            @Value("${ldap.url}") String url,
            @Value("${ldap.base}") String base,
            @Value("${ldap.manager-dn}") String managerDn,
            @Value("${ldap.manager-password}") String managerPassword) {
        LdapContextSource cs = new LdapContextSource();
        cs.setUrl(url);
        cs.setBase(base);              // dc=unam,dc=org
        cs.setUserDn(managerDn);       // este DN va completo, no es relativo a la base
        cs.setPassword(managerPassword);
        return cs;
    }

    @Bean
    public LdapTemplate ldapTemplate(LdapContextSource contextSource) {
        return new LdapTemplate(contextSource);
    }

    // Grupos -> roles: cn=profesores -> ROLE_PROFESORES
    @Bean
    public LdapAuthoritiesPopulator authorities(LdapContextSource contextSource) {
        DefaultLdapAuthoritiesPopulator populator =
                new DefaultLdapAuthoritiesPopulator(contextSource, "ou=grupos");
        populator.setGroupSearchFilter("(member={0})");  // tus grupos usan member
        return populator;
    }

    @Bean
    public AuthenticationManager authManager(LdapContextSource contextSource,
                                             LdapAuthoritiesPopulator authorities) {
        LdapBindAuthenticationManagerFactory factory =
                new LdapBindAuthenticationManagerFactory(contextSource);
        factory.setUserDnPatterns("uid={0},ou=usuarios");  // relativo a la base
        factory.setLdapAuthoritiesPopulator(authorities);
        factory.setUserDetailsContextMapper(new InetOrgPersonContextMapper()); //user data
        return factory.createAuthenticationManager();
    }
}
