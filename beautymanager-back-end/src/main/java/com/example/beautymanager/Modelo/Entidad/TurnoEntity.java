package com.example.beautymanager.Modelo.Entidad;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.beautymanager.Modelo.Enums.EstadoTurnoEnums;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "turno", schema = "beautymanager")
public class TurnoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_turno")
    private Long id;

    @Column(name = "fechaHoraInicio")
    private LocalDateTime fechaHoraInicio;

    @Column(name = "precio_total", nullable = false)
    private BigDecimal precioTotal = BigDecimal.ZERO;

    @Column(name = "observaciones", length = 2000)
    private String observaciones;
    
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fechaHoraFin")
    private LocalDateTime fechaHoraFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_turno", nullable = false)
    private EstadoTurnoEnums estadoTurno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private ClienteEntity cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado", nullable = false)
    private EmpleadoEntity empleado;

    @OneToMany(mappedBy = "turno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TurnoServicioEntity> servicios = new ArrayList<>();

    @OneToMany(mappedBy = "turno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecordatorioEntity> recordatorio = new ArrayList<>();

    @OneToMany(mappedBy = "turno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistorialTratamientoEntity> historialTratamiento = new ArrayList<>();

    @OneToOne(mappedBy = "turno", fetch = FetchType.LAZY)
    private PagoEntity pago;

    @PrePersist
    public void prePersist() {
        if(this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
    }
}
