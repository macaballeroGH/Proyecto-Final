package com.example.beautymanager.Servicio.servicioImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.beautymanager.Modelo.Enums.CarpetaArchivoEnums;
import com.example.beautymanager.Servicio.ArchivoService;
import com.example.beautymanager.exception.BusinessException;

@Service
public class ArchivoServiceImpl implements ArchivoService {

    private static final String RUTA_UPLOADS = "uploads";
    private static final long TAMANIO_MAXIMO = 5 * 1024 * 1024;

    //=======================
    //Guardar archivo
    //=======================
    @Override
    public String guardarArchivo(MultipartFile archivo, CarpetaArchivoEnums carpeta){
        
        validarArchivo(archivo);

        try {

            Path rutaCarpeta = obtenerRutaCarpeta(carpeta);
            crearCarpetaSiNoExiste(rutaCarpeta);

            String nombreArchivo = generarNombreUnico(archivo);
            Path rutaArchivo = rutaCarpeta.resolve(nombreArchivo);

            Files.copy(archivo.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

            return nombreArchivo;

        } catch (IOException e) {

            throw new BusinessException("No se pudo guardar el archivo");
        }
    }

    //=======================
    //Eliminar archivo
    //=======================
    @Override
    public void eliminarArchivo(String nombreArchivo, CarpetaArchivoEnums carpeta){

        if(nombreArchivo == null || nombreArchivo.isBlank()){
            throw new BusinessException("El nombre del archivo es obligatorio");
        }

        try{

            Path rutaArchivo = obtenerRutaCarpeta(carpeta).resolve(nombreArchivo);

            Files.deleteIfExists(rutaArchivo);

        } catch (IOException e) {
            throw new BusinessException("No se pudo eliminar el archivo");
        }
    }

    //=======================
    //Obtener ruta archivo
    //=======================
    @Override
    public String obtenerRutaArchivo(String nombreArchivo, CarpetaArchivoEnums carpeta){

        if(nombreArchivo == null || nombreArchivo.isBlank()){
            return null;
        }

        return RUTA_UPLOADS + "/" + carpeta.name().toLowerCase() + "/" + nombreArchivo;
    }

    //=======================
    //Validar archivo
    //=======================
    private void validarArchivo(MultipartFile archivo){

        if(archivo == null || archivo.isEmpty()){
            throw new BusinessException("El archivo es obligatorio");
        }

        if(archivo.getSize() > TAMANIO_MAXIMO){
            throw new BusinessException("El archivo supera el tamaño maximo permitido de 5 MB");
        }

        String nombreOriginal = archivo.getOriginalFilename();

        if(nombreOriginal == null || nombreOriginal.isBlank()){
            throw new BusinessException("El archivo no tiene nombre valido");
        }

        String extension = obtenerExtension(nombreOriginal);

        if(!extension.equals("jpg") && !extension.equals("jpeg") && !extension.equals("png") && !extension.equals("webp")){
            throw new BusinessException("Formato de archivo no permitido");
        }
    }

    //=======================
    //Obtener ruta carpeta
    //=======================
    private Path obtenerRutaCarpeta(CarpetaArchivoEnums carpeta){

        if(carpeta == null){
            throw new BusinessException("La carpeta es obligatoria");
        }
        
        return Paths.get(RUTA_UPLOADS, carpeta.name().toLowerCase());
    }

    //=======================
    //Crear carpeta
    //=======================
    private void crearCarpetaSiNoExiste(Path rutaCarpeta) throws IOException {
        if(!Files.exists(rutaCarpeta)){
            Files.createDirectories(rutaCarpeta);
        }
    }

    //=======================
    //Generar nombre unico
    //=======================
    private String generarNombreUnico(MultipartFile archivo){
        String extension = obtenerExtension(archivo.getOriginalFilename());

        return UUID.randomUUID().toString() + "." + extension;
    }

    //=======================
    //Obtener extension
    //=======================
    private String obtenerExtension(String nombreArchivo){

        int ultimoPunto = nombreArchivo.lastIndexOf(".");

        if(ultimoPunto == -1){
            throw new BusinessException("El archivo no tiene extension");
        }

        return nombreArchivo.substring(ultimoPunto + 1).toLowerCase();
    }

}
