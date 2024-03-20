package edu.unam.springsecurity.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UserInfoRoleNotFoundException extends Exception {
    public UserInfoRoleNotFoundException(Long id) {
        super("User Info Role with id " + id + " is NOT found");
    }
}
