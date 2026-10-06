package com.example.beautymanager.Modelo.Entidad;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    @Column(name = "nombre_gasto", nullable = false, length = 100, unique = true)
    private String nombreGasto;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @OneToMany(mappedBy = "tipoGasto")
    private List<MovimientoFinanzasEntity> movimientosFinanzas = new ArrayList<>();
}
