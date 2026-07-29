package com.example.beautymanager.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.beautymanager.Modelo.DTO.ObtenerDashboardDTO;
import com.example.beautymanager.Servicio.DashboardService;

@RestController
@RequestMapping("/dashboard")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    //=======================
    //Obtener dashboard
    //=======================
    @GetMapping
    public ResponseEntity<ObtenerDashboardDTO> obtenerDashboard(){
        return ResponseEntity.ok(dashboardService.obtenerDashboard());
    }
}
