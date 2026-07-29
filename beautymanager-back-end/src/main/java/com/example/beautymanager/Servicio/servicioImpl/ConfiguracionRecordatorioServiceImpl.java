package com.example.beautymanager.Servicio.servicioImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.DTO.ActualizarConfiguracionRecordatorioDTO;
import com.example.beautymanager.Modelo.DTO.CrearConfiguracionRecordatorioDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerConfiguracionRecordatorioDTO;
import com.example.beautymanager.Modelo.Entidad.ConfiguracionRecordatorioEntity;
import com.example.beautymanager.Repositorio.ConfiguracionRecordatorioRepository;
import com.example.beautymanager.Servicio.ConfiguracionRecordatorioService;
import com.example.beautymanager.exception.BusinessException;

@Service
public class ConfiguracionRecordatorioServiceImpl implements ConfiguracionRecordatorioService{

    @Autowired
    private ConfiguracionRecordatorioRepository configuracionRepository;

    //=======================
    //Crear Configuracion
    //=======================
    @Override
    public ObtenerConfiguracionRecordatorioDTO crearConfiguracion(CrearConfiguracionRecordatorioDTO crearConfiguracion){

        if(crearConfiguracion == null){
            throw new BusinessException("La configuracion no puede ser nula");
        }

        if(configuracionRepository.findFirstByOrderByIdConfiguracionAsc().isPresent()){
            throw new BusinessException("La configuracion de recordatorios ya existe");
        }

        validarDTO(crearConfiguracion.getEstadoEmail(), crearConfiguracion.getEstadoWhatsapp(), crearConfiguracion.getEstadoNotificacionInterna(), crearConfiguracion.getPrimerRecordatorioHoras(), crearConfiguracion.getSegundoRecordatorioHoras());

        ConfiguracionRecordatorioEntity configuracion = new ConfiguracionRecordatorioEntity();

        configuracion.setEstadoEmail(crearConfiguracion.getEstadoEmail());
        configuracion.setEstadoWhatsapp(crearConfiguracion.getEstadoWhatsapp());
        configuracion.setEstadoNotificacionInterna(crearConfiguracion.getEstadoNotificacionInterna());
        configuracion.setPrimerRecordatorioHoras(crearConfiguracion.getPrimerRecordatorioHoras());
        configuracion.setSegundoRecordatorioHoras(crearConfiguracion.getSegundoRecordatorioHoras());

        return toMap(configuracionRepository.save(configuracion));
    }

    //=======================
    //Obtener Configuracion
    //=======================
    @Override
    public ObtenerConfiguracionRecordatorioDTO obtenerConfiguracion(){

        ConfiguracionRecordatorioEntity configuracion = configuracionRepository.findFirstByOrderByIdConfiguracionAsc()
                .orElseThrow(() -> new BusinessException("No existe configuracion de recordatorios"));

        return toMap(configuracion);
    }

    //===========================
    //Actualizar Configuracion
    //===========================
    @Override
    public ObtenerConfiguracionRecordatorioDTO actualizarConfiguracion(ActualizarConfiguracionRecordatorioDTO actualizarConfiguracion){

        if(actualizarConfiguracion == null){
            throw new BusinessException("Los datos de actualizacion son obligatorios");
        }

        ConfiguracionRecordatorioEntity configuracion = configuracionRepository.findFirstByOrderByIdConfiguracionAsc()
                .orElseThrow(() -> new BusinessException("No existe configuracion de recordatorios"));

        if(actualizarConfiguracion.getEstadoEmail() != null){
            configuracion.setEstadoEmail(actualizarConfiguracion.getEstadoEmail());
        }

        if(actualizarConfiguracion.getEstadoWhatsapp() != null){
            configuracion.setEstadoWhatsapp(actualizarConfiguracion.getEstadoWhatsapp());
        }

        if(actualizarConfiguracion.getEstadoNotificacionInterna() != null){
            configuracion.setEstadoNotificacionInterna(actualizarConfiguracion.getEstadoNotificacionInterna());
        }

        if(actualizarConfiguracion.getPrimerRecordatorioHoras() != null){
            configuracion.setPrimerRecordatorioHoras(actualizarConfiguracion.getPrimerRecordatorioHoras());
        }

        if(actualizarConfiguracion.getSegundoRecordatorioHoras() != null){
            configuracion.setSegundoRecordatorioHoras(actualizarConfiguracion.getSegundoRecordatorioHoras());
        }

        validarEntidad(configuracion);

        return toMap(configuracionRepository.save(configuracion));
    }

    //=======================
    //Validaciones
    //=======================
    private void validarDTO(Object email, Object whatsapp, Object notificacionInterna, Integer primerRecordatorio, Integer segundoRecordatorio){

        if(email == null || whatsapp == null || notificacionInterna == null){
            throw new BusinessException("Debe definir todos los metodos de notificacion");
        }

        if(primerRecordatorio == null || segundoRecordatorio == null){
            throw new BusinessException("Debe definir los tiempos de los recordatorios");
        }

        if(primerRecordatorio <= 0 || segundoRecordatorio <= 0){
            throw new BusinessException("Los tiempos deben ser mayores a 0");
        }

        if(primerRecordatorio <= segundoRecordatorio){
            throw new BusinessException("El primer recordatorio debe ser mayor al segundo recordatorio");
        }
    }

    private void validarEntidad(ConfiguracionRecordatorioEntity configuracion){

        if(configuracion.getPrimerRecordatorioHoras() == null || configuracion.getSegundoRecordatorioHoras() == null){
            throw new BusinessException("Los recordatorios no pueden ser nulos");
        }

        if(configuracion.getPrimerRecordatorioHoras() <= 0 || configuracion.getSegundoRecordatorioHoras() <= 0){
            throw new BusinessException("Los recordatorios deben ser mayores a 0");
        }

        if(configuracion.getPrimerRecordatorioHoras() <= configuracion.getSegundoRecordatorioHoras()){
            throw new BusinessException("El primer recordatorio debe ser mayor al segundo recordatorio");
        }
    }

    @Override
    public ObtenerConfiguracionRecordatorioDTO toMap(ConfiguracionRecordatorioEntity configuracion) {

        ObtenerConfiguracionRecordatorioDTO obtenerConfiguracionDTO = new ObtenerConfiguracionRecordatorioDTO();

        obtenerConfiguracionDTO.setIdConfiguracion(configuracion.getIdConfiguracion());
        obtenerConfiguracionDTO.setEstadoEmail(configuracion.getEstadoEmail());
        obtenerConfiguracionDTO.setEstadoWhatsapp(configuracion.getEstadoWhatsapp());
        obtenerConfiguracionDTO.setEstadoNotificacionInterna(configuracion.getEstadoNotificacionInterna());
        obtenerConfiguracionDTO.setPrimerRecordatorioHoras(configuracion.getPrimerRecordatorioHoras());
        obtenerConfiguracionDTO.setSegundoRecordatorioHoras(configuracion.getSegundoRecordatorioHoras());

        return obtenerConfiguracionDTO;
    }

}
