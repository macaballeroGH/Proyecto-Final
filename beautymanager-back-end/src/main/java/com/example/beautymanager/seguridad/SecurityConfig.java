package com.example.beautymanager.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.example.beautymanager.Servicio.JWTUtilityService;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JWTAuthorizationFilter jwtAuthorizationFilter;

    public SecurityConfig(JWTUtilityService jwtUtilityService){
        this.jwtAuthorizationFilter = new JWTAuthorizationFilter(jwtUtilityService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(authRequest ->
                    authRequest
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/auth/logout").authenticated()
                        .requestMatchers("/auth/forgot-password").permitAll()
                        .requestMatchers("/auth/reset-password").permitAll()
                        .requestMatchers("/registro/nuevo").permitAll()

                        .requestMatchers("/usuario/existe-email/**").permitAll()

                        .requestMatchers("/servicio/id/**").permitAll()
                        .requestMatchers("/servicio/listar-activos").permitAll()
                        .requestMatchers("/servicio/nombre/**").permitAll()
                        .requestMatchers("/servicio/descripcion/**").permitAll()
                        .requestMatchers("/servicio/categoria/**").permitAll()
                        .requestMatchers("/servicio/categoria-activa/**").permitAll()
                        .requestMatchers("/servicio/precio-rango/**").permitAll()
                        .requestMatchers("/servicio/precio-max/**").permitAll()
                        .requestMatchers("/servicio/precio-min/**").permitAll()
                        .requestMatchers("/servicio/duracion/**").permitAll()
                        .requestMatchers("/servicio/duracion-max/**").permitAll()
                        .requestMatchers("/servicio/duracion-min/**").permitAll()
                        .requestMatchers("/servicio/duracion-rango/**").permitAll()

                        .requestMatchers("/producto/id/**").permitAll()
                        .requestMatchers("/producto/lista").permitAll()

                        .requestMatchers("/uploads/**").permitAll()

                        .anyRequest().authenticated()
                )

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling(exception ->

                    exception 
                        .authenticationEntryPoint((request, response, authException) ->
                            response.sendError(
                                HttpServletResponse.SC_UNAUTHORIZED, "No autorizado"))

                        .accessDeniedHandler((request, response, accessDeniedException) ->
                            response.sendError(
                                HttpServletResponse.SC_FORBIDDEN, "Acceso denegado"))
                )
                .build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(12);
    }
}
