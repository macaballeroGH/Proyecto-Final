package com.example.beautymanager.utils;

import java.text.Normalizer;
import java.util.Locale;

import com.example.beautymanager.Modelo.Entidad.EmpleadoEntity;
import com.example.beautymanager.Modelo.Entidad.ServicioEntity;
import com.example.beautymanager.Modelo.Enums.EspecialidadEmpleadoEnums;

public final class CompatibilidadServicio {

    private CompatibilidadServicio() {
    }

    public static boolean puedeRealizar(EmpleadoEntity empleado, ServicioEntity servicio) {

        if(empleado == null || servicio == null || empleado.getEspecialidad() == null || servicio.getCategoria() == null || servicio.getCategoria().getNombre() == null) {
            return false;
        }

        EspecialidadEmpleadoEnums especialidad = empleado.getEspecialidad();

        if(especialidad == EspecialidadEmpleadoEnums.RECEPCION) {
            return false;
        }

        if(especialidad == EspecialidadEmpleadoEnums.MULTIFUNCIONAL) {
            return true;
        }

        return normalizar(especialidad.name()).equals(normalizar(servicio.getCategoria().getNombre()));
    }

    private static String normalizar(String texto) {

        String resultado = Normalizer.normalize(
            texto.toUpperCase(Locale.ROOT),
            Normalizer.Form.NFD
        );

        return resultado.replaceAll("\\p{M}+", "").replaceAll("[^A-Z0-9]+", "_").replaceAll("^_+|_+$", "");
    }
}