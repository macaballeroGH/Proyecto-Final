package com.example.beautymanager.Modelo.Entidad;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "historial_tratamiento_servicio", schema = "beautymanager")
public class HistorialTratamientoServicioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial_servicio")
    private Long id;

    @Column(name = "motivo_cambio", length = 500)
    private String motivoCambio;

    @Column(name = "fecha_cambio")
    private LocalDateTime fechaCambio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_historial", nullable = false)
    private HistorialTratamientoEntity historial;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_servicio_realizado", nullable = false)
    private ServicioEntity servicioRealizado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_turno_servicio_reservado")
    private TurnoServicioEntity servicioReservado;
}
