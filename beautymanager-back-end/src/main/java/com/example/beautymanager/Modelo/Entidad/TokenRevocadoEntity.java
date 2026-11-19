package com.example.beautymanager.Modelo.Entidad;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor 
@Table(name = "token_revocado", schema = "beautymanager")
public class TokenRevocadoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_token_revocado")
    private Long id;

    @Column(name = "jwt_id", nullable = false, unique = true, length = 36)
    private String jwtId;

    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDateTime fechaExpiracion;
}
