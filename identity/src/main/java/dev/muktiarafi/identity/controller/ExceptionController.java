package dev.muktiarafi.identity.controller;

import dev.muktiarafi.identity.dto.ResponseDto;
import dev.muktiarafi.identity.exception.ResourceConflictException;
import dev.muktiarafi.identity.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionController {
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResponseDto<List<String>>> badCredentialExceptionHandler(BadCredentialsException e) {
        var messages = List.of(e.getMessage());
        var status = HttpStatus.UNAUTHORIZED;

        return new ResponseEntity<>(new ResponseDto<>(status.value(), status.getReasonPhrase(), messages), status);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseDto<List<String>>> resourceNotFoundExceptionHandler(ResourceNotFoundException e) {
        var messages = List.of(e.getMessage());
        var status = HttpStatus.NOT_FOUND;

        return new ResponseEntity<>(new ResponseDto<>(status.value(), status.getReasonPhrase(), messages), status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<List<String>>> validationExceptionHandler(MethodArgumentNotValidException e) {
        var messages = e.getBindingResult()
                .getAllErrors().stream()
                .map(err -> ((FieldError) err).getField() + " " + err.getDefaultMessage())
                .collect(Collectors.toList());
        var status = HttpStatus.NOT_FOUND;

        return new ResponseEntity<>(new ResponseDto<>(status.value(), status.getReasonPhrase(), messages), status);
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ResponseDto<List<String>>> resourceConflictExceptionHandler(ResourceConflictException e) {
        var messages = List.of(e.getMessage());
        var status = HttpStatus.CONFLICT;

        return new ResponseEntity<>(new ResponseDto<>(status.value(), status.getReasonPhrase(), messages), status);
    }
}
