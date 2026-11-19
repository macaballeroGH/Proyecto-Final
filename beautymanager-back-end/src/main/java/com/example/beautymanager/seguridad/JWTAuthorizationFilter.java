package com.example.beautymanager.seguridad;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;
import com.example.beautymanager.Modelo.Enums.EstadoEmpleadoEnums;
import com.example.beautymanager.Modelo.Enums.EstadoUsuarioEnums;
import com.example.beautymanager.Repositorio.EmpleadoRepository;
import com.example.beautymanager.Repositorio.UsuarioRepository;
import com.example.beautymanager.Servicio.JWTUtilityService;
import com.nimbusds.jwt.JWTClaimsSet;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private final JWTUtilityService jwtUtilityService;

    private final UsuarioRepository usuarioRepository;

    private final EmpleadoRepository empleadoRepository;

    public JWTAuthorizationFilter(
            JWTUtilityService jwtUtilityService,
            UsuarioRepository usuarioRepository,
            EmpleadoRepository empleadoRepository
    ) {

        this.jwtUtilityService = jwtUtilityService;

        this.usuarioRepository = usuarioRepository;

        this.empleadoRepository = empleadoRepository;
    }

    //========================================
    //VALIDAR AUTENTICACIÓN JWT
    //========================================

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String ruta = request.getServletPath();

        boolean autenticacionPublica =
                "/auth/login".equals(ruta) ||
                "/auth/forgot-password".equals(ruta) ||
                "/auth/reset-password".equals(ruta);

        if (
            "OPTIONS".equalsIgnoreCase(request.getMethod()) ||
            autenticacionPublica
        ) {

            filterChain.doFilter(request, response);

            return;
        }

        //====================================
        //OBTENER ENCABEZADO DE AUTORIZACIÓN
        //====================================

        String header = request.getHeader("Authorization");

        //====================================
        //SOLICITUD SIN TOKEN
        //====================================

        if (
            header == null ||
            !header.startsWith("Bearer ")
        ) {

            filterChain.doFilter(request, response);

            return;
        }

        String token = header.substring(7);

        //====================================
        //VALIDAR TOKEN Y EXTRAER IDENTIDAD
        //====================================

        JWTClaimsSet claims;
        Long idUsuario;
        String rol;

        try {

            claims = jwtUtilityService.parseJWT(token);

            idUsuario = Long.valueOf(
                claims.getSubject()
            );

            rol = claims.getStringClaim("rol");

            if (idUsuario <= 0 || rol == null || rol.isBlank()) {
                responderNoAutorizado(response);
                return;
            }

        } catch (Exception e) {

            responderNoAutorizado(response);

            return;
        }

        //====================================
        //VERIFICAR REVOCACIÓN
        //====================================

        if (
            jwtUtilityService.isTokenRevocado(
                claims.getJWTID()
            )
        ) {

            responderNoAutorizado(response);

            return;
        }

        //====================================
        //OBTENER USUARIO DE LA BASE DE DATOS
        //====================================

        UsuarioEntity usuario = usuarioRepository
                .findById(idUsuario)
                .orElse(null);

        //====================================
        //VERIFICAR ESTADO DEL USUARIO
        //====================================

        if (
            usuario == null ||
            usuario.getEstadoUsuario() !=
                EstadoUsuarioEnums.ACTIVO
        ) {

            responderNoAutorizado(response);

            return;
        }

        //====================================
        //VALIDACIÓN ADICIONAL DE EMPLEADA
        //====================================

        if ("EMPLEADO".equals(rol)) {

            boolean empleadoActivo = empleadoRepository
                    .findByUsuario_Id(idUsuario)
                    .map(empleado ->
                        empleado.getEstadoEmpleado() ==
                            EstadoEmpleadoEnums.ACTIVO
                    )
                    .orElse(false);

            if (!empleadoActivo) {

                responderNoAutorizado(response);

                return;
            }
        }

        //====================================
        //CREAR AUTORIDADES
        //====================================

        List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority(
                "ROLE_" + rol
            )
        );

        //====================================
        //CREAR AUTENTICACIÓN
        //====================================

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    idUsuario,
                    null,
                    authorities
                );

        //====================================
        //GUARDAR AUTENTICACIÓN
        //====================================

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        //====================================
        //CONTINUAR SOLICITUD
        //====================================

        filterChain.doFilter(request, response);
    }

    //========================================
    //RESPUESTA DE AUTENTICACIÓN RECHAZADA
    //========================================

    private void responderNoAutorizado(
            HttpServletResponse response
    ) throws IOException {

        //====================================
        //LIMPIAR CONTEXTO DE SEGURIDAD
        //====================================

        SecurityContextHolder.clearContext();

        //====================================
        //CONFIGURAR RESPUESTA HTTP 401
        //====================================

        response.setStatus(
            HttpServletResponse.SC_UNAUTHORIZED
        );

        response.setContentType("application/json");

        response.setCharacterEncoding(
            StandardCharsets.UTF_8.name()
        );

        //====================================
        //DEVOLVER MENSAJE
        //====================================

        response.getWriter().write(
            "{\"message\":\"Sesión inválida o expirada\"}"
        );
    }
}
