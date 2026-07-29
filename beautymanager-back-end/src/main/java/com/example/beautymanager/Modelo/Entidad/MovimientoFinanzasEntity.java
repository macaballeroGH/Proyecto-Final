package com.example.beautymanager.Modelo.Entidad;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.beautymanager.Modelo.Enums.EstadoMovimientoEnums;
import com.example.beautymanager.Modelo.Enums.TipoMovimientoEnums;

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
import lombok.Data;

@Entity
@Data
@Table(name = "movimiento_finanzas", schema = "pp2")
public class MovimientoFinanzasEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento")
    private Long id;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @Column(name = "monto")
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento")
    private TipoMovimientoEnums tipoMovimiento;

    @Column(name = "descripcion", length = 1000)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoMovimientoEnums estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private UsuarioEntity usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipoGasto", nullable = true)
    private TipoGastoEntity tipoGasto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_compra", nullable = true)
    private CompraEntity compra;

    @PrePersist
    public void prePersist() {
        if (this.fecha == null) {
            this.fecha = LocalDateTime.now();
        }
    }
}