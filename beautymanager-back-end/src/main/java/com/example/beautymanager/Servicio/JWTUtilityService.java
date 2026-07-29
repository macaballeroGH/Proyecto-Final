package com.example.beautymanager.Servicio;

import java.time.LocalDateTime;

import com.example.beautymanager.Modelo.Enums.RolesEnums;
import com.nimbusds.jwt.JWTClaimsSet;

public interface JWTUtilityService {

    String generateJWT(Long idUsuario, RolesEnums rol) throws Exception;

    JWTClaimsSet parseJWT(String token) throws Exception;

    Long getUserId(String token) throws Exception;

    RolesEnums getRol(String token) throws Exception;

    LocalDateTime getFechaExpiracion(String token) throws Exception;

    boolean isTokenValid(String token);

}
