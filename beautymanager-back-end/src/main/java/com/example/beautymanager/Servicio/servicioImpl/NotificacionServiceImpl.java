package com.example.beautymanager.Servicio.servicioImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.DTO.ActualizarNotificacionDTO;
import com.example.beautymanager.Modelo.DTO.CrearNotificacionDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerNotificacionDTO;
import com.example.beautymanager.Modelo.Entidad.NotificacionEntity;
import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;
import com.example.beautymanager.Repositorio.NotificacionRepository;
import com.example.beautymanager.Repositorio.UsuarioRepository;
import com.example.beautymanager.Servicio.NotificacionService;
import com.example.beautymanager.exception.BusinessException;

@Service
public class NotificacionServiceImpl implements NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    //=======================
    //Crear Notificacion
    //=======================
    @Override
    public ObtenerNotificacionDTO crearNotificacion(CrearNotificacionDTO crearNotificacion) {

        if(crearNotificacion.getIdUsuario() == null || crearNotificacion.getIdUsuario() <= 0) {
            throw new BusinessException("ID de usuario inválido");
        }

        if(crearNotificacion.getMensaje() == null || crearNotificacion.getMensaje().isBlank()) {
            throw new BusinessException("El mensaje es obligatorio");
        }

        UsuarioEntity usuario = usuarioRepository.findById(crearNotificacion.getIdUsuario())
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        NotificacionEntity notificacion = new NotificacionEntity();

        notificacion.setUsuario(usuario);
        notificacion.setMensaje(crearNotificacion.getMensaje().trim());
        notificacion.setLeida(false);
        notificacion.setFechaCreacion(LocalDateTime.now());

        return toMap(notificacionRepository.save(notificacion));
    }

    //============================
    //Actualizar notificacion
    //============================
    @Override
    public ObtenerNotificacionDTO actualizarNotificacion(Long idUsuario, Long idNotificacion, ActualizarNotificacionDTO actualizarNotificacion){
        
        UsuarioEntity usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        NotificacionEntity notificacion = notificacionRepository.findById(idNotificacion)
                .orElseThrow(() -> new BusinessException("Notificacion no encontrada"));

        if(!notificacion.getUsuario().getId().equals(usuario.getId())){
            throw new BusinessException("No tiene permisos para modificar esta notificacion");
        }

        if(actualizarNotificacion.getLeida() != null){
            notificacion.setLeida(actualizarNotificacion.getLeida());
        }

        return toMap(notificacionRepository.save(notificacion));
    }

    //=======================
    //Buscar por ID
    //=======================
    @Override
    public ObtenerNotificacionDTO buscarPorId(Long idNotificacion){
        return toMap(notificacionRepository.findById(idNotificacion)
                    .orElseThrow(() -> new BusinessException("Notificacion no encontrada")));
    }

    //=======================
    //Listar todas
    //=======================
    @Override
    public List<ObtenerNotificacionDTO> listarTodas(){
        return notificacionRepository.findAll().stream().map(this::toMap).toList();
    }

    //=======================
    //Listar por usuario
    //=======================
    @Override
    public List<ObtenerNotificacionDTO> listarPorUsuario(Long idUsuario){
        
        usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        return notificacionRepository.findByUsuarioId(idUsuario).stream().map(this::toMap).toList();
    }

    //=======================
    //Listar no leida
    //=======================
    @Override
    public List<ObtenerNotificacionDTO> listarNoLeidas(Long idUsuario){

        usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        return notificacionRepository.findByUsuarioIdAndLeidaFalse(idUsuario).stream().map(this::toMap).toList();
    }

    //=======================
    //Eliminar
    //=======================
    @Override
    public void eliminar(Long idNotificacion){
        
        NotificacionEntity notificacion = notificacionRepository.findById(idNotificacion)
                .orElseThrow(() -> new BusinessException("Notificacion no encontrada"));

        notificacionRepository.delete(notificacion);
    }

    @Override
    public ObtenerNotificacionDTO toMap(NotificacionEntity notificacion) {

        ObtenerNotificacionDTO obtenerNotificacionDTO = new ObtenerNotificacionDTO();

        obtenerNotificacionDTO.setIdNotificacion(notificacion.getId());
        obtenerNotificacionDTO.setMensaje(notificacion.getMensaje());
        obtenerNotificacionDTO.setLeida(notificacion.getLeida());
        obtenerNotificacionDTO.setFechaCreacion(notificacion.getFechaCreacion());

        if(notificacion.getUsuario() != null){

            obtenerNotificacionDTO.setIdUsuario(notificacion.getUsuario().getId());
            obtenerNotificacionDTO.setNombreUsuario(notificacion.getUsuario().getNombre());
            obtenerNotificacionDTO.setApellidoUsuario(notificacion.getUsuario().getApellido());
            obtenerNotificacionDTO.setNombreCompletoUsuario(notificacion.getUsuario().getNombre() + " " + notificacion.getUsuario().getApellido());
            obtenerNotificacionDTO.setEmailUsuario(notificacion.getUsuario().getEmail());
        }

        return obtenerNotificacionDTO;
    }
}
