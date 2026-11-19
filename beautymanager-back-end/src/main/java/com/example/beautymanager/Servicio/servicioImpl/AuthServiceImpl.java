package com.example.beautymanager.Servicio.servicioImpl;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.DTO.LoginDTO;
import com.example.beautymanager.Modelo.DTO.LoginResponseDTO;
import com.example.beautymanager.Modelo.DTO.ResponseDTO;
import com.example.beautymanager.Modelo.DTO.UsuarioAutenticadoDTO;
import com.example.beautymanager.Modelo.Entidad.EmpleadoEntity;
import com.example.beautymanager.Modelo.Entidad.TokenRevocadoEntity;
import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;
import com.example.beautymanager.Modelo.Enums.EstadoEmpleadoEnums;
import com.example.beautymanager.Modelo.Enums.RolesEnums;
import com.example.beautymanager.Repositorio.EmpleadoRepository;
import com.example.beautymanager.Repositorio.TokenRevocadoRepository;
import com.example.beautymanager.Repositorio.UsuarioRepository;
import com.example.beautymanager.Servicio.AuthService;
import com.example.beautymanager.Servicio.JWTUtilityService;
import com.example.beautymanager.exception.BusinessException;

@Service
public class AuthServiceImpl implements AuthService {

    //=======================
    //Logger
    //=======================
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    //=======================
    //Dependencias
    //=======================
    private final UsuarioRepository usuarioRepository;
    private final JWTUtilityService jwtUtilityService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenRevocadoRepository tokenRevocadoRepository;
    private final EmpleadoRepository empleadoRepository;

    public AuthServiceImpl(UsuarioRepository usuarioRepository, JWTUtilityService jwtUtilityService, BCryptPasswordEncoder passwordEncoder, TokenRevocadoRepository tokenRevocadoRepository, EmpleadoRepository empleadoRepository){

        this.usuarioRepository = usuarioRepository;

        this.jwtUtilityService = jwtUtilityService;

        this.passwordEncoder = passwordEncoder;

        this.tokenRevocadoRepository = tokenRevocadoRepository;

        this.empleadoRepository = empleadoRepository;
    }

    //=======================
    //Login
    //=======================
    @Override
    public LoginResponseDTO login(LoginDTO login){

        logger.info("Intentando iniciar sesión");

        if(login == null){
            throw new BusinessException("Los datos de login son obligatorios");
        }

        if(login.getEmail() == null || login.getEmail().isBlank()){
            throw new BusinessException("El email es obligatorio");
        }

        if(login.getPassword() == null || login.getPassword().isBlank()){
            throw new BusinessException("La contraseña es obligatoria");
        }

        String email = login.getEmail().trim().toLowerCase();

        UsuarioEntity usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Email o contraseña incorrectos"));

        if(!usuario.isEnabled()){
            throw new BusinessException("La cuenta se encuentra deshabilitada");
        }

        if(!passwordEncoder.matches(login.getPassword(), usuario.getPassword())){
            throw new BusinessException("Email o contraseña incorrectos");
        }

        RolesEnums rol = usuario.getRol().getNombre();

        //========================================
        //VALIDAR ESTADO DE LA EMPLEADA
        //========================================

        if (rol == RolesEnums.EMPLEADO) {

            //====================================
            //OBTENER REGISTRO DE EMPLEADA
            //====================================

            EmpleadoEntity empleado = empleadoRepository
                    .findByUsuario_Id(usuario.getId())
                    .orElseThrow(() -> new BusinessException(
                        "La cuenta de empleada no está disponible"
                    ));

            //====================================
            //COMPROBAR ESTADO LABORAL
            //====================================

            if (
                empleado.getEstadoEmpleado() !=
                    EstadoEmpleadoEnums.ACTIVO
            ) {

                throw new BusinessException(
                    "La cuenta de empleada no se encuentra activa"
                );
            }
        }

        //========================================
        //GENERAR TOKEN
        //========================================

        String token;

        try{
            token = jwtUtilityService.generateJWT(usuario.getId(), rol);
        } catch (Exception e) {

            logger.error("Error generando JWT", e);

            throw new BusinessException("No fue posible generar el token de acceso");
        }

        LoginResponseDTO response = new LoginResponseDTO();

        response.setMessage("Inicio de sesión exitoso");
        response.setToken(token);
        response.setIdUsuario(usuario.getId());
        response.setNombre(usuario.getNombre());
        response.setApellido(usuario.getApellido());
        response.setEmail(usuario.getEmail());
        response.setRol(rol);
        response.setSuccess(true);

        logger.info("Login exitoso para usuario {}", usuario.getId());

        return response;
    }

    //=======================
    //Logout
    //=======================
    @Override
public ResponseDTO logout(String token) {

    logger.info("Cerrando sesión");

    try {

        String jwtId = jwtUtilityService.getJwtId(token);

        if (jwtUtilityService.isTokenRevocado(jwtId)) {
            throw new BusinessException("El token ya fue revocado");
        }

        LocalDateTime fechaExpiracion = jwtUtilityService
                .getFechaExpiracion(token);

        TokenRevocadoEntity tokenRevocado = new TokenRevocadoEntity();

        tokenRevocado.setJwtId(jwtId);
        tokenRevocado.setFechaExpiracion(fechaExpiracion);

        tokenRevocadoRepository.save(tokenRevocado);

        ResponseDTO response = new ResponseDTO();

        response.setSuccess(true);
        response.setMensaje("Sesión cerrada correctamente");
        response.setNumOfErrors(0);

        return response;

    } catch (BusinessException e) {

        throw e;

    } catch (Exception e) {

        logger.error("Error al cerrar sesión", e);

        throw new BusinessException("No fue posible cerrar la sesión");
    }
}

    //===============================
    //Obtener usuario autenticado
    //===============================
    @Override
    public UsuarioAutenticadoDTO obtenerUsuarioPorId(Long idUsuario){
        
        logger.info("Obteniendo usuario por ID: {}", idUsuario);

        if(idUsuario == null){
            throw new BusinessException("El ID del usuario es obligatorio");
        }

        UsuarioEntity usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
        //=========================
        //Armar DTO de respuesta
        //=========================
        UsuarioAutenticadoDTO usuarioAutenticadoDTO = new UsuarioAutenticadoDTO();

        usuarioAutenticadoDTO.setIdUsuario(usuario.getId());
        usuarioAutenticadoDTO.setNombre(usuario.getNombre());
        usuarioAutenticadoDTO.setApellido(usuario.getApellido());
        usuarioAutenticadoDTO.setEmail(usuario.getEmail());
        usuarioAutenticadoDTO.setRol(usuario.getRol().getNombre());

        logger.info("Usuario autenticado obtenido correctamente: {}", usuario.getId());

        return usuarioAutenticadoDTO;
    }

    @Override
    public ResponseDTO mensajeSoloAdmin() {

        ResponseDTO response = new ResponseDTO();

        response.setSuccess(true);
        response.setNumOfErrors(0);
        response.setMensaje("Acceso permitido. Usuario con rol ADMIN");

        return response;
    }
}
