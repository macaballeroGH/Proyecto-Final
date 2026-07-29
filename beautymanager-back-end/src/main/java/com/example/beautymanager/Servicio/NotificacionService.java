package com.example.beautymanager.Servicio;

import java.util.List;

import com.example.beautymanager.Modelo.DTO.ActualizarNotificacionDTO;
import com.example.beautymanager.Modelo.DTO.CrearNotificacionDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerNotificacionDTO;
import com.example.beautymanager.Modelo.Entidad.NotificacionEntity;

public interface NotificacionService {
    
   ObtenerNotificacionDTO toMap(NotificacionEntity notificacion);

   ObtenerNotificacionDTO crearNotificacion(CrearNotificacionDTO crearNotificacionDTO);

   ObtenerNotificacionDTO actualizarNotificacion(Long idUsuario, Long idNotificacion, ActualizarNotificacionDTO actualizarNotificacion);

   ObtenerNotificacionDTO buscarPorId(Long idNotificacion);

   List<ObtenerNotificacionDTO> listarTodas();

   List<ObtenerNotificacionDTO> listarPorUsuario(Long idUsuario);

   List<ObtenerNotificacionDTO> listarNoLeidas(Long idUsuario);

   void eliminar(Long idNotificacion);
}
