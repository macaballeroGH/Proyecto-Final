package com.example.beautymanager.Modelo.Entidad;

import com.example.beautymanager.Modelo.Enums.EstadoRecordatorioEnums;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "configuracion_recordatorio", schema = "pp2")
public class ConfiguracionRecordatorioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_configuracion")
    private Long idConfiguracion;

    @Enumerated(EnumType.STRING)
    @Column(name = "email", nullable = false)
    private EstadoRecordatorioEnums estadoEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "whatsapp", nullable = false)
    private EstadoRecordatorioEnums estadoWhatsapp;

    @Enumerated(EnumType.STRING)
    @Column(name = "notificacion_interna", nullable = false)
    private EstadoRecordatorioEnums estadoNotificacionInterna;

    @Column(name = "primer_recordatorio", nullable = false)
    private Integer primerRecordatorioHoras;

    @Column(name = "segundo_recordatorio", nullable = false)
    private Integer segundoRecordatorioHoras;
}
