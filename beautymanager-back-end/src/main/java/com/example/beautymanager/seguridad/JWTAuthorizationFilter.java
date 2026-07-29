package com.example.beautymanager.seguridad;

import java.io.IOException;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.beautymanager.Servicio.JWTUtilityService;
import com.example.beautymanager.exception.BusinessException;
import com.nimbusds.jwt.JWTClaimsSet;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JWTAuthorizationFilter.class);

    private final JWTUtilityService jwtUtilityService;

    public JWTAuthorizationFilter(JWTUtilityService jwtUtilityService){

        this.jwtUtilityService = jwtUtilityService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{

        String header = request.getHeader("Authorization");

        log.info("Authorization Header: {}", header);

        if(header == null || !header.startsWith("Bearer ")){

            filterChain.doFilter(request, response);

            return;
        }

        String token = header.substring(7);

        log.info("Token recibido correctamente");

        try{

            JWTClaimsSet claims = jwtUtilityService.parseJWT(token);

            log.info("Token valido correctamente");

            log.info("Claims: {}", claims.toJSONObject());

            String subject = claims.getSubject();

            if(subject == null || subject.isBlank()){
                throw new BusinessException("Subject invalido");
            }

            Long userId = Long.parseLong(subject);

            String rol = claims.getStringClaim("rol");

            log.info("SUB del token: {}", subject);
            log.info("ROL del token: {}", rol);

            log.info("Rol obtenido: {}", rol);

            if(rol == null){
                throw new BusinessException("Rol no presente en el token");
            }

            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + rol));

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userId, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(auth);

            log.info("Usuario autenticado correctamente");
        } catch (Exception e) {

            log.error("Error validando JWT", e);
            
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }
}
