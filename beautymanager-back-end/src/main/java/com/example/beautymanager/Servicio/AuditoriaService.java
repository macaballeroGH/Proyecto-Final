package com.example.beautymanager.Servicio;

import java.util.List;

import com.example.beautymanager.Modelo.DTO.ObtenerAuditoriaDTO;
import com.example.beautymanager.Modelo.Entidad.AuditoriaEntity;
import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;

public interface AuditoriaService {

    ObtenerAuditoriaDTO toMap(AuditoriaEntity auditoria);

    ObtenerAuditoriaDTO guardarAuditoria(String accion, String tablaAfectada, Long idRegistro, UsuarioEntity usuario);

    ObtenerAuditoriaDTO obtenerPorId(Long idAuditoria);

    List<ObtenerAuditoriaDTO> obtenerTodas();

    List<ObtenerAuditoriaDTO> obtenerPorUsuario(Long idUsuario);
}
