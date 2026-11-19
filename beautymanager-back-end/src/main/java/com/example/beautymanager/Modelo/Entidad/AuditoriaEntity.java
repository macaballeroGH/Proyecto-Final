package com.example.beautymanager.Modelo.Entidad;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "auditoria", schema = "beautymanager")
public class AuditoriaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_auditoria")
    private Long id;

    @Column(
        name = "accion",
        nullable = false,
        length = 150,
        updatable = false
    )
    private String accion;

    @Column(
        name = "tabla_afectada",
        nullable = false,
        length = 100,
        updatable = false
    )
    private String tablaAfectada;

    @Column(name = "id_registro", updatable = false)
    private Long idRegistro;

    @Column(
        name = "detalle",
        columnDefinition = "LONGTEXT",
        updatable = false
    )
    private String detalle;

    @Column(
        name = "fecha_hora",
        nullable = false,
        updatable = false
    )
    private LocalDateTime fechaHora;

    @Column(
        name = "tipo_evento",
        nullable = false,
        length = 30,
        updatable = false
    )
    private String tipoEvento = "OPERACION";

    @Column(
        name = "origen",
        nullable = false,
        length = 25,
        updatable = false
    )
    private String origen = "USUARIO";

    @Column(
        name = "resultado",
        nullable = false,
        length = 20,
        updatable = false
    )
    private String resultado = "EXITO";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "id_usuario",
        nullable = true,
        updatable = false
    )
    private UsuarioEntity usuario;

    @Column(
        name = "usuario_nombre",
        length = 100,
        updatable = false
    )
    private String usuarioNombre;

    @Column(
        name = "usuario_apellido",
        length = 100,
        updatable = false
    )
    private String usuarioApellido;

    @Column(
        name = "usuario_email",
        length = 255,
        updatable = false
    )
    private String usuarioEmail;

    @Column(
        name = "usuario_rol",
        length = 150,
        updatable = false
    )
    private String usuarioRol;

    @Column(
        name = "identificador_externo",
        length = 255,
        updatable = false
    )
    private String identificadorExterno;

    @Column(
        name = "valores_anteriores",
        columnDefinition = "LONGTEXT",
        updatable = false
    )
    private String valoresAnteriores;

    @Column(
        name = "valores_nuevos",
        columnDefinition = "LONGTEXT",
        updatable = false
    )
    private String valoresNuevos;

    @PrePersist
    public void prePersist() {

        if (fechaHora == null) {
            fechaHora = LocalDateTime.now();
        }
    }
}