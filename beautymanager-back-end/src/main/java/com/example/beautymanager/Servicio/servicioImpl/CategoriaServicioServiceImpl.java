package com.example.beautymanager.Servicio.servicioImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.DTO.ActualizarCategoriaServicioDTO;
import com.example.beautymanager.Modelo.DTO.CrearCategoriaServicioDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerCategoriaServicioDTO;
import com.example.beautymanager.Modelo.Entidad.CategoriaServicioEntity;
import com.example.beautymanager.Modelo.Enums.EstadoCategoriaServicioEnums;
import com.example.beautymanager.Repositorio.CategoriaServicioRepository;
import com.example.beautymanager.Servicio.CategoriaServicioService;
import com.example.beautymanager.exception.BusinessException;

@Service
public class CategoriaServicioServiceImpl implements CategoriaServicioService {

    @Autowired
    private CategoriaServicioRepository categoriaServicioRepository;

    //=======================
    //Crear categoria
    //=======================
    @Override
    public ObtenerCategoriaServicioDTO crearCategoria(CrearCategoriaServicioDTO crearCategoria) {
        
        if(crearCategoria == null) {
            throw new BusinessException("La categoria no puede ser nula");
        }

        CategoriaServicioEntity categoria = new CategoriaServicioEntity();

        categoria.setNombre(crearCategoria.getNombreCategoria());
        categoria.setDescripcion(crearCategoria.getDescripcion());
        categoria.setEstado(crearCategoria.getEstado());

        validarCategoria(categoria);

        categoria.setNombre(categoria.getNombre().trim());

        if(categoria.getDescripcion() != null) {
            categoria.setDescripcion(categoria.getDescripcion().trim());
        }

        if(categoriaServicioRepository.existsByNombreIgnoreCase(categoria.getNombre())) {
            throw new BusinessException("Ya existe una categoria con ese nombre");
        }

        if(categoria.getEstado() == null) {
            categoria.setEstado(EstadoCategoriaServicioEnums.ACTIVA);
        }

        return toMap(categoriaServicioRepository.save(categoria));
    }

    //=======================
    //Actualizar categoria
    //=======================
    @Override
    public ObtenerCategoriaServicioDTO actualizarCategoria(Long id, ActualizarCategoriaServicioDTO actualizarCategoria) {

        if(actualizarCategoria == null){
            throw new BusinessException("Los datos son obligatorios");
        }

        CategoriaServicioEntity categoria = obtenerEntidadPorId(id);

        if(actualizarCategoria.getNombreCategoria() != null && !actualizarCategoria.getNombreCategoria().isBlank()){

            String nuevoNombre = actualizarCategoria.getNombreCategoria().trim();

            if(!categoria.getNombre().equalsIgnoreCase(nuevoNombre) && categoriaServicioRepository.existsByNombreIgnoreCase(nuevoNombre)){
                throw new BusinessException("Ya existe una categoria con ese nombre");
            }

            categoria.setNombre(nuevoNombre);
        }

        if(actualizarCategoria.getDescripcion() != null){
            categoria.setDescripcion(actualizarCategoria.getDescripcion().trim());
        }

        if(actualizarCategoria.getEstado() != null){
            categoria.setEstado(actualizarCategoria.getEstado());
        }

        validarCategoria(categoria);

        return toMap(categoriaServicioRepository.save(categoria));
    }

    //=======================
    //Eliminar categoria
    //=======================
    @Override
    public void eliminarCategoria(Long id) {

        CategoriaServicioEntity categoria = obtenerEntidadPorId(id);

        if(categoria.getServicios() != null && !categoria.getServicios().isEmpty()) {
            throw new BusinessException("No se puede eliminar una categoria que tiene servicios asociados");
        }

        categoriaServicioRepository.delete(categoria);
    }

    //=======================
    //Obtener por ID
    //=======================
    @Override
    public ObtenerCategoriaServicioDTO obtenerPorId(Long id) {
        return toMap(categoriaServicioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Categoria no encontrada")));
    }

    //=======================
    //Listar todas
    //=======================
    @Override
    public List<ObtenerCategoriaServicioDTO> listarTodas() {
        return categoriaServicioRepository.findAll().stream().map(this::toMap).toList();
    }

    //=======================
    //Listar activas
    //=======================
    @Override
    public List<ObtenerCategoriaServicioDTO> listarActivas() {
        return categoriaServicioRepository.findByEstado(EstadoCategoriaServicioEnums.ACTIVA).stream().map(this::toMap).toList();
    }

    //=======================
    //Listar inactivas
    //=======================
    @Override
    public List<ObtenerCategoriaServicioDTO> listarInactivas() {
        return categoriaServicioRepository.findByEstado(EstadoCategoriaServicioEnums.INACTIVA).stream().map(this::toMap).toList();
    }

    //=======================
    //Buscar por nombre
    //=======================
    @Override
    public List<ObtenerCategoriaServicioDTO> buscarPorNombre(String nombre) {
        
        if(nombre == null || nombre.isBlank()) {
            throw new BusinessException("Debe ingresar un nombre");
        }

        return categoriaServicioRepository.findByNombreContainingIgnoreCase(nombre.trim()).stream().map(this::toMap).toList();
    }

    //=======================
    //Buscar por descripcion
    //=======================
    @Override
    public List<ObtenerCategoriaServicioDTO> buscarPorDescripcion(String descripcion) {

        if(descripcion == null || descripcion.isBlank()) {
            throw new BusinessException("Debe ingresar una descripcion");
        }

        return categoriaServicioRepository.findByDescripcionContainingIgnoreCase(descripcion.trim()).stream().map(this::toMap).toList();
    }

    //=======================
    //Validar categoria
    //=======================
    private void validarCategoria(CategoriaServicioEntity categoria) {

        if(categoria.getNombre() == null || categoria.getNombre().isBlank()) {
            throw new BusinessException("El nombre de la categoria es obligatorio");
        }

        if(categoria.getNombre().length() > 150) {
            throw new BusinessException("El nombre de la categoria no puede superar los 150 caracteres");
        }

        if(categoria.getDescripcion() != null && categoria.getDescripcion().length() > 1000) {
            throw new BusinessException("La descripcion de la categoria no puede superar los 1000 caracteres");
        }
    }

    private CategoriaServicioEntity obtenerEntidadPorId(Long id){

        if(id == null || id <= 0){
            throw new BusinessException("ID de categoria invalido");
        }

        return categoriaServicioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Categoria no encontrada"));
    }

    @Override
    public ObtenerCategoriaServicioDTO toMap(CategoriaServicioEntity categoria) {

        ObtenerCategoriaServicioDTO obtenerCategoriaServicioDTO = new ObtenerCategoriaServicioDTO();

        obtenerCategoriaServicioDTO.setIdCategoria(categoria.getId());
        obtenerCategoriaServicioDTO.setNombreCategoria(categoria.getNombre());
        obtenerCategoriaServicioDTO.setDescripcion(categoria.getDescripcion());
        obtenerCategoriaServicioDTO.setEstado(categoria.getEstado());

        return obtenerCategoriaServicioDTO;
    }
}
