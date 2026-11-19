package com.example.beautymanager.Modelo.Entidad;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.beautymanager.Modelo.Enums.ResultadoTratamientoEnums;

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
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "historial_tratamiento", schema = "beautymanager")
public class HistorialTratamientoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Long id;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "observacion", length = 3500)
    private String observacion;

    @Column(name = "duracion_real")
    private Integer duracionReal;

    @Column(name = "productos_utilizados", length = 2500)
    private String productosUtilizados;

    @Enumerated(EnumType.STRING)
    @Column(name = "resultado_tratamiento", length = 50)
    private ResultadoTratamientoEnums resultadoTratamiento;

    @Column(name = "fecha_ultima_modificacion")
    private LocalDateTime fechaUltimaModificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_turno")
    private TurnoEntity turno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado_registro")
    private EmpleadoEntity registradoPor;

    @OneToMany(mappedBy = "historial", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<HistorialTratamientoServicioEntity> serviciosRealizados = new ArrayList<>();
}
