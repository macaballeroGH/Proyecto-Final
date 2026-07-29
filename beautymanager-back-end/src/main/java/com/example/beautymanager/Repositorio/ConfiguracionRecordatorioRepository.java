package com.example.beautymanager.Repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.beautymanager.Modelo.Entidad.ConfiguracionRecordatorioEntity;

public interface ConfiguracionRecordatorioRepository extends JpaRepository<ConfiguracionRecordatorioEntity, Long>{

    Optional<ConfiguracionRecordatorioEntity> findFirstByOrderByIdConfiguracionAsc();
}
