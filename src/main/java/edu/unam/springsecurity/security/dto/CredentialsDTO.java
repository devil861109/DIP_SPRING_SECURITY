package edu.unam.springsecurity.security.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class CredentialsDTO {
    private String sub;
    private String aud;
    private Long iat;
    private Long exp;
}
