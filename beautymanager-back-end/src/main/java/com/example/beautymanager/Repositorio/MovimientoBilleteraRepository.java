package com.example.beautymanager.Repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.beautymanager.Modelo.Entidad.BilleteraEntity;
import com.example.beautymanager.Modelo.Entidad.MovimientoBilleteraEntity;

public interface MovimientoBilleteraRepository extends JpaRepository<MovimientoBilleteraEntity, Long> {
    List<MovimientoBilleteraEntity> findByBilletera(BilleteraEntity billetera);

    List<MovimientoBilleteraEntity> findByBilleteraOrderByFechaDesc(BilleteraEntity billetera);
}
