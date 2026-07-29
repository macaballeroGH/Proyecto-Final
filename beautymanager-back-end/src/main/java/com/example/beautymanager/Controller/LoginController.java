package com.example.beautymanager.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.beautymanager.Modelo.DTO.LoginDTO;
import com.example.beautymanager.Modelo.DTO.LoginResponseDTO;
import com.example.beautymanager.Modelo.DTO.RecuperacionPasswordResponseDTO;
import com.example.beautymanager.Modelo.DTO.ResponseDTO;
import com.example.beautymanager.Modelo.DTO.RestablecerPasswordDTO;
import com.example.beautymanager.Modelo.DTO.SolicitarRecuperacionPasswordDTO;
import com.example.beautymanager.Modelo.DTO.UsuarioAutenticadoDTO;
import com.example.beautymanager.Servicio.AuthService;
import com.example.beautymanager.Servicio.RecuperacionPasswordService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private AuthService authService;

    @Autowired
    private RecuperacionPasswordService recuperacionPasswordService;

   //========================
   //Login
   //========================
   @PostMapping("/login")
   public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO loginRequest){

    LoginResponseDTO response = authService.login(loginRequest);

    return ResponseEntity.ok(response);
   }

   //========================================
   //Informacion del usuario autenticado
   //========================================
   @PreAuthorize("isAuthenticated()")
   @GetMapping("/me")
   public ResponseEntity<UsuarioAutenticadoDTO> getUsuarioAutenticado(){

    Long userId = (Long) SecurityContextHolder

            .getContext()
            .getAuthentication()
            .getPrincipal();

    return ResponseEntity.ok(authService.obtenerUsuarioPorId(userId));
   }

   //========================
   //Endpoint solo Admin
   //========================
   @GetMapping("/admin")
   @PreAuthorize("hasRole('ADMIN')")
   public ResponseEntity<ResponseDTO> soloAdmin(){

    return ResponseEntity.ok(authService.mensajeSoloAdmin());
   }

   //========================
   //Logout
   //========================
   @PostMapping("/logout")
   @PreAuthorize("isAuthenticated()")
   public ResponseEntity<ResponseDTO> logout(HttpServletRequest request){

    return ResponseEntity.ok(authService.logout());
    }

    //========================================
    //Solicitar recuperacion de contraseña
    //========================================
    @PostMapping("/forgot-password")
    public ResponseEntity<RecuperacionPasswordResponseDTO> solicitarRecuperacionPassword(@RequestBody SolicitarRecuperacionPasswordDTO solicitarRecuperacionPassword){
        
        return ResponseEntity.ok(recuperacionPasswordService.solicitarRecuperacionPassword(solicitarRecuperacionPassword));
    }

    //=========================
    //Restablecer contraseña
    //=========================
    @PostMapping("reset-password")
    public ResponseEntity<ResponseDTO> restablecerPassword(@RequestBody RestablecerPasswordDTO restablecerPassword){

        return ResponseEntity.ok(recuperacionPasswordService.restablecerPassword(restablecerPassword));
    }
}
