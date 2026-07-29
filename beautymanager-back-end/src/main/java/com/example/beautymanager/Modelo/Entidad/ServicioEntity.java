package com.example.beautymanager.Modelo.Entidad;

import java.math.BigDecimal;
import java.util.List;

import com.example.beautymanager.Modelo.Enums.EstadoServicioEnums;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "servicio", schema = "pp2")
public class ServicioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servicio")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 200, unique = true)
    private String nombre;

    @Column(name = "descripcion", nullable = false, length = 2000)
    private String descripcion;

    @Column(name = "precio", nullable = false)
    private BigDecimal precio;

    @Column(name = "duracion_minutos", nullable = false)
    private Integer duracionMinutos;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private CategoriaServicioEntity categoria;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoServicioEnums estado;

    @OneToMany(mappedBy = "servicio")
    private List<TurnoServicioEntity> turnoServicio;
}
