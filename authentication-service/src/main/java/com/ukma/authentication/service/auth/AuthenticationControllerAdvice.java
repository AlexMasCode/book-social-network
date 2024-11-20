package com.ukma.authentication.service.auth;

import com.ukma.authentication.service.shared.dto.ExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class AuthenticationControllerAdvice {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionDto> handleIllegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(
            ExceptionDto.builder()
                .exceptionClass(exception.getClass().getName())
                .build()
        );
    }

    @ExceptionHandler({NoSuchElementException.class, UsernameNotFoundException.class})
    public ResponseEntity<ExceptionDto> handleNoSuchElementException(Exception exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ExceptionDto.builder()
                .exceptionClass(exception.getClass().getName())
                .build()
        );
    }
}
