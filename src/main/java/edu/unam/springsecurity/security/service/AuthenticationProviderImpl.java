package edu.unam.springsecurity.security.service;

import edu.unam.springsecurity.auth.model.UserInfo;
import edu.unam.springsecurity.auth.repository.UserInfoRepository;
import edu.unam.springsecurity.security.model.UserDetailsImpl;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class AuthenticationProviderImpl implements AuthenticationProvider {
    private final UserInfoRepository userInfoRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) {
        String username = authentication.getName();
        String pwd = authentication.getCredentials().toString();

        //No recomendado. Razones:
        //Separación de responsabilidades: El UserDetailsService está diseñado específicamente
        //para encapsular la lógica de recuperación de usuarios desde la base de datos o
        //cualquier fuente externa.
        //Muchos componentes de Spring (como DaoAuthenticationProvider) esperan que el
        //UserDetailsService maneje la carga de usuarios.
        /*UserInfo userAdmin = Optional.ofNullable(userInfoRepository.findByUseNickname(username))
                .orElseThrow(() -> new BadCredentialsException("User not found in database"));
        if (passwordEncoder.matches(pwd, userAdmin.getUsePasswd())) { //Bcrypt, Scrypt
            List<GrantedAuthority> authorities = userAdmin.getUseInfoRoles().stream().map(role ->
                    new SimpleGrantedAuthority(role.getUsrRoleName())).collect(Collectors.toList());
            return new UsernamePasswordAuthenticationToken(username, pwd, authorities);
        } else {
            throw new BadCredentialsException("Invalid password!");
        }*/

        UserDetails user = userDetailsService.loadUserByUsername(username);
        if (passwordEncoder.matches(pwd, user.getPassword())) {
            //OJO
            //return new UsernamePasswordAuthenticationToken(username, pwd, user.getAuthorities());
            return new UsernamePasswordAuthenticationToken(username, user.getAuthorities());
        } else {
            throw new BadCredentialsException("Credenciales inválidas");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
