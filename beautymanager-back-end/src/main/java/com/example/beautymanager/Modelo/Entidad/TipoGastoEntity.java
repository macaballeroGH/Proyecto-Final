package com.example.beautymanager.Modelo.Entidad;

import java.util.ArrayList;
import java.util.List;

import com.example.beautymanager.Modelo.Enums.TipoGastoEnums;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(name = "tipo_gasto", schema = "beautymanager")
public class TipoGastoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipoGasto")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "nombre_gasto", nullable = false)
    private TipoGastoEnums nombreGasto;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @JsonIgnore 
    @OneToMany(mappedBy = "tipoGasto")
    private List<MovimientoFinanzasEntity> movimientosFinanzas = new ArrayList<>();
}
