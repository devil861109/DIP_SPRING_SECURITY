package edu.unam.springsecurity.security.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({
        "username",
        "password"
})
public class LoginUserRequest {
    @JsonProperty("username")
    private String username;
    @JsonProperty("password")
    private String password;
}
