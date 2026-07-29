package com.example.beautymanager.Servicio.servicioImpl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.DTO.ActualizarServicioDTO;
import com.example.beautymanager.Modelo.DTO.CrearServicioDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerServicioDTO;
import com.example.beautymanager.Modelo.Entidad.CategoriaServicioEntity;
import com.example.beautymanager.Modelo.Entidad.ServicioEntity;
import com.example.beautymanager.Modelo.Enums.EstadoServicioEnums;
import com.example.beautymanager.Repositorio.CategoriaServicioRepository;
import com.example.beautymanager.Repositorio.ServicioRepository;
import com.example.beautymanager.Servicio.ServicioService;
import com.example.beautymanager.exception.BusinessException;

@Service
public class ServicioServiceImpl implements ServicioService {

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private CategoriaServicioRepository categoriaServicioRepository;

    //===================
    //Crear servicio
    //===================
    @Override
    public ObtenerServicioDTO crearServicio(CrearServicioDTO crearServicio) {

        if(crearServicio == null){
            throw new BusinessException("El servicio no puede ser nulo");
        }

        ServicioEntity servicio = new ServicioEntity();

        servicio.setNombre(crearServicio.getNombre());
        servicio.setDescripcion(crearServicio.getDescripcion());
        servicio.setPrecio(crearServicio.getPrecio());
        servicio.setDuracionMinutos(crearServicio.getDuracionMinutos());

        CategoriaServicioEntity categoria = categoriaServicioRepository.findById(crearServicio.getIdCategoria())
                .orElseThrow(() -> new BusinessException("Categoria no encontrada"));

        servicio.setCategoria(categoria);

        validarServicio(servicio);

        servicio.setNombre(servicio.getNombre().trim());

        if(servicioRepository.existsByNombreIgnoreCase(servicio.getNombre())){
            throw new BusinessException("Ya existe un servicio con ese nombre");
        }

        servicio.setEstado(crearServicio.getEstado() != null ? crearServicio.getEstado() : EstadoServicioEnums.ACTIVO);

        return toMap(servicioRepository.save(servicio));
    }

    //=======================
    //Actualizar servicio
    //=======================
    @Override
    public ObtenerServicioDTO actualizarServicio(Long id, ActualizarServicioDTO actualizarServicio) {

        ServicioEntity servicio = obtenerEntidadPorId(id);

       if(actualizarServicio == null){
        throw new BusinessException("Datos invalidos");
       }

       if(actualizarServicio.getNombre() != null && !actualizarServicio.getNombre().isBlank()){

        String nuevoNombre = actualizarServicio.getNombre().trim();

        if(!servicio.getNombre().equalsIgnoreCase(nuevoNombre) && servicioRepository.existsByNombreIgnoreCase(nuevoNombre)){
            throw new BusinessException("Ya existe un servicio con ese nombre");
        }

        servicio.setNombre(nuevoNombre);
       }

       if(actualizarServicio.getDescripcion() != null){
        servicio.setDescripcion(actualizarServicio.getDescripcion().trim());
       }

       if(actualizarServicio.getPrecio() != null){
        if(actualizarServicio.getPrecio().compareTo(BigDecimal.ZERO) <= 0){
            throw new BusinessException("El precio debe ser mayor a 0");
        }
        servicio.setPrecio(actualizarServicio.getPrecio());
       }

       if(actualizarServicio.getDuracionMinutos() != null){
        if(actualizarServicio.getDuracionMinutos() <= 0){
            throw new BusinessException("La duracion debe ser mayor a 0");
        }
        servicio.setDuracionMinutos(actualizarServicio.getDuracionMinutos());
       }

       if(actualizarServicio.getIdCategoria() != null) {
        CategoriaServicioEntity categoria = categoriaServicioRepository.findById(actualizarServicio.getIdCategoria())
                .orElseThrow(() -> new BusinessException("Categoria no encontrada"));

        servicio.setCategoria(categoria);
       }

       if(actualizarServicio.getEstado() != null){
        servicio.setEstado(actualizarServicio.getEstado());
       }

        return toMap(servicioRepository.save(servicio));
    }

    //=====================
    //Eliminar servicio
    //=====================
    @Override
    public void eliminarServicio(Long id) {

        ServicioEntity servicio = obtenerEntidadPorId(id);

        if(servicio.getTurnoServicio() != null && !servicio.getTurnoServicio().isEmpty()){
            throw new BusinessException("No se puede eliminar un servicio asociado a turnos");
        }
        servicioRepository.delete(servicio);
    }

    //=====================
    //Obtener por ID
    //=====================
    @Override
    public ObtenerServicioDTO obtenerPorId(Long id) {

        return toMap(obtenerEntidadPorId(id));
    }

    //======================
    //Listados
    //======================
    @Override
    public List<ObtenerServicioDTO> listarTodos() {
        return servicioRepository.findAll().stream().map(this::toMap).toList();
    }

    @Override
    public List<ObtenerServicioDTO> listarActivos() {
        return servicioRepository.findByEstado(EstadoServicioEnums.ACTIVO).stream().map(this::toMap).toList();
    }

    @Override
    public List<ObtenerServicioDTO> listarInactivos() {
        return servicioRepository.findByEstado(EstadoServicioEnums.INACTIVO).stream().map(this::toMap).toList();
    }

    //=======================
    //Busquedas generales
    //=======================
    @Override
    public List<ObtenerServicioDTO> buscarPorNombre(String nombre) {

        if(nombre == null || nombre.isBlank()){
            throw new BusinessException("Debe ingresar un nombre");
        }

        return servicioRepository.findByNombreContainingIgnoreCase(nombre.trim()).stream().map(this::toMap).toList();
    }

