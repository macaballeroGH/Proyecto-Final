package com.example.beautymanager.Modelo.Entidad;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Data
@Table(name = "reserva", schema = "beautymanager")
public class ReservaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private ClienteEntity cliente;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    //==========================
    //Proteccion contra pagos duplicados
    //==========================

    @Column(
        name = "clave_idempotencia",
        length = 36,
        unique = true
    )
    private String claveIdempotencia;

    //==========================
    //Turnos de la reserva
    //==========================

    @OneToMany(mappedBy = "reserva", fetch = FetchType.LAZY)
    private List<TurnoEntity> turnos = new ArrayList<>();

    //==========================
    //Fecha de creacion
    //==========================

    @PrePersist
    public void prePersist() {

        if(fechaCreacion == null){
            fechaCreacion = LocalDateTime.now();
        }
    }
}