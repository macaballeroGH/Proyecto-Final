package com.example.beautymanager.Repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;
import com.example.beautymanager.Modelo.Enums.EstadoUsuarioEnums;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByEmail(String email);

    
    boolean existsByEmail(String email);

    List<UsuarioEntity> findByNombreContainingIgnoreCase(String nombre);

    List<UsuarioEntity> findByApellidoContainingIgnoreCase(String apellido);

    List<UsuarioEntity> findByRolId(Long idRol);

    List<UsuarioEntity> findByEstadoUsuario(EstadoUsuarioEnums estadoUsuario);
}
