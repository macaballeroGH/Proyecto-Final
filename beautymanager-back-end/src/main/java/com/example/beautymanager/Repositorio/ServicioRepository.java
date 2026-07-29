package com.example.beautymanager.Repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.beautymanager.Modelo.Entidad.ServicioEntity;
import com.example.beautymanager.Modelo.Enums.EstadoServicioEnums;

import java.math.BigDecimal;
import java.util.List;

public interface ServicioRepository extends JpaRepository<ServicioEntity, Long> {
    List<ServicioEntity> findByNombre(String nombre);

    List<ServicioEntity> findByNombreContainingIgnoreCase(String nombre);

    List<ServicioEntity> findByEstado(EstadoServicioEnums estado);

    List<ServicioEntity> findByEstadoAndCategoriaContainingIgnoreCase(EstadoServicioEnums estado, String nombre);

    List<ServicioEntity> findByCategoriaNombreContainingIgnoreCase(String nombre);

    List<ServicioEntity> findByDescripcionContainingIgnoreCase(String descripcion);

    List<ServicioEntity> findByPrecioBetween(BigDecimal min, BigDecimal max);

    List<ServicioEntity> findByPrecioLessThanEqual(BigDecimal precio);

    List<ServicioEntity> findByPrecioGreaterThanEqual(BigDecimal precio);

    List<ServicioEntity> findByDuracionMinutos(Integer minutos);

    List<ServicioEntity> findByDuracionMinutosLessThanEqual(Integer minutos);

    List<ServicioEntity> findByDuracionMinutosGreaterThanEqual(Integer minutos);

    List<ServicioEntity> findByDuracionMinutosBetween(Integer min, Integer max);

    boolean existsByNombreIgnoreCase(String nombre);
}
