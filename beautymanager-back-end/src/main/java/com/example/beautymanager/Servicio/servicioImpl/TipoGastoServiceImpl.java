package com.example.beautymanager.Servicio.servicioImpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.beautymanager.Modelo.DTO.ActualizarTipoGastoDTO;
import com.example.beautymanager.Modelo.DTO.CrearTipoGastoDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerTipoGastoDTO;
import com.example.beautymanager.Modelo.Entidad.TipoGastoEntity;
import com.example.beautymanager.Modelo.Enums.TipoGastoEnums;
import com.example.beautymanager.Repositorio.TipoGastoRepository;
import com.example.beautymanager.Servicio.TipoGastoService;
import com.example.beautymanager.exception.BusinessException;

@Service
public class TipoGastoServiceImpl implements TipoGastoService {
    
    private final TipoGastoRepository tipoGastoRepository;

    public TipoGastoServiceImpl(TipoGastoRepository tipoGastoRepository){
        this.tipoGastoRepository = tipoGastoRepository;
    }

    //==============================
    //Crear nuevo tipo de gasto
    //==============================
    @Override
    @Transactional
    public ObtenerTipoGastoDTO crearTipoGasto(CrearTipoGastoDTO tipoGasto) {

        if (tipoGasto == null) {
            throw new BusinessException("El tipo de gasto no puede ser nulo");
        }

        TipoGastoEnums nombreGasto = tipoGasto.getNombreGasto();

        if (nombreGasto == null) {
            throw new BusinessException("El nombre del tipo de gasto es obligatorio");
        }

        if (tipoGastoRepository.existsByNombreGasto(nombreGasto)) {
            throw new BusinessException("Ya existe un tipo de gasto con ese nombre");
        }

        TipoGastoEntity entidad = new TipoGastoEntity();
        entidad.setNombreGasto(nombreGasto);
        entidad.setDescripcion(tipoGasto.getDescripcion());

        return toMap(tipoGastoRepository.save(entidad));
    }

    //==========================
    //Actualizar tipo de gasto
    //==========================
    @Override
    @Transactional
    public ObtenerTipoGastoDTO actualizarTipoGasto(Long id, ActualizarTipoGastoDTO tipoGasto) {

        if (id == null || id <= 0) {
            throw new BusinessException("ID inválido");
        }

        if (tipoGasto == null) {
            throw new BusinessException("Los datos del tipo de gasto son obligatorios");
        }

        TipoGastoEntity existente = tipoGastoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Tipo de gasto no encontrado"));

        TipoGastoEnums nuevoNombre = tipoGasto.getNombreGasto();

        if (nuevoNombre != null && nuevoNombre != existente.getNombreGasto()) {

            if (tipoGastoRepository.existsByNombreGasto(nuevoNombre)) {
                throw new BusinessException("Ya existe otro tipo de gasto con ese nombre");
            }

            existente.setNombreGasto(nuevoNombre);
        }

        if (tipoGasto.getDescripcion() != null) {
            existente.setDescripcion(tipoGasto.getDescripcion().trim());
        }

        return toMap(tipoGastoRepository.save(existente));
    }

    //==========================
    //Eliminar tipo de gasto
    //==========================
    @Override
    @Transactional
    public void eliminarTipoGasto(Long id) {

        if (id == null || id <= 0) {
            throw new BusinessException("ID inválido");
        }

        TipoGastoEntity tipoGasto = tipoGastoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Tipo de gasto no encontrado"));

        if (tipoGasto.getMovimientosFinanzas() != null
                && !tipoGasto.getMovimientosFinanzas().isEmpty()) {
            throw new BusinessException(
                    "No se puede eliminar este tipo de gasto porque tiene movimientos asociados");
        }

        tipoGastoRepository.delete(tipoGasto);
    }

    //=========================
    //Buscar por ID
    //=========================
    @Override
    public ObtenerTipoGastoDTO buscarPorId(Long id) {

        if (id == null || id <= 0) {
            throw new BusinessException("ID inválido");
        }

        return toMap(tipoGastoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Tipo de gasto no encontrado")));
    }

    //========================
    //Listar todos
    //========================
    @Override
    public List<ObtenerTipoGastoDTO> listarTodos() {
        return tipoGastoRepository.findAll()
                .stream()
                .map(this::toMap)
                .toList();
    }

    //====================================
    //Listar ordenados alfabéticamente
    //====================================
    @Override
    public List<ObtenerTipoGastoDTO> listarOrdenados() {
        return tipoGastoRepository.findAllByOrderByNombreGastoAsc()
                .stream()
                .map(this::toMap)
                .toList();
    }

    //=======================
    //Buscar por nombre
    //=======================
    @Override
    public List<ObtenerTipoGastoDTO> buscarPorNombre(String nombre) {

        if (nombre == null || nombre.isBlank()) {
            throw new BusinessException("Debe ingresar un nombre para buscar");
        }

        TipoGastoEnums nombreGasto;

        try {
            nombreGasto = TipoGastoEnums.valueOf(
                    nombre.trim().toUpperCase().replace(' ', '_'));
        } catch (IllegalArgumentException e) {
            return List.of();
        }

        return tipoGastoRepository.findByNombreGasto(nombreGasto)
                .map(this::toMap)
                .map(List::of)
                .orElseGet(List::of);
    }

    //==========================
    //Buscar por descripción
    //==========================
    @Override
    public List<ObtenerTipoGastoDTO> buscarPorDescripcion(String descripcion) {

        if (descripcion == null || descripcion.isBlank()) {
            throw new BusinessException("Debe ingresar una descripción para buscar");
        }

        return tipoGastoRepository.findByDescripcionContainingIgnoreCase(descripcion.trim())
                .stream()
                .map(this::toMap)
                .toList();
    }

    //==================================
    //Verificar si existe por nombre
    //==================================
    @Override
    public boolean existePorNombre(String nombre) {

        if (nombre == null || nombre.isBlank()) {
            return false;
        }

        try {
            TipoGastoEnums nombreGasto = TipoGastoEnums.valueOf(
                    nombre.trim().toUpperCase().replace(' ', '_'));

            return tipoGastoRepository.existsByNombreGasto(nombreGasto);

        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    //=======================
    //Mapear DTO
    //=======================
    @Override
    public ObtenerTipoGastoDTO toMap(TipoGastoEntity tipoGasto) {

        ObtenerTipoGastoDTO dto = new ObtenerTipoGastoDTO();

        dto.setIdTipoGasto(tipoGasto.getId());
        dto.setNombreGasto(tipoGasto.getNombreGasto());
        dto.setDescripcion(tipoGasto.getDescripcion());

        dto.setCantidadMovimientos(
                tipoGasto.getMovimientosFinanzas() != null
                        ? tipoGasto.getMovimientosFinanzas().size()
                        : 0);

        return dto;
    }
}
