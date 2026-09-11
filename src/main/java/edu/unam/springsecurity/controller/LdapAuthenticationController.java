package edu.unam.springsecurity.controller;

import org.springframework.security.core.Authentication;
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
}