package edu.unam.springsecurity.security.exception;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse {
    private int errorCode;
    private HttpStatus errorStatus;
    private String errorMessage;
    private String errorUrl;
    private LocalDateTime timestamp;
    private List<String> errors;
}
