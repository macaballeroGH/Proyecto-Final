package com.example.beautymanager.Servicio;

import com.example.beautymanager.Modelo.Entidad.RecordatorioEntity;

public interface EmailService {
    
    void enviarEmail(RecordatorioEntity recordatorio);
}
