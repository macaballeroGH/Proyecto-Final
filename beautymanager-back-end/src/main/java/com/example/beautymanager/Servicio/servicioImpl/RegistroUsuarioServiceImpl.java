package com.example.beautymanager.Servicio.servicioImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.DTO.InicializarClienteDTO;
import com.example.beautymanager.Modelo.DTO.RegistroDTO;
import com.example.beautymanager.Modelo.DTO.ResponseDTO;
import com.example.beautymanager.Modelo.Entidad.RolEntity;
import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;
import com.example.beautymanager.Modelo.Enums.EstadoUsuarioEnums;
import com.example.beautymanager.Modelo.Enums.RolesEnums;
import com.example.beautymanager.Repositorio.RolRepository;
import com.example.beautymanager.Repositorio.UsuarioRepository;
import com.example.beautymanager.Servicio.ClienteInicializacionService;
import com.example.beautymanager.Servicio.RegistroUsuarioService;
import com.example.beautymanager.exception.BusinessException;

import jakarta.transaction.Transactional;

@Service
public class RegistroUsuarioServiceImpl implements RegistroUsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteInicializacionService clienteInicializacionService;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ResponseDTO registrarUsuario(RegistroDTO usuarioDTO){

        if (usuarioDTO == null) {
            throw new BusinessException("Los datos del usuario son obligatorios");
        }

        ResponseDTO response = new ResponseDTO();

        //==================
        //Validaciones
        //==================
        validarDatos(usuarioDTO);

        //==================
        //Crear Usuario
        //==================
        UsuarioEntity nuevoUsuario = new UsuarioEntity();

        nuevoUsuario.setNombre(usuarioDTO.getNombre() != null ? usuarioDTO.getNombre().trim() : null);
        nuevoUsuario.setApellido(usuarioDTO.getApellido() != null ? usuarioDTO.getApellido().trim() : null);
        nuevoUsuario.setEmail(usuarioDTO.getEmail() != null ? usuarioDTO.getEmail().trim().toLowerCase() : null);
        nuevoUsuario.setTelefono(usuarioDTO.getTelefono() != null ? usuarioDTO.getTelefono().trim() : null);
        nuevoUsuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));

        //=================
        //Rol
        //=================
        RolEntity rol = rolRepository.findByNombre(RolesEnums.CLIENTE)
                .orElseThrow(() -> new BusinessException("Rol no encontrado"));

        nuevoUsuario.setRol(rol);

        //=================
        //Estado
        //=================
        nuevoUsuario.setEstadoUsuario(EstadoUsuarioEnums.ACTIVO);

        //=================
        //Guardar Usuario
        //=================
        usuarioRepository.save(nuevoUsuario);

        //===================
        //Crear cliente
        //===================
        InicializarClienteDTO inicializarCliente = new InicializarClienteDTO();
        inicializarCliente.setIdUsuario(nuevoUsuario.getId());
        inicializarCliente.setDireccion(usuarioDTO.getDireccion());
        inicializarCliente.setFechaNacimiento(usuarioDTO.getFechaNacimiento());

        clienteInicializacionService.inicializarCliente(inicializarCliente);

        //==============
        //Respuesta
        //==============
        response.setMensaje("Usuario registrado correctamente");
        response.setSuccess(true);

        return response;

    }
    //=======================
    //Metodo de Validacion
    //=======================
    private void validarDatos(RegistroDTO dto){
        if (dto == null) {
            throw new BusinessException("Datos de registro invalidos");
        }

        if (dto.getEmail() == null || dto.getEmail().isBlank()){
            throw new BusinessException("Email obligatorio");
        }

        String email = dto.getEmail().trim().toLowerCase();
        if (usuarioRepository.existsByEmail(email)){
            throw new BusinessException("El email ya existe");
        }

        if (dto.getPassword() == null || dto.getPassword().isBlank()){
            throw new BusinessException("Password obligatorio");
        }
    }
}