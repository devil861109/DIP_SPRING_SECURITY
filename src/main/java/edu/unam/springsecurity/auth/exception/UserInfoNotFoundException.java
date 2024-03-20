package edu.unam.springsecurity.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UserInfoNotFoundException extends Exception {
    public UserInfoNotFoundException(Long id) {
        super("User Info with id " + id + " is NOT found");
    }

    public UserInfoNotFoundException(String email) {
        super("User Email already exists! " + email);
    }
}
