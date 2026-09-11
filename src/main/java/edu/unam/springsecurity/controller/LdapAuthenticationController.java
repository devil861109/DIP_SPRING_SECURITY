package edu.unam.springsecurity.controller;

import edu.unam.springsecurity.dto.UserInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.ldap.userdetails.InetOrgPerson;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LdapAuthenticationController {
    @GetMapping("/")
    public String home(Authentication auth) {
        return "Hola " + auth.getName() + " | Roles: " + auth.getAuthorities();
    }

    @GetMapping("/admin")
    public String admin() { return "Zona de administradores"; }

    @GetMapping("/profesores")
    public String profesores() { return "Zona de profesores"; }

    @GetMapping("/alumnos")
    public String alumnos() { return "Zona de alumnos"; }

    @GetMapping("/perfil")
    public UserInfo perfil(@AuthenticationPrincipal InetOrgPerson p) {
        return new UserInfo(
                p.getUid(), p.getGivenName(), p.getSn(), p.getCn()[0],
                p.getMail(), p.getDn(),
                p.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
    }
}
