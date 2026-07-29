package com.example.beautymanager.Servicio.servicioImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.DTO.ObtenerAuditoriaDTO;
import com.example.beautymanager.Modelo.Entidad.AuditoriaEntity;
import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;
import com.example.beautymanager.Repositorio.AuditoriaRepository;
import com.example.beautymanager.Servicio.AuditoriaService;
import com.example.beautymanager.exception.BusinessException;

@Service
public class AuditoriaServiceImpl implements AuditoriaService {

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    //=======================
    //Guardar auditoria
    //=======================
    @Override
    public ObtenerAuditoriaDTO guardarAuditoria(String accion, String tablaAfectada, Long idRegistro, UsuarioEntity usuario){

        if(accion == null || accion.isBlank()){
            throw new BusinessException("La accion es obligatoria");
        }

        if(tablaAfectada == null || tablaAfectada.isBlank()){
            throw new BusinessException("La tabla afectada es obligatoria");
        }

        if(usuario == null){
            throw new BusinessException("El usuario es obligatorio");
        }

        AuditoriaEntity auditoria = new AuditoriaEntity();

        auditoria.setAccion(accion.trim());
        auditoria.setTablaAfectada(tablaAfectada.trim());
        auditoria.setIdRegistro(idRegistro);
        auditoria.setUsuario(usuario);

        return toMap(auditoriaRepository.save(auditoria));
    }

    //=======================
    //Obtener por ID
    //=======================
    @Override
    public ObtenerAuditoriaDTO obtenerPorId(Long idAuditoria){

        if(idAuditoria == null || idAuditoria <= 0){
            throw new BusinessException("ID de auditoria invalido");
        }

        return toMap(auditoriaRepository.findById(idAuditoria)
                        .orElseThrow(() -> new BusinessException("Auditoria no encontrada")));
    }

    //=======================
    //Obtener todas
    //=======================
    @Override
    public List<ObtenerAuditoriaDTO> obtenerTodas(){
        return auditoriaRepository.findAll().stream().map(this::toMap).toList();
    }

    //=======================
    //Obtener por usuario
    //=======================
    @Override
    public List<ObtenerAuditoriaDTO> obtenerPorUsuario(Long idUsuario){

        if(idUsuario == null || idUsuario <= 0){
            throw new BusinessException("ID de usuario invalido");
        }

        return auditoriaRepository.findByUsuarioId(idUsuario).stream().map(this::toMap).toList();
    }

    @Override
    public ObtenerAuditoriaDTO toMap(AuditoriaEntity auditoria) {

        ObtenerAuditoriaDTO obtenerAuditoriaDTO = new ObtenerAuditoriaDTO();

        obtenerAuditoriaDTO.setIdAuditoria(auditoria.getId());
        obtenerAuditoriaDTO.setAccion(auditoria.getAccion());
        obtenerAuditoriaDTO.setTablaAfectada(auditoria.getTablaAfectada());
        obtenerAuditoriaDTO.setIdRegistro(auditoria.getIdRegistro());
        obtenerAuditoriaDTO.setDetalle(auditoria.getDetalle());
        obtenerAuditoriaDTO.setFechaHora(auditoria.getFechaHora());

        if(auditoria.getUsuario() != null){

            obtenerAuditoriaDTO.setIdUsuario(auditoria.getUsuario().getId());
            obtenerAuditoriaDTO.setNombreUsuario(auditoria.getUsuario().getNombre());
            obtenerAuditoriaDTO.setApellidoUsuario(auditoria.getUsuario().getApellido());
            obtenerAuditoriaDTO.setEmailUsuario(auditoria.getUsuario().getEmail());
        }

        return obtenerAuditoriaDTO;
    }
}
