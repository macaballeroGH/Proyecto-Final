package com.example.beautymanager.Servicio.servicioImpl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.DTO.RecuperacionPasswordResponseDTO;
import com.example.beautymanager.Modelo.DTO.ResponseDTO;
import com.example.beautymanager.Modelo.DTO.RestablecerPasswordDTO;
import com.example.beautymanager.Modelo.DTO.SolicitarRecuperacionPasswordDTO;
import com.example.beautymanager.Modelo.Entidad.RecuperacionPasswordEntity;
import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;
import com.example.beautymanager.Repositorio.RecuperacionPasswordRepository;
import com.example.beautymanager.Repositorio.UsuarioRepository;
import com.example.beautymanager.Servicio.RecuperacionPasswordService;
import com.example.beautymanager.exception.BusinessException;

@Service
public class RecuperacionPasswordServiceImpl implements RecuperacionPasswordService {

    @Autowired
    private RecuperacionPasswordRepository recuperacionPasswordRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //========================================
    //Solicitar recuperacion de contraseña
    //========================================
    @Override
    public RecuperacionPasswordResponseDTO solicitarRecuperacionPassword(SolicitarRecuperacionPasswordDTO solicitarRecuperacionPassword){
        
        UsuarioEntity usuario = usuarioRepository.findByEmail(solicitarRecuperacionPassword.getEmail())
                .orElseThrow(() -> new BusinessException("No existe una cuenta asociada a ese email"));

        String token = UUID.randomUUID().toString();

        RecuperacionPasswordEntity recuperacion = new RecuperacionPasswordEntity();

        recuperacion.setToken(token);
        recuperacion.setUsuario(usuario);
        recuperacion.setFechaExpiracion(LocalDateTime.now().plusMinutes(15));
        recuperacion.setUtilizado(false);

        recuperacionPasswordRepository.save(recuperacion);

        //Simulacion del envio de correo
        String linkRecuperacion = "http://localhost:4200/reset-password?token=" + token;

        RecuperacionPasswordResponseDTO response = new RecuperacionPasswordResponseDTO();

        response.setMensaje("Se generó el enlace de recuperación correctamente.");
        response.setLinkRecuperacion(linkRecuperacion);

        return response;
    }

    //=========================
    //Restablecer contraseña
    //=========================
    @Override
    public ResponseDTO restablecerPassword(RestablecerPasswordDTO restablecerPassword){
        
        RecuperacionPasswordEntity recuperacion = recuperacionPasswordRepository.findByToken(restablecerPassword.getToken())
                .orElseThrow(() -> new BusinessException("El enlace de recuperación es invalido"));

        if(Boolean.TRUE.equals(recuperacion.getUtilizado())){
            throw new BusinessException("El enlace de recuperación ya fue utilizado");
        }

        if(recuperacion.getFechaExpiracion().isBefore(LocalDateTime.now())){
            throw new BusinessException("El enlace de recuperacion ha expirado");
        }

        if(!restablecerPassword.getPasswordNueva().equals(restablecerPassword.getConfirmarPasswordNueva())){
            throw new BusinessException("Las contraseñas nuevas no coinciden.");
        }

        UsuarioEntity usuario = recuperacion.getUsuario();

        usuario.setPassword(passwordEncoder.encode(restablecerPassword.getPasswordNueva()));

        usuarioRepository.save(usuario);

        recuperacion.setUtilizado(true);

        recuperacionPasswordRepository.save(recuperacion);

        ResponseDTO response = new ResponseDTO();

        response.setNumOfErrors(0);
        response.setMensaje("Contraseña actualizada correctamente.");
        response.setSuccess(true);

        return response;
    }
}
