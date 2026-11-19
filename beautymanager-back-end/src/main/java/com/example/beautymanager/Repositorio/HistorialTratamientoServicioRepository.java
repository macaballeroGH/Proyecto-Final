package com.example.beautymanager.Repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.beautymanager.Modelo.Entidad.HistorialTratamientoServicioEntity;

public interface HistorialTratamientoServicioRepository extends JpaRepository<HistorialTratamientoServicioEntity, Long> {

    List<HistorialTratamientoServicioEntity> findByHistorialIdOrderByIdAsc(Long idHistorial);
}