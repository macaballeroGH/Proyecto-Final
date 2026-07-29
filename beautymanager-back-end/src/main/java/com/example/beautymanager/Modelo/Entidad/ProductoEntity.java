package com.example.beautymanager.Modelo.Entidad;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.example.beautymanager.Modelo.Enums.EstadoProductoEnums;

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
@Table(name = "producto", schema = "pp2")
public class ProductoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    @Column(name = "descripcion", length = 1500)
    private String descripcion;

    @Column(name = "precio", nullable = false)
    private BigDecimal precio;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_producto", nullable = false)
    private EstadoProductoEnums estadoProducto;

    @OneToMany(mappedBy = "producto")
    private List<DetalleCompraEntity> detalleCompra = new ArrayList<>();
}
