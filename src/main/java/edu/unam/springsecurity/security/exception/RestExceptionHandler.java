package edu.unam.springsecurity.security.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    /************************************* GLOBAL EXCEPTIONS *************************************/

    private ResponseEntity<Object> buildResponseEntity(ExceptionResponse exceptionResponse) {
        return new ResponseEntity<>(exceptionResponse, exceptionResponse.getErrorStatus());
    }

    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode errorStatus,
            WebRequest request) {
        log.info("Entering RestExceptionHandler.handleMethodArgumentNotValid");
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        ExceptionResponse response = ExceptionResponse.builder()
                .errorStatus(HttpStatus.BAD_REQUEST)
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .errorMessage(ex.getLocalizedMessage())
                .timestamp(LocalDateTime.now())
                .errors(errors)
                .build();
        return handleExceptionInternal(
                ex, response, headers, response.getErrorStatus(), request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        log.info("Entering RestExceptionHandler.handleMissingServletRequestParameter");
        String error = ex.getParameterName() + " parameter is missing";
        ExceptionResponse response = ExceptionResponse.builder()
                .errorStatus(HttpStatus.BAD_REQUEST)
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .errorMessage(ex.getLocalizedMessage())
                .timestamp(LocalDateTime.now())
                .errors(List.of(error))
                .build();
        return new ResponseEntity<>(
                response, new HttpHeaders(), response.getErrorStatus());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex) {
        log.info("Entering RestExceptionHandler.handleMethodArgumentTypeMismatch");
        String error =
                ex.getName() + " should be of type " + Objects.requireNonNull(ex.getRequiredType()).getName();
        ExceptionResponse response = ExceptionResponse.builder()
                .errorStatus(HttpStatus.BAD_REQUEST)
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .errorMessage(ex.getLocalizedMessage())
                .timestamp(LocalDateTime.now())
                .errors(List.of(error))
                .build();
        return new ResponseEntity<>(
                response, new HttpHeaders(), response.getErrorStatus());
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.info("Entering RestExceptionHandler.handleNoHandlerFoundException");
        String error = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();
        ExceptionResponse response = ExceptionResponse.builder()
                .errorStatus(HttpStatus.NOT_FOUND)
                .errorCode(HttpStatus.NOT_FOUND.value())
                .errorMessage(ex.getLocalizedMessage())
                .timestamp(LocalDateTime.now())
                .errors(List.of(error))
                .build();
        return new ResponseEntity<>(response, new HttpHeaders(), response.getErrorStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.info("Entering RestExceptionHandler.handleHttpRequestMethodNotSupported");
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod());
        builder.append(" method is not supported for this request. Supported methods are ");
        Objects.requireNonNull(ex.getSupportedHttpMethods()).forEach(t -> builder.append(t).append(" "));
        ExceptionResponse response = ExceptionResponse.builder()
                .errorStatus(HttpStatus.METHOD_NOT_ALLOWED)
                .errorCode(HttpStatus.METHOD_NOT_ALLOWED.value())
                .errorMessage(ex.getLocalizedMessage())
                .timestamp(LocalDateTime.now())
                .errors(List.of(builder.toString()))
                .build();
        return new ResponseEntity<>(response, new HttpHeaders(), response.getErrorStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.info("Entering RestExceptionHandler.handleHttpMediaTypeNotSupported");
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));
        ExceptionResponse response = ExceptionResponse.builder()
                .errorStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .errorCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .errorMessage(ex.getLocalizedMessage())
                .timestamp(LocalDateTime.now())
                .errors(List.of(builder.substring(0, builder.length() - 2)))
                .build();
        return new ResponseEntity<>(response, new HttpHeaders(), response.getErrorStatus());
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAll(Exception ex) {
        log.info("Entering RestExceptionHandler.handleAll");
        ExceptionResponse response = ExceptionResponse.builder()
                .errorStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorMessage(ex.getLocalizedMessage())
                .timestamp(LocalDateTime.now())
                .errors(List.of("error occurred"))
                .build();
        return new ResponseEntity<>(response, new HttpHeaders(), response.getErrorStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> details = new ArrayList<>();
        details.add(ex.getMessage());
        ExceptionResponse response = ExceptionResponse.builder()
                .errorStatus(HttpStatus.BAD_REQUEST)
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .errorMessage("Malformed JSON request")
                .timestamp(LocalDateTime.now())
                .errors(details)
                .build();
        return new ResponseEntity<>(response, new HttpHeaders(), response.getErrorStatus());
    }
}
