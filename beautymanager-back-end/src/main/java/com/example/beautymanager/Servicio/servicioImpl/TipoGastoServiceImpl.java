package com.example.beautymanager.Servicio.servicioImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.DTO.ActualizarTipoGastoDTO;
import com.example.beautymanager.Modelo.DTO.CrearTipoGastoDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerTipoGastoDTO;
import com.example.beautymanager.Modelo.Entidad.TipoGastoEntity;
import com.example.beautymanager.Repositorio.TipoGastoRepository;
import com.example.beautymanager.Servicio.TipoGastoService;
import com.example.beautymanager.exception.BusinessException;

@Service
public class TipoGastoServiceImpl implements TipoGastoService {
    
    @Autowired
    private TipoGastoRepository tipoGastoRepository;

    //==============================
    //Crear nuevo tipo de gasto
    //==============================
    @Override
    public ObtenerTipoGastoDTO crearTipoGasto(CrearTipoGastoDTO tipoGasto) {

        //Validar objeto nulo
        if(tipoGasto == null) {
            throw new BusinessException("El tipo de gasto no puede ser nulo");
        }

        //Validar nombre obligatorio
        if (tipoGasto.getNombreGasto() == null || tipoGasto.getNombreGasto().isBlank()) {
            throw new BusinessException("El nombre del tipo de gasto es obligatorio");
        }

        String nombreNormalizado = tipoGasto.getNombreGasto().trim();

        //Validar duplicado
        if (tipoGastoRepository.existsByNombreGastoIgnoreCase(nombreNormalizado)) {
            throw new BusinessException("Ya existe un tipo de gasto con ese nombre");
        }

        TipoGastoEntity entidad = new TipoGastoEntity();

        entidad.setNombreGasto(nombreNormalizado);
        entidad.setDescripcion(tipoGasto.getDescripcion());

        //Guardar
        return toMap(tipoGastoRepository.save(entidad));
    }

    //==========================
    //Actualizar tipo gasto
    //==========================
    @Override
    public ObtenerTipoGastoDTO actualizarTipoGasto(Long id, ActualizarTipoGastoDTO tipoGasto) {

        TipoGastoEntity existente = tipoGastoRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Tipo de gasto no encontrado"));

        if (tipoGasto == null) {
            throw new BusinessException("Los datos del tipo de gasto son obligatorios");
        }

        //Actualizar nombre
        if (tipoGasto.getNombreGasto() != null && !tipoGasto.getNombreGasto().isBlank()) {
            
            String nuevoNombre = tipoGasto.getNombreGasto().trim();

            if (!existente.getNombreGasto().equalsIgnoreCase(nuevoNombre) && tipoGastoRepository.existsByNombreGastoIgnoreCase(nuevoNombre)) {
                throw new BusinessException("Ya existe otro tipo de gasto con ese nombre");
            }

            existente.setNombreGasto(nuevoNombre);
        }

        //Actualizar descripcion
        if (tipoGasto.getDescripcion() != null) {
            existente.setDescripcion(tipoGasto.getDescripcion().trim());
        }

        return toMap(tipoGastoRepository.save(existente));
    }

    //==========================
    //Eliminar tipo de gasto
    //==========================
    @Override
    public void eliminarTipoGasto(Long id) {

        TipoGastoEntity tipoGasto = tipoGastoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Tipo de gasto no encontrado"));

        //Validar movimientos asociados
        if (tipoGasto.getMovimientosFinanzas() != null && !tipoGasto.getMovimientosFinanzas().isEmpty()) {
            throw new BusinessException("No se puede eliminar este tipo de gasto porque tiene movimientos asociados");
        }

        tipoGastoRepository.delete(tipoGasto);
    }

    //=========================
    //Buscar por id
    //=========================
    @Override
    public ObtenerTipoGastoDTO buscarPorId(Long id) {

        if(id == null || id <= 0) {
            throw new BusinessException("ID invalido");
        }

        return toMap(tipoGastoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Tipo de gasto no encontrado")));
    }

    //========================
    //Listar todos
    //========================
    @Override
    public List<ObtenerTipoGastoDTO> listarTodos() {
        return tipoGastoRepository.findAll().stream().map(this::toMap).toList();
    }

    //====================================
    //Listar ordenados alfabeticamente
    //====================================
    @Override
    public List<ObtenerTipoGastoDTO> listarOrdenados() {
        return tipoGastoRepository.findAllByOrderByNombreGastoAsc().stream().map(this::toMap).toList();
    }

    //=======================
    //Buscar por nombre
    //=======================
    @Override
    public List<ObtenerTipoGastoDTO> buscarPorNombre(String nombre) {

        if (nombre == null || nombre.isBlank()) {
            throw new BusinessException("Debe ingresar un nombre para buscar");
        }

        return tipoGastoRepository. findByNombreGastoContainingIgnoreCase(nombre.trim()).stream().map(this::toMap).toList();
    }

    //==========================
    //Buscar por decripcion
    //==========================
    @Override
    public List<ObtenerTipoGastoDTO> buscarPorDescripcion(String descripcion) {

        if (descripcion == null || descripcion.isBlank()) {
            throw new BusinessException("Debe ingresar una descripcion para buscar");
        }

        return tipoGastoRepository.findByDescripcionContainingIgnoreCase(descripcion.trim()).stream().map(this::toMap).toList();
    }

    //==================================
    //Verificar si existe por nombre
    //==================================
    @Override
    public boolean existePorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return false;
        }

        return tipoGastoRepository.existsByNombreGasto(nombre.trim());
    }

    @Override
    public ObtenerTipoGastoDTO toMap(TipoGastoEntity tipoGasto) {
        
        ObtenerTipoGastoDTO obtenerTipoGastoDTO = new ObtenerTipoGastoDTO();

        obtenerTipoGastoDTO.setIdTipoGasto(tipoGasto.getId());
        obtenerTipoGastoDTO.setNombreGasto(tipoGasto.getNombreGasto());
        obtenerTipoGastoDTO.setDescripcion(tipoGasto.getDescripcion());

        obtenerTipoGastoDTO.setCantidadMovimientos(tipoGasto.getMovimientosFinanzas() != null ? tipoGasto.getMovimientosFinanzas().size() : 0);

        return obtenerTipoGastoDTO;
    }
}
