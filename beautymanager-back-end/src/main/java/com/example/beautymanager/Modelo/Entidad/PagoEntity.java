package com.example.beautymanager.Modelo.Entidad;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.beautymanager.Modelo.Enums.EstadoPagoEnums;
import com.example.beautymanager.Modelo.Enums.MetodoPagoEnums;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "pago", schema = "pp2")
public class PagoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Long id;

    @Column(name = "monto", nullable = false)
    private BigDecimal monto;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false)
    private MetodoPagoEnums metodoPago;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoPagoEnums estado;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_turno", unique = true)
    private TurnoEntity turno;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_compra", unique = true)
    private CompraEntity compra;

    @PrePersist
    public void prePersist() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }
}
