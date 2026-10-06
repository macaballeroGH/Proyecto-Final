package com.example.beautymanager.Servicio;

import com.example.beautymanager.Modelo.Entidad.TurnoEntity;

public interface RecordatorioService {

    void crearRecordatorios(TurnoEntity turno);

    void procesarRecordatorios();

    void reprogramarRecordatoriosFuturos();
}
