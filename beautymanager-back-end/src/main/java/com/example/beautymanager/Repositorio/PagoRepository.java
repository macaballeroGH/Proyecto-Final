package com.example.beautymanager.Repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.beautymanager.Modelo.Entidad.PagoEntity;
import com.example.beautymanager.Modelo.Enums.EstadoPagoEnums;
import com.example.beautymanager.Modelo.Enums.MetodoPagoEnums;

public interface PagoRepository extends JpaRepository<PagoEntity, Long> {

    Optional<PagoEntity> findByTurnoId(Long idTurno);

    List<PagoEntity> findByEstado(EstadoPagoEnums estado);

    List<PagoEntity> findByMetodoPago(MetodoPagoEnums metodo);

    Optional<PagoEntity> findByCompraId(Long idCompra);
}
