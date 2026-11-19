package com.example.beautymanager.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice 
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, String>> manejarBusinessException(
            BusinessException exception
    ) {

        Map<String, String> respuesta = Map.of(
            "message",
            exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(respuesta);
    }

    //========================================
    //ERRORES DE AUTORIZACIÓN
    //========================================

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> manejarAccesoDenegado(
            AccessDeniedException exception
    ) {

        Map<String, String> respuesta = Map.of(
            "message",
            exception.getMessage() != null
                ? exception.getMessage()
                : "No tenés permisos para realizar esta operación"
        );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(respuesta);
    }
}
