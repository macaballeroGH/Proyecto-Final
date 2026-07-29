package com.example.beautymanager.Servicio;

import com.example.beautymanager.Modelo.DTO.ActualizarConfiguracionRecordatorioDTO;
import com.example.beautymanager.Modelo.DTO.CrearConfiguracionRecordatorioDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerConfiguracionRecordatorioDTO;
import com.example.beautymanager.Modelo.Entidad.ConfiguracionRecordatorioEntity;

public interface ConfiguracionRecordatorioService {

    ObtenerConfiguracionRecordatorioDTO toMap(ConfiguracionRecordatorioEntity configuracion);

    ObtenerConfiguracionRecordatorioDTO crearConfiguracion(CrearConfiguracionRecordatorioDTO crearConfiguracion);

    ObtenerConfiguracionRecordatorioDTO obtenerConfiguracion();

    ObtenerConfiguracionRecordatorioDTO actualizarConfiguracion(ActualizarConfiguracionRecordatorioDTO actualizarConfiguracion);
}
