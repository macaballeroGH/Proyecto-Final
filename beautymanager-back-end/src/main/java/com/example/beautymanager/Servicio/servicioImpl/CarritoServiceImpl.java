package com.example.beautymanager.Servicio.servicioImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.DTO.ActualizarCantidadCarritoDTO;
import com.example.beautymanager.Modelo.DTO.AgregarProductoCarritoDTO;
import com.example.beautymanager.Modelo.DTO.CarritoItemDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerCarritoDTO;
import com.example.beautymanager.Modelo.Entidad.CarritoEntity;
import com.example.beautymanager.Modelo.Entidad.CarritoItemEntity;
import com.example.beautymanager.Modelo.Entidad.ProductoEntity;
import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;
import com.example.beautymanager.Modelo.Enums.EstadoCarritoEnums;
import com.example.beautymanager.Repositorio.CarritoItemRepository;
import com.example.beautymanager.Repositorio.CarritoRepository;
import com.example.beautymanager.Repositorio.ProductoRepository;
import com.example.beautymanager.Repositorio.UsuarioRepository;
import com.example.beautymanager.Servicio.CarritoService;
import com.example.beautymanager.exception.BusinessException;

import jakarta.transaction.Transactional;

@Service
public class CarritoServiceImpl implements CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private CarritoItemRepository carritoItemRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    //==========================
    //Obtener usuario por email
    //==========================
    private UsuarioEntity obtenerUsuario(String email){
        
        if(email == null || email.isBlank()){
            throw new BusinessException("EEmail invalido");
        }

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
    }

    //==========================
    //Obtener o crear carrito
    //==========================
    private CarritoEntity obtenerOCrearCarrito(UsuarioEntity usuario){

        return carritoRepository.findByUsuarioAndEstado(usuario, EstadoCarritoEnums.ACTIVO)
                .orElseGet(() -> {
                    CarritoEntity nuevoCarrito = new CarritoEntity();
                    nuevoCarrito.setUsuario(usuario);
                    nuevoCarrito.setEstado(EstadoCarritoEnums.ACTIVO);
                    return carritoRepository.save(nuevoCarrito);
                });
    }

    //=======================
    //Ver carrito
    //=======================
    @Override
    public ObtenerCarritoDTO obtenerCarrito(String email){

        UsuarioEntity usuario = obtenerUsuario(email);

        return toMap(obtenerOCrearCarrito(usuario));
    }

    //=======================
    //Agregar producto
    //=======================
    @Override
    @Transactional
    public ObtenerCarritoDTO agregarProducto(String email, AgregarProductoCarritoDTO agregarProductoCarrito){

        if(agregarProductoCarrito == null || agregarProductoCarrito.getCantidad() == null || agregarProductoCarrito.getCantidad() <= 0){
            throw new BusinessException("Cantidad invalida");
        }

        UsuarioEntity usuario = obtenerUsuario(email);

        ProductoEntity producto = productoRepository.findById(agregarProductoCarrito.getIdProducto())
                .orElseThrow(() -> new BusinessException("Producto no encontrado"));

        CarritoEntity carrito = obtenerOCrearCarrito(usuario);

        CarritoItemEntity item = carritoItemRepository.findByCarritoAndProducto(carrito, producto)
             .orElse(null);

        if(item != null){
            item.setCantidad(item.getCantidad() + agregarProductoCarrito.getCantidad());
        } else {

            CarritoItemEntity nuevoItem = new CarritoItemEntity();
            nuevoItem.setCarrito(carrito);
            nuevoItem.setProducto(producto);
            nuevoItem.setCantidad(agregarProductoCarrito.getCantidad());
            nuevoItem.setPrecioUnitario(producto.getPrecio());

            carritoItemRepository.save(nuevoItem);
        }

        return toMap(carrito);
    }

    //=======================
    //Eliminar producto
    //=======================
    @Override
    @Transactional
    public ObtenerCarritoDTO eliminarProducto(String email, Long idProducto){

        UsuarioEntity usuario = obtenerUsuario(email);

        ProductoEntity producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new BusinessException("Producto no encontrado"));

        CarritoEntity carrito = obtenerOCrearCarrito(usuario);

        CarritoItemEntity item = carritoItemRepository.findByCarritoAndProducto(carrito, producto)
                .orElseThrow(() -> new BusinessException("El producto no esta en el carrito"));

        carritoItemRepository.delete(item);

        return toMap(carrito);
    }

    //=======================
    //Actualizar cantidad
    //=======================
    @Override
    @Transactional
    public ObtenerCarritoDTO actualizarCantidad(String email, ActualizarCantidadCarritoDTO actualizarCantidadCarrito){

        if(actualizarCantidadCarrito == null || actualizarCantidadCarrito.getCantidad() == null || actualizarCantidadCarrito.getCantidad() <= 0){
            throw new BusinessException("Cantidad invalida");
        }

        UsuarioEntity usuario = obtenerUsuario(email);

        ProductoEntity producto = productoRepository.findById(actualizarCantidadCarrito.getIdProducto())
                .orElseThrow(() -> new BusinessException("Producto no encontrado"));

        CarritoEntity carrito = obtenerOCrearCarrito(usuario);

        CarritoItemEntity item = carritoItemRepository.findByCarritoAndProducto(carrito, producto)
                .orElseThrow(() -> new BusinessException("El producto no esta en el carrito"));

        item.setCantidad(actualizarCantidadCarrito.getCantidad());

        carritoItemRepository.save(item);

        return toMap(carrito);
    }

    @Override
    public ObtenerCarritoDTO toMap(CarritoEntity carrito) {
        
        ObtenerCarritoDTO obtenerCarritoDTO = new ObtenerCarritoDTO();

        obtenerCarritoDTO.setIdCarrito(carrito.getId());
        obtenerCarritoDTO.setFechaCreacion(carrito.getFechaCreacion());
        obtenerCarritoDTO.setEstadoCarrito(carrito.getEstado());

        obtenerCarritoDTO.setIdUsuario(carrito.getUsuario().getId());
        obtenerCarritoDTO.setNombreUsuario(carrito.getUsuario().getNombre());
        obtenerCarritoDTO.setApellidoUsuario(carrito.getUsuario().getApellido());

        List<CarritoItemDTO> itemsDTO = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for(CarritoItemEntity item : carrito.getItems()){

            CarritoItemDTO itemDTO = new CarritoItemDTO();

            itemDTO.setIdCarritoItem(item.getId());
            itemDTO.setIdProducto(item.getProducto().getId());
            itemDTO.setNombreProducto(item.getProducto().getNombre());
            itemDTO.setDescripcionProducto(item.getProducto().getDescripcion());
            itemDTO.setPrecioProducto(item.getProducto().getPrecio());
            itemDTO.setCantidad(item.getCantidad());
            itemDTO.setPrecioProducto(item.getPrecioUnitario());
            itemDTO.setSubtotal(item.getSubTotal());

            total = total.add(item.getSubTotal());

            itemsDTO.add(itemDTO);
        }

        obtenerCarritoDTO.setItems(itemsDTO);
        obtenerCarritoDTO.setTotal(total);

        return obtenerCarritoDTO;
    }
}
