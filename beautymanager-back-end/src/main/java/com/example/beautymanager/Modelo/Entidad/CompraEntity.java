package com.example.beautymanager.Modelo.Entidad;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.beautymanager.Modelo.Enums.EstadoCompraEnums;

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
import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "compra", schema = "pp2")
public class CompraEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compra")
    private Long id;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @Column(name = "total")
    private BigDecimal total;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private ClienteEntity cliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_compra")
    private EstadoCompraEnums estadoCompra;

    @OneToMany(mappedBy = "compra")
    private List<MovimientoFinanzasEntity> movimientosFinanzas = new ArrayList<>();

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleCompraEntity> detalleCompra = new ArrayList<>();

    @OneToOne(mappedBy = "compra", fetch = FetchType.LAZY)
    private PagoEntity pago;

    @PrePersist
    public void prePersist() {
        if (this.fecha == null) {
            this.fecha = LocalDateTime.now();
        }
    }
}