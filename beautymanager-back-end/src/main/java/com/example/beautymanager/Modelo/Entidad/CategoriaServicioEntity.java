package com.example.beautymanager.Modelo.Entidad;

import java.util.ArrayList;
import java.util.List;

import com.example.beautymanager.Modelo.Enums.EstadoCategoriaServicioEnums;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "categoria_servicio", schema = "pp2")
public class CategoriaServicioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "descripcion", length = 1000)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoCategoriaServicioEnums estado;

    @OneToMany(mappedBy = "categoria")
    private List<ServicioEntity> servicios = new ArrayList<>();
}
