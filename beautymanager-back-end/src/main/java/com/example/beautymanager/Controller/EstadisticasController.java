package com.example.beautymanager.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.beautymanager.Servicio.EstadisticasService;
import com.example.beautymanager.Modelo.DTO.FiltroEstadisticasDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerEstadisticasDTO;

@RestController
@RequestMapping ("/estadisticas")
@PreAuthorize ("hasRole('ADMIN')") 
public class EstadisticasController {

    @Autowired 
    private EstadisticasService estadisticasService;

    @PostMapping ("/obtener")
    public ResponseEntity<ObtenerEstadisticasDTO> obtenerEstadisticas(@RequestBody FiltroEstadisticasDTO filtro) {

        ObtenerEstadisticasDTO resultado = estadisticasService.obtenerEstadisticas(filtro);

        return ResponseEntity.ok(resultado);
    }
}
