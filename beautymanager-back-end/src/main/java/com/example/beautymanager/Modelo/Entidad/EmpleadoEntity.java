package com.example.beautymanager.Modelo.Entidad;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.beautymanager.Modelo.Enums.EspecialidadEmpleadoEnums;
import com.example.beautymanager.Modelo.Enums.EstadoEmpleadoEnums;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "empleado", schema = "beautymanager")
public class EmpleadoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empleado")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "especialidad", nullable = false)
    private EspecialidadEmpleadoEnums especialidad;

    @Column(name = "fecha_alta", nullable = false)
    private LocalDateTime fechaAlta;

    @Column(name = "fecha_baja")
    private LocalDateTime fechaBaja;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_empleado", nullable = false)
    private EstadoEmpleadoEnums estadoEmpleado;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    private UsuarioEntity usuario;

    @OneToMany(mappedBy = "empleado", fetch = FetchType.LAZY)
    private List<TurnoEntity> turnos = new ArrayList<>();

    @OneToMany(mappedBy = "empleado", fetch = FetchType.LAZY)
    private List<HorarioEmpleadoEntity> horarios = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (fechaAlta == null) {
            fechaAlta = LocalDateTime.now();
        }

        if (estadoEmpleado == null) {
            estadoEmpleado = EstadoEmpleadoEnums.ACTIVO;
        }
    }
}
