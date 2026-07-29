package com.example.beautymanager.Servicio;

import org.springframework.web.multipart.MultipartFile;

import com.example.beautymanager.Modelo.Enums.CarpetaArchivoEnums;

public interface ArchivoService {

    String guardarArchivo(MultipartFile archivo, CarpetaArchivoEnums carpeta);

    void eliminarArchivo(String nombreArchivo, CarpetaArchivoEnums carpeta);

    String obtenerRutaArchivo(String nombreArchivo, CarpetaArchivoEnums carpeta);
}
