package edu.unam.springsecurity.security.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JwtRequest {
    private String token;
    private String tokenType;
    private Long userId;
    private String userName;
    private Long expiryDuration;
    private Collection<? extends GrantedAuthority> authorities;

    public JwtRequest(String token, Long userId, String userName, Long expiryDuration,
                      Collection<? extends GrantedAuthority> authorities) {
        this.token = token;
        this.userId = userId;
        this.userName = userName;
        this.expiryDuration = expiryDuration;
        this.authorities = authorities;
        this.tokenType = "Bearer";
    }
}