    @Override
    public List<ObtenerServicioDTO> buscarPorDescripcion(String descripcion) {

        if(descripcion == null || descripcion.isBlank()){
            throw new BusinessException("Debe ingresar una descripcion");
        }

        return servicioRepository.findByDescripcionContainingIgnoreCase(descripcion.trim()).stream().map(this::toMap).toList();
    }

    @Override
    public List<ObtenerServicioDTO> buscarPorCategoria(String categoria) {

        if(categoria == null || categoria.isBlank()){
            throw new BusinessException("Debe ingresar una categoria");
        }
        return servicioRepository.findByCategoriaNombreContainingIgnoreCase(categoria.trim()).stream().map(this::toMap).toList();
    }

    @Override
    public List<ObtenerServicioDTO> buscarActivosPorCategoria(String categoria) {

        if(categoria == null || categoria.isBlank()){
            throw new BusinessException("Debe ingresar una categoria");
        }

        return servicioRepository.findByEstadoAndCategoriaContainingIgnoreCase(EstadoServicioEnums.ACTIVO,categoria.trim()).stream().map(this::toMap).toList();
    }

    //=======================
    //Precio
    //=======================
    @Override
    public List<ObtenerServicioDTO> buscarPorRangoPrecio(BigDecimal min, BigDecimal max) {

        if(min == null || max == null){
            throw new BusinessException("Debe ingresar valores validos");
        }

        if(min.compareTo(max) > 0){
            throw new BusinessException("El precio minimo no puede ser mayor al maximo");
        }

        return servicioRepository.findByPrecioBetween(min, max).stream().map(this::toMap).toList();
    }

    @Override
    public List<ObtenerServicioDTO> buscarPrecioMaximo(BigDecimal precio) {

        if(precio == null || precio.compareTo(BigDecimal.ZERO) <= 0){
            throw new BusinessException("Precio invalido");
        }
        return servicioRepository.findByPrecioLessThanEqual(precio).stream().map(this::toMap).toList();
    }

    @Override
    public List<ObtenerServicioDTO> buscarPrecioMinimo(BigDecimal precio) {

        if(precio == null || precio.compareTo(BigDecimal.ZERO) <= 0){
            throw new BusinessException("Precio invalido");
        }
        return servicioRepository.findByPrecioGreaterThanEqual(precio).stream().map(this::toMap).toList();
    }

    //====================
    //Duracion
    //====================
    @Override
    public List<ObtenerServicioDTO> buscarPorDuracion(Integer minutos) {

        if(minutos == null || minutos <= 0){
            throw new BusinessException("Duracion invalida");
        }

        return servicioRepository.findByDuracionMinutos(minutos).stream().map(this::toMap).toList();
    }

    @Override
    public List<ObtenerServicioDTO> buscarDuracionMaxima(Integer minutos) {

        if(minutos == null || minutos <= 0){
            throw new BusinessException("Duracion invalida");
        }

        return servicioRepository.findByDuracionMinutosLessThanEqual(minutos).stream().map(this::toMap).toList();
    }

    @Override
    public List<ObtenerServicioDTO> buscarDuracionMinima(Integer minutos) {

        if(minutos == null || minutos <= 0){
            throw new BusinessException("Duracion invalida");
        }

        return servicioRepository.findByDuracionMinutosGreaterThanEqual(minutos).stream().map(this::toMap).toList();
    }

    @Override
    public List<ObtenerServicioDTO> buscarPorRangoDuracion(Integer min, Integer max) {

        if(min == null || max == null){
            throw new BusinessException("Debe ingresar valores validos");
        }

        if(min <= 0 || max <= 0){
            throw new BusinessException("La duracion debe ser mayor a 0");
        }

        if(min > max){
            throw new BusinessException("La duracion minima no puede ser mayor a la maxima");
        }

        return servicioRepository.findByDuracionMinutosBetween(min, max).stream().map(this::toMap).toList();
    }

    //======================
    //Validaciones
    //======================
    private void validarServicio(ServicioEntity servicio) {

        if (servicio.getNombre() == null || servicio.getNombre().isBlank()) {
            throw new BusinessException("El nombre del servicio es obligatorio");
        }

        if (servicio.getPrecio() == null || servicio.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El precio debe ser mayor a 0");
        }

        if (servicio.getDuracionMinutos() == null || servicio.getDuracionMinutos() <= 0) {
            throw new BusinessException("La duracion debe ser mayor a 0");
        }

        if (servicio.getCategoria() == null) {
            throw new BusinessException("La categoria es obligatoria");
        }

        if(servicio.getDescripcion() == null || servicio.getDescripcion().isBlank()){
            throw new BusinessException("La descripcion es obligatoria");
        }
    }

    private ServicioEntity obtenerEntidadPorId(Long id){

        if(id == null || id <= 0){
            throw new BusinessException("ID invalido");
        }

        return servicioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Servicio no encontrado"));
    }

    @Override
    public ObtenerServicioDTO toMap(ServicioEntity servicio) {

        ObtenerServicioDTO obtenerServicioDTO = new ObtenerServicioDTO();

        obtenerServicioDTO.setId(servicio.getId());
        obtenerServicioDTO.setNombre(servicio.getNombre());
        obtenerServicioDTO.setDescripcion(servicio.getDescripcion());
        obtenerServicioDTO.setPrecio(servicio.getPrecio());
        obtenerServicioDTO.setDuracionMinutos(servicio.getDuracionMinutos());

        if(servicio.getCategoria() != null){
            obtenerServicioDTO.setNombreCategoria(servicio.getCategoria().getNombre());
        }

        obtenerServicioDTO.setEstado(servicio.getEstado());

        return obtenerServicioDTO;
    }
}
