package edu.unam.springsecurity.controller;

import java.util.List;

import edu.unam.springsecurity.dto.NewUser;
import edu.unam.springsecurity.dto.UserInfo;
import edu.unam.springsecurity.service.UserLdapService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ldap.NameAlreadyBoundException;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/api/usuarios")
public class UserController {

    private final UserLdapService service;

    public UserController(UserLdapService service) {
        this.service = service;
    }

    @GetMapping
    public List<UserInfo> listar() {
        return service.listar();
    }

    @GetMapping("/{uid}")
    public ResponseEntity<UserInfo> obtener(@PathVariable("uid") String uid) {
        return service.buscarPorUid(uid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<String> crear(@RequestBody NewUser u) {
        if (service.buscarPorUid(u.uid()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El uid ya existe");
        }
        try {
            service.crear(u);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Usuario " + u.uid() + " creado en " + u.grupo());
        } catch (NameAlreadyBoundException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El uid ya existe");
        } catch (NameNotFoundException e) {
            return ResponseEntity.badRequest().body("El grupo no existe");
        }
    }
}
