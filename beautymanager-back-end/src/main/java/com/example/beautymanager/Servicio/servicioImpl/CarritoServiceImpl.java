package com.example.beautymanager.Servicio.servicioImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
import com.example.beautymanager.Modelo.Enums.EstadoProductoEnums;
import com.example.beautymanager.Repositorio.CarritoItemRepository;
import com.example.beautymanager.Repositorio.CarritoRepository;
import com.example.beautymanager.Repositorio.ClienteRepository;
import com.example.beautymanager.Repositorio.ProductoRepository;
import com.example.beautymanager.Repositorio.UsuarioRepository;
import com.example.beautymanager.Servicio.CarritoService;
import com.example.beautymanager.exception.BusinessException;

import jakarta.transaction.Transactional;

@Service
public class CarritoServiceImpl implements CarritoService {

    private final CarritoRepository carritoRepository;

    private final CarritoItemRepository carritoItemRepository;

    private final UsuarioRepository usuarioRepository;

    private final ProductoRepository productoRepository;

    private final ClienteRepository clienteRepository;

    public CarritoServiceImpl(CarritoRepository carritoRepository, CarritoItemRepository carritoItemRepository, UsuarioRepository usuarioRepository, ProductoRepository productoRepository, ClienteRepository clienteRepository){

        this.carritoRepository = carritoRepository;

        this.carritoItemRepository = carritoItemRepository;

        this.usuarioRepository = usuarioRepository;

        this.productoRepository = productoRepository;

        this.clienteRepository = clienteRepository;
    }

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
//Bloquear cliente para modificar carrito
//==========================
private void bloquearClienteParaCarrito(
        UsuarioEntity usuario) {

    if(usuario == null ||
       usuario.getCliente() == null ||
       usuario.getCliente().getId() == null) {

        throw new BusinessException(
            "El usuario no tiene un perfil de cliente válido"
        );
    }

    Long idCliente = usuario.getCliente().getId();

    clienteRepository.bloquearParaReserva(idCliente)
        .orElseThrow(() ->
            new BusinessException(
                "No se encontró el perfil de cliente"
            )
        );
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
    @Transactional
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

        if(agregarProductoCarrito == null) {
            throw new BusinessException(
                "Los datos del producto son obligatorios"
            );
        }

        Long idProducto = agregarProductoCarrito.getIdProducto();

        Integer cantidad = agregarProductoCarrito.getCantidad();

        if(idProducto == null || idProducto <= 0) {
            throw new BusinessException(
                "El ID del producto es inválido"
            );
        }

        if(cantidad == null || cantidad <= 0) {
            throw new BusinessException(
                "La cantidad debe ser mayor a cero"
            );
        }

        //=======================
        //Obtener usuario
        //=======================
        UsuarioEntity usuario = obtenerUsuario(email);

        bloquearClienteParaCarrito(usuario);

        //=======================
        //Buscar producto
        //=======================
        ProductoEntity producto = productoRepository
            .findById(idProducto)
            .orElseThrow(() ->
                new BusinessException(
                    "Producto no encontrado"
                )
            );

        //=======================
        //Validar estado
        //=======================
        if(producto.getEstadoProducto() != EstadoProductoEnums.ACTIVO) {

            throw new BusinessException(
                "El producto no está disponible para la venta: "
                + producto.getNombre()
            );
        }

        //=======================
        //Validar precio
        //=======================
        if(producto.getPrecio() == null ||
           producto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {

            throw new BusinessException(
                "El producto tiene un precio inválido"
            );
        }

        //=======================
        //Validar stock
        //=======================
        if(producto.getStock() == null ||
           producto.getStock() <= 0) {

            throw new BusinessException(
                "El producto está agotado: "
                + producto.getNombre()
            );
        }

        //=======================
        //Obtener carrito
        //=======================
        CarritoEntity carrito = obtenerOCrearCarrito(usuario);

        //=======================
        //Buscar producto en carrito
        //=======================
        CarritoItemEntity item = carritoItemRepository
            .findByCarritoAndProducto(carrito, producto)
            .orElse(null);

        //=======================
        //Calcular cantidad total
        //=======================
        Integer cantidadGuardada = item != null ? item.getCantidad() : null;

        long cantidadActual = cantidadGuardada == null ? 0L : cantidadGuardada.longValue();

        long cantidadTotal = cantidadActual + cantidad.longValue();

        //=======================
        //Comprobar disponibilidad
        //=======================
        if(cantidadTotal > producto.getStock()) {

            throw new BusinessException(
                "Stock insuficiente para "
                + producto.getNombre()
                + ". Unidades disponibles: "
                + producto.getStock()
            );
        }

        //=======================
        //Actualizar producto existente
        //=======================
        if(item != null) {

            item.setCantidad((int) cantidadTotal);

            //Usar precio vigente
            item.setPrecioUnitario(
                producto.getPrecio()
            );

            carritoItemRepository.save(item);

        } else {

            //=======================
            //Agregar nuevo producto
            //=======================
            CarritoItemEntity nuevoItem = new CarritoItemEntity();

            nuevoItem.setCarrito(carrito);

            nuevoItem.setProducto(producto);

            nuevoItem.setCantidad(cantidad);

            nuevoItem.setPrecioUnitario(
                producto.getPrecio()
            );

            carritoItemRepository.save(nuevoItem);
        }

        //=======================
        //Devolver carrito actualizado
        //=======================
        return toMap(carrito);
    }

    //=======================
    //Eliminar producto
    //=======================
    @Override
    @Transactional
    public ObtenerCarritoDTO eliminarProducto(String email, Long idProducto){

        UsuarioEntity usuario = obtenerUsuario(email);

        bloquearClienteParaCarrito(usuario);

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

        if(actualizarCantidadCarrito == null) {
            throw new BusinessException(
                "Los datos del producto son obligatorios"
            );
        }

        Long idProducto = actualizarCantidadCarrito.getIdProducto();

        Integer cantidad = actualizarCantidadCarrito.getCantidad();

        if(idProducto == null || idProducto <= 0) {
            throw new BusinessException(
                "El ID del producto es inválido"
            );
        }

        if(cantidad == null || cantidad <= 0) {
            throw new BusinessException(
                "La cantidad debe ser mayor a cero"
            );
        }

        //=======================
        //Obtener usuario
        //=======================
        UsuarioEntity usuario = obtenerUsuario(email);

        bloquearClienteParaCarrito(usuario);

        //=======================
        //Obtener producto
        //=======================
        ProductoEntity producto = productoRepository
            .findById(idProducto)
            .orElseThrow(() ->
                new BusinessException("Producto no encontrado")
            );

        //=======================
        //Validar estado
        //=======================
        if(producto.getEstadoProducto() != EstadoProductoEnums.ACTIVO) {

            throw new BusinessException(
                "El producto no está disponible para la venta: "
                + producto.getNombre()
            );
        }

        //=======================
        //Validar stock
        //=======================
        
        Integer stockActual = producto.getStock();

        if(stockActual == null || stockActual < cantidad) {

            throw new BusinessException("Stock insuficiente para " + producto.getNombre() + ". Unidades disponibles: " + (stockActual == null ? "0" : stockActual.toString()));
        }


        //=======================
        //Validar precio
        //=======================
        if(producto.getPrecio() == null ||
           producto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {

            throw new BusinessException(
                "El producto tiene un precio inválido"
            );
        }

        //=======================
        //Obtener carrito
        //=======================
        CarritoEntity carrito = obtenerOCrearCarrito(usuario);

        //=======================
        //Buscar artículo
        //=======================
        CarritoItemEntity item = carritoItemRepository
            .findByCarritoAndProducto(carrito, producto)
            .orElseThrow(() ->
                new BusinessException(
                    "El producto no está en el carrito"
                )
            );

        //=======================
        //Actualizar cantidad
        //=======================
        item.setCantidad(cantidad);

        //=======================
        //Actualizar precio vigente
        //=======================
        item.setPrecioUnitario(
            producto.getPrecio()
        );

        //=======================
        //Guardar cambios
        //=======================
        carritoItemRepository.save(item);

        //=======================
        //Devolver carrito
        //=======================
        return toMap(carrito);
    }

    @Override
    public ObtenerCarritoDTO toMap(CarritoEntity carrito) {
        
        ObtenerCarritoDTO obtenerCarritoDTO =
            new ObtenerCarritoDTO();

        //========================
        //Datos del carrito
        //========================
        obtenerCarritoDTO.setIdCarrito(
            carrito.getId()
        );

        obtenerCarritoDTO.setFechaCreacion(
            carrito.getFechaCreacion()
        );

        obtenerCarritoDTO.setEstadoCarrito(
            carrito.getEstado()
        );

        //========================
        //Datos del usuario
        //========================
        obtenerCarritoDTO.setIdUsuario(
            carrito.getUsuario().getId()
        );

        obtenerCarritoDTO.setNombreUsuario(
            carrito.getUsuario().getNombre()
        );

        obtenerCarritoDTO.setApellidoUsuario(
            carrito.getUsuario().getApellido()
        );

        //========================
        //Consultar productos del carrito
        //========================
        List<CarritoItemEntity> items =
            carritoItemRepository.findByCarrito(carrito);

        List<CarritoItemDTO> itemsDTO =
            new ArrayList<>();

        BigDecimal total = BigDecimal.ZERO;

        //========================
        //Convertir productos
        //========================
        for(CarritoItemEntity item : items) {

            CarritoItemDTO itemDTO =
                new CarritoItemDTO();

            itemDTO.setIdCarritoItem(
                item.getId()
            );

            itemDTO.setIdProducto(
                item.getProducto().getId()
            );

            itemDTO.setNombreProducto(
                item.getProducto().getNombre()
            );

            itemDTO.setDescripcionProducto(
                item.getProducto().getDescripcion()
            );

            //========================
            //Precio registrado en carrito
            //========================
            itemDTO.setPrecioProducto(
                item.getProducto().getPrecio()
            );

            //========================
            //Precio registrado en carrito
            //========================
            itemDTO.setPrecioUnitario(
                item.getPrecioUnitario()
            );

            //========================
            //Cantidad
            //========================
            itemDTO.setCantidad(
                item.getCantidad()
            );

            //========================
            //Subtotal
            //========================
            BigDecimal subtotal =
                item.getSubTotal();

            itemDTO.setSubtotal(subtotal);

            total = total.add(subtotal);

            itemsDTO.add(itemDTO);
        }

        //========================
        //Resultado
        //========================
        obtenerCarritoDTO.setItems(itemsDTO);

        obtenerCarritoDTO.setTotal(total);

        return obtenerCarritoDTO;
    }
}
