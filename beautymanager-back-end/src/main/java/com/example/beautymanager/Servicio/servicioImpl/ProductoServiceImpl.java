package com.example.beautymanager.Servicio.servicioImpl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.DTO.ActualizarProductoDTO;
import com.example.beautymanager.Modelo.DTO.CrearProductoDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerProductoDTO;
import com.example.beautymanager.Modelo.Entidad.ProductoEntity;
import com.example.beautymanager.Modelo.Enums.EstadoProductoEnums;
import com.example.beautymanager.Repositorio.ProductoRepository;
import com.example.beautymanager.Servicio.ProductoService;
import com.example.beautymanager.exception.BusinessException;

import jakarta.transaction.Transactional;

@Service
public class ProductoServiceImpl implements ProductoService {
    
    @Autowired
    private ProductoRepository productoRepository;

    //======================
    //Crear
    //======================
    @Override
    public ObtenerProductoDTO crearProducto(CrearProductoDTO crearProducto) {

        if(crearProducto == null) {
            throw new BusinessException("El producto no puede ser nulo");
        }

        if(crearProducto.getNombre() == null || crearProducto.getNombre().isBlank()) {
            throw new BusinessException("El nombre del producto es obligatorio");
        }

        String nombre = crearProducto.getNombre().trim();

        if(productoRepository.existsByNombreIgnoreCase(nombre)) {
            throw new BusinessException("Ya existe un producto con ese nombre");
        }

        if(crearProducto.getPrecio() == null || crearProducto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El precio del producto debe ser mayor a cero");
        }

        if(crearProducto.getStock() == null || crearProducto.getStock() < 0) {
            throw new BusinessException("Stock invalido");
        }

        ProductoEntity producto = new ProductoEntity();

        producto.setNombre(nombre);
        producto.setDescripcion(crearProducto.getDescripcion());
        producto.setPrecio(crearProducto.getPrecio());
        producto.setStock(crearProducto.getStock());

        if(crearProducto.getEstado() == null){
            producto.setEstadoProducto(EstadoProductoEnums.ACTIVO);
        } else {
            producto.setEstadoProducto(crearProducto.getEstado());
        }

        return toMap(productoRepository.save(producto));
    }

    //======================
    //Actualizar
    //======================
    @Override
    public ObtenerProductoDTO actualizarProducto(Long id, ActualizarProductoDTO actualizarProducto) {

        ProductoEntity producto = productoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Producto no encontrado"));

        if(actualizarProducto == null){
            throw new BusinessException("Datos invalidos");
        }

        if(actualizarProducto.getNombre() != null && !actualizarProducto.getNombre().isBlank()){
            String nuevoNombre = actualizarProducto.getNombre().trim();

            if(!producto.getNombre().equalsIgnoreCase(nuevoNombre) && productoRepository.existsByNombreIgnoreCase(nuevoNombre)){
                throw new BusinessException("Ya existe un producto con ese nombre");
            }
            producto.setNombre(nuevoNombre);
        }

        if(actualizarProducto.getDescripcion() != null) {
            producto.setDescripcion(actualizarProducto.getDescripcion().trim());
        }

        if(actualizarProducto.getPrecio() != null){
            if(actualizarProducto.getPrecio().compareTo(BigDecimal.ZERO) <= 0){
                throw new BusinessException("Precio invalido");
            }
            producto.setPrecio(actualizarProducto.getPrecio());
        }

        if(actualizarProducto.getStock() != null){
            if(actualizarProducto.getStock() < 0){
                throw new BusinessException("Stock invalido");
            }
            producto.setStock(actualizarProducto.getStock());
        }

        if(actualizarProducto.getEstado() != null){
            producto.setEstadoProducto(actualizarProducto.getEstado());
        }

        return toMap(productoRepository.save(producto));
    }

    //=======================
    //Eliminar
    //=======================
    @Override
    public void eliminarProducto(Long id) {

        ProductoEntity producto = productoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Producto no encontrado"));

        if(producto.getDetalleCompra() != null && !producto.getDetalleCompra().isEmpty()){
            throw new BusinessException("No se puede eliminar un producto con compras asociadas");
        }
        productoRepository.delete(producto);
    }

    //=======================
    //Obtener por id
    //=======================
    @Override
    public ObtenerProductoDTO obtenerPorId(Long id) {

        ProductoEntity producto = productoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Producto no encontrado"));

        return toMap(producto);
    }

    //=======================
    //Listar
    //=======================
    @Override
    public List<ObtenerProductoDTO> listarProducto() {
        
        return productoRepository.findAll().stream().map(this::toMap).toList();
    }

    //========================
    //Descontar stock
    //========================
    @Override
    @Transactional
    public void descontarStock(Long idProducto, Integer cantidad) {

        ProductoEntity producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new BusinessException("Producto no encotrado"));

        if (cantidad == null || cantidad <= 0){
            throw new BusinessException("Cantidad invalida");
        }

        if(producto.getStock() < cantidad){
            throw new BusinessException("Stock insuficiente");
        }

        producto.setStock(producto.getStock() - cantidad);

        productoRepository.save(producto);
    }
    
    //=========================
    //Aumentar stock
    //=========================
    @Override
    @Transactional
    public void aumentarStock(Long idProducto, Integer cantidad) {

        ProductoEntity producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new BusinessException("Producto no encontrado"));

        if(cantidad == null || cantidad <= 0){
            throw new BusinessException("Cantidad invalida");
        }

        producto.setStock(producto.getStock() + cantidad);

        productoRepository.save(producto);
    }

    @Override
    public ObtenerProductoDTO toMap(ProductoEntity producto) {

        ObtenerProductoDTO obtenerProductoDTO = new ObtenerProductoDTO();

        obtenerProductoDTO.setIdProducto(producto.getId());
        obtenerProductoDTO.setNombre(producto.getNombre());
        obtenerProductoDTO.setDescripcion(producto.getDescripcion());
        obtenerProductoDTO.setPrecio(producto.getPrecio());
        obtenerProductoDTO.setStock(producto.getStock());
        obtenerProductoDTO.setEstadoProducto(producto.getEstadoProducto());

        return obtenerProductoDTO;
    }

}
