package com.example.beautymanager.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.beautymanager.Modelo.DTO.ActualizarConfiguracionRecordatorioDTO;
import com.example.beautymanager.Modelo.DTO.CrearConfiguracionRecordatorioDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerConfiguracionRecordatorioDTO;
import com.example.beautymanager.Modelo.DTO.ProbarEmailDTO;
import com.example.beautymanager.Servicio.ConfiguracionRecordatorioService;
import com.example.beautymanager.Servicio.EmailService;

@RestController
@RequestMapping("/configuracion-recordatorios")
@PreAuthorize("hasRole('ADMIN')")
public class ConfiguracionRecordatorioController {

    @Autowired
    private ConfiguracionRecordatorioService configuracionRecordatorioService;

    @Autowired
    private EmailService emailService;

    //=======================
    //Crear configuracion
    //=======================
    @PostMapping
    public ResponseEntity<ObtenerConfiguracionRecordatorioDTO> crearConfiguracion(@RequestBody CrearConfiguracionRecordatorioDTO crearConfiguracion){

        return ResponseEntity.ok(configuracionRecordatorioService.crearConfiguracion(crearConfiguracion));
    }

    //=======================
    //Obtener configuracion
    //=======================
    @GetMapping
    public ResponseEntity<ObtenerConfiguracionRecordatorioDTO> obtenerConfiguracion(){
        return ResponseEntity.ok(configuracionRecordatorioService.obtenerConfiguracion());
    }

    //===========================
    //Actualizar configuracion
    //===========================
    @PutMapping
    public ResponseEntity<ObtenerConfiguracionRecordatorioDTO> actualizarConfiguracion(@RequestBody ActualizarConfiguracionRecordatorioDTO actualizarConfiguracion){
        return ResponseEntity.ok(configuracionRecordatorioService.actualizarConfiguracion(actualizarConfiguracion));
    }

    
    //=======================
    // Probar envio de email
    //=======================
    @PostMapping("/probar-email")
    public ResponseEntity<String> probarEmail(@RequestBody ProbarEmailDTO probarEmailDTO) {

        if (probarEmailDTO == null || probarEmailDTO.getEmail() == null || probarEmailDTO.getEmail().isBlank()) {

            return ResponseEntity.badRequest().body("La dirección de correo es obligatoria");
        }

        emailService.enviarEmailPrueba(probarEmailDTO.getEmail());

        return ResponseEntity.ok("Correo de prueba enviado correctamente");
    }
}
