package edu.unam.springsecurity.system.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {
	public String getText() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		log.info("Auth {}", auth);
		log.info("Credentials {}", auth.getCredentials());
		return "User";
	}
}
