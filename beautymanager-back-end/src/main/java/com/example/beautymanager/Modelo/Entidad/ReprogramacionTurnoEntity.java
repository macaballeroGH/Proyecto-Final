package com.example.beautymanager.Modelo.Entidad;

import java.time.LocalDateTime;

import com.example.beautymanager.Modelo.Enums.EstadoReprogramacionEnums;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "reprogramacion_turno", schema = "beautymanager")
public class ReprogramacionTurnoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reprogramacion")
    private Long id;

    //====================================
    //Turno asociado
    //====================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_turno", nullable = false)
    private TurnoEntity turno;

    //====================================
    //Usuario que propone el cambio
    //====================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private UsuarioEntity usuarioResponsable;

    //====================================
    //Horario original
    //====================================

    @Column(name = "inicio_anterior", nullable = false)
    private LocalDateTime inicioAnterior;

    @Column(name = "fin_anterior", nullable = false)
    private LocalDateTime finAnterior;

    //====================================
    //Horario propuesto
    //====================================

    @Column(name = "inicio_nuevo", nullable = false)
    private LocalDateTime inicioNuevo;

    @Column(name = "fin_nuevo", nullable = false)
    private LocalDateTime finNuevo;

    //====================================
    //Motivo
    //====================================

    @Column(name = "motivo", length = 500, nullable = false)
    private String motivo;

    //====================================
    //Estado de la propuesta
    //====================================

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_propuesta", nullable = false)
    private EstadoReprogramacionEnums estadoPropuesta;

    //====================================
    //Fechas de seguimiento
    //====================================

    @Column(name = "fecha_cambio", nullable = false)
    private LocalDateTime fechaCambio;

    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;

    //====================================
    //Valores iniciales
    //====================================

    @PrePersist
    public void prePersist() {

        if (fechaCambio == null) {
            fechaCambio = LocalDateTime.now();
        }

        if (estadoPropuesta == null) {
            estadoPropuesta = EstadoReprogramacionEnums.PENDIENTE;
        }
    }
}
