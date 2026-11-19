package com.example.beautymanager.Servicio.servicioImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.DTO.DetalleCompraDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerCompraDTO;
import com.example.beautymanager.Modelo.Entidad.CarritoEntity;
import com.example.beautymanager.Modelo.Entidad.CarritoItemEntity;
import com.example.beautymanager.Modelo.Entidad.ClienteEntity;
import com.example.beautymanager.Modelo.Entidad.CompraEntity;
import com.example.beautymanager.Modelo.Entidad.DetalleCompraEntity;
import com.example.beautymanager.Modelo.Entidad.MovimientoFinanzasEntity;
import com.example.beautymanager.Modelo.Entidad.PagoEntity;
import com.example.beautymanager.Modelo.Entidad.ProductoEntity;
import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;
import com.example.beautymanager.Modelo.Enums.EstadoCarritoEnums;
import com.example.beautymanager.Modelo.Enums.EstadoCompraEnums;
import com.example.beautymanager.Modelo.Enums.EstadoMovimientoEnums;
import com.example.beautymanager.Modelo.Enums.EstadoPagoEnums;
import com.example.beautymanager.Modelo.Enums.EstadoProductoEnums;
import com.example.beautymanager.Modelo.Enums.EstadoUsuarioEnums;
import com.example.beautymanager.Modelo.Enums.MetodoPagoEnums;
import com.example.beautymanager.Modelo.Enums.TipoMovimientoEnums;
import com.example.beautymanager.Repositorio.CarritoItemRepository;
import com.example.beautymanager.Repositorio.CarritoRepository;
import com.example.beautymanager.Repositorio.ClienteRepository;
import com.example.beautymanager.Repositorio.CompraRepository;
import com.example.beautymanager.Repositorio.DetalleCompraRepository;
import com.example.beautymanager.Repositorio.MovimientoFinanzasRepository;
import com.example.beautymanager.Repositorio.PagoRepository;
import com.example.beautymanager.Repositorio.ProductoRepository;
import com.example.beautymanager.Servicio.BilleteraService;
import com.example.beautymanager.Servicio.CompraService;
import com.example.beautymanager.Servicio.ProductoService;
import com.example.beautymanager.exception.BusinessException;

import jakarta.transaction.Transactional;

@Service
public class CompraServiceImpl implements CompraService {

    private final ClienteRepository clienteRepository;

    private final CarritoRepository carritoRepository;

    private final CarritoItemRepository carritoItemRepository;

    private final CompraRepository compraRepository;

    private final DetalleCompraRepository detalleCompraRepository;

    private final MovimientoFinanzasRepository movimientoFinanzasRepository;

    private final PagoRepository pagoRepository;

    private final ProductoRepository productoRepository;

    private final ProductoService productoService;

    private final BilleteraService billeteraService;

    @Value("${beautymanager.cobros.admin-usuario-id}")
    private Long idUsuarioAdmin;

    public CompraServiceImpl(
            ClienteRepository clienteRepository,
            CarritoRepository carritoRepository,
            CarritoItemRepository carritoItemRepository,
            CompraRepository compraRepository,
            DetalleCompraRepository detalleCompraRepository,
            MovimientoFinanzasRepository movimientoFinanzasRepository,
            PagoRepository pagoRepository,
            ProductoRepository productoRepository,
            ProductoService productoService,
            BilleteraService billeteraService) {

        this.clienteRepository = clienteRepository;

        this.carritoRepository = carritoRepository;

        this.carritoItemRepository = carritoItemRepository;

        this.compraRepository = compraRepository;

        this.detalleCompraRepository = detalleCompraRepository;

        this.movimientoFinanzasRepository = movimientoFinanzasRepository;

        this.pagoRepository = pagoRepository;

        this.productoRepository = productoRepository;

        this.productoService = productoService;

        this.billeteraService = billeteraService;
    }

    //========================
    //Realizar compra
    //========================
    @Override
    @Transactional
    public ObtenerCompraDTO realizarCompra(Long idCliente, String claveIdempotencia) {

        //========================
        //Validar cliente
        //========================
        if(idCliente == null || idCliente <= 0) {
            throw new BusinessException(
                "El ID del cliente es inválido"
            );
        }

        //========================
        //Bloquear cliente
        //========================
        ClienteEntity cliente = clienteRepository
            .bloquearParaReserva(idCliente)
            .orElseThrow(() ->
                new BusinessException("Cliente no encontrado")
            );

        UsuarioEntity usuario = cliente.getUsuario();

        if(usuario == null ||
           usuario.getEstadoUsuario() != EstadoUsuarioEnums.ACTIVO) {

            throw new BusinessException(
                "La cuenta del cliente no está activa"
            );
        }


        //========================
        //Validar clave de idempotencia
        //========================
        validarClaveIdempotencia(claveIdempotencia);

        //========================
        //Buscar compra anterior
        //========================
        CompraEntity compraAnterior = compraRepository
            .findByClaveIdempotencia(claveIdempotencia)
            .orElse(null);

        if(compraAnterior != null) {

            //========================
            //Verificar propietaria
            //========================
            if(!compraAnterior.getCliente().getId()
                    .equals(cliente.getId())) {

                throw new BusinessException(
                    "La clave de idempotencia pertenece a otra compra"
                );
            }

            //========================
            //Verificar pago
            //========================
            if(compraAnterior.getEstadoCompra()
                    != EstadoCompraEnums.PAGADA ||
               compraAnterior.getPago() == null ||
               compraAnterior.getPago().getEstado()
                    != EstadoPagoEnums.PAGADO) {

                throw new BusinessException(
                    "La compra anterior no tiene un pago confirmado"
                );
            }

            //========================
            //Devolver compra anterior
            //Sin realizar otro cobro
            //Sin descontar nuevamente stock
            //Sin modificar el carrito actual
            //========================
            return toMap(compraAnterior);
        }


        //========================
        //Buscar carrito activo
        //========================
        CarritoEntity carrito = carritoRepository
            .findByUsuarioAndEstado(
                usuario,
                EstadoCarritoEnums.ACTIVO
            )
            .orElseThrow(() ->
                new BusinessException(
                    "No se encontró un carrito activo"
                )
            );

        List<CarritoItemEntity> items = carritoItemRepository
            .findByCarrito(carrito);

        if(items.isEmpty()) {
            throw new BusinessException(
                "El carrito está vacío"
            );
        }

        //========================
        //Ordenar productos
        //========================
        List<CarritoItemEntity> itemsOrdenados =
            new ArrayList<>(items);

        for(CarritoItemEntity item : itemsOrdenados) {

            if(item.getProducto() == null ||
               item.getProducto().getId() == null) {

                throw new BusinessException(
                    "El carrito contiene un producto inválido"
                );
            }
        }

        itemsOrdenados.sort(
            Comparator.comparing(
                item -> item.getProducto().getId()
            )
        );

        //========================
        //Validar productos y total
        //========================
        BigDecimal total = BigDecimal.ZERO;

        Set<Long> productosProcesados = new HashSet<>();

        for(CarritoItemEntity item : itemsOrdenados) {

            Long idProducto = item.getProducto().getId();

            Integer cantidad = item.getCantidad();

            if(cantidad == null || cantidad <= 0) {
                throw new BusinessException(
                    "La cantidad del producto debe ser mayor a cero"
                );
            }

            if(!productosProcesados.add(idProducto)) {
                throw new BusinessException(
                    "El carrito contiene productos duplicados"
                );
            }

            //========================
            //Bloquear producto
            //========================
            ProductoEntity producto = productoRepository
                .buscarPorIdConBloqueo(idProducto)
                .orElseThrow(() ->
                    new BusinessException(
                        "Producto no encontrado"
                    )
                );

            //========================
            //Validar estado
            //========================
            if(producto.getEstadoProducto() !=
               EstadoProductoEnums.ACTIVO) {

                throw new BusinessException(
                    "El producto no está disponible: "
                    + producto.getNombre()
                );
            }

            //========================
            //Validar stock
            //========================
            if(producto.getStock() == null ||
               producto.getStock() < cantidad) {

                throw new BusinessException(
                    "Stock insuficiente para: "
                    + producto.getNombre()
                );
            }

            //========================
            //Validar precio
            //========================
            if(producto.getPrecio() == null ||
               producto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {

                throw new BusinessException(
                    "El producto tiene un precio inválido: "
                    + producto.getNombre()
                );
            }

            if(item.getPrecioUnitario() == null ||
               item.getPrecioUnitario().compareTo(
                   producto.getPrecio()
               ) != 0) {

                throw new BusinessException(
                    "El precio del producto "
                    + producto.getNombre()
                    + " cambió. Actualizá tu carrito antes de comprar."
                );
            }

            //========================
            //Calcular subtotal
            //========================
            BigDecimal subtotal = producto.getPrecio()
                .multiply(BigDecimal.valueOf(cantidad));

            total = total.add(subtotal);
        }

        if(total.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(
                "El total de la compra debe ser mayor a cero"
            );
        }

        //========================
        //Crear compra
        //========================
        
        CompraEntity compra = new CompraEntity();

        compra.setCliente(cliente);

        compra.setTotal(total);

        compra.setEstadoCompra(
            EstadoCompraEnums.PAGADA
        );

        compra.setClaveIdempotencia(
            claveIdempotencia
        );

        compraRepository.saveAndFlush(compra);


        //========================
        //Transferir dinero
        //========================
        billeteraService.transferirSaldo(
            usuario.getId(),
            idUsuarioAdmin,
            total,
            "Compra ID: " + compra.getId()
        );

        //========================
        //Registrar detalles y stock
        //========================
        for(CarritoItemEntity item : itemsOrdenados) {

            Long idProducto = item.getProducto().getId();

            Integer cantidad = item.getCantidad();

            //========================
            //Descontar stock
            //========================
            productoService.descontarStock(
                idProducto,
                cantidad
            );

            //========================
            //Crear detalle
            //========================
            DetalleCompraEntity detalle =
                new DetalleCompraEntity();

            detalle.setCompra(compra);

            detalle.setProducto(item.getProducto());

            detalle.setCantidad(cantidad);

            detalle.setPrecioUnitario(
                item.getPrecioUnitario()
            );

            detalleCompraRepository.save(detalle);

            compra.getDetalleCompra().add(detalle);
        }

        //========================
        //Registrar pago
        //========================
        PagoEntity pago = new PagoEntity();

        pago.setCompra(compra);

        pago.setTurno(null);

        pago.setMonto(total);

        pago.setMetodoPago(
            MetodoPagoEnums.BILLETERA
        );

        pago.setEstado(
            EstadoPagoEnums.PAGADO
        );

        pagoRepository.save(pago);

        compra.setPago(pago);

        //========================
        //Registrar ingreso financiero
        //========================
        MovimientoFinanzasEntity movimientoFinanzas =
            new MovimientoFinanzasEntity();

        movimientoFinanzas.setFechaGasto(
            compra.getFecha()
        );

        movimientoFinanzas.setCompra(compra);

        movimientoFinanzas.setMonto(total);

        movimientoFinanzas.setTipoMovimiento(
            TipoMovimientoEnums.INGRESO
        );

        movimientoFinanzas.setEstado(
            EstadoMovimientoEnums.CONFIRMADO
        );

        movimientoFinanzas.setMetodoPago(
            MetodoPagoEnums.BILLETERA
        );

        movimientoFinanzas.setDescripcion(
            "Venta de productos - Compra ID: "
            + compra.getId()
        );

        movimientoFinanzas.setUsuario(usuario);

        movimientoFinanzas.setTipoGasto(null);

        movimientoFinanzasRepository.save(
            movimientoFinanzas
        );

        //========================
        //Vaciar carrito
        //========================
        carritoItemRepository.deleteAll(items);

        //========================
        //Devolver compra
        //========================
        return toMap(compra);
    }

    //========================
    //Convertir a DTO
    //========================
    @Override
    public ObtenerCompraDTO toMap(CompraEntity compra) {

        ObtenerCompraDTO obtenerCompraDTO =
            new ObtenerCompraDTO();

        obtenerCompraDTO.setIdCompra(
            compra.getId()
        );

        obtenerCompraDTO.setFecha(
            compra.getFecha()
        );

        obtenerCompraDTO.setTotal(
            compra.getTotal()
        );

        obtenerCompraDTO.setEstadoCompra(
            compra.getEstadoCompra()
        );

        //========================
        //Cliente
        //========================
        obtenerCompraDTO.setIdCliente(
            compra.getCliente().getId()
        );

        obtenerCompraDTO.setNombreCliente(
            compra.getCliente().getUsuario().getNombre()
        );

        obtenerCompraDTO.setApellidoCliente(
            compra.getCliente().getUsuario().getApellido()
        );

        //========================
        //Detalles
        //========================
        List<DetalleCompraDTO> detallesDTO =
            new ArrayList<>();

        for(DetalleCompraEntity detalle :
                compra.getDetalleCompra()) {

            DetalleCompraDTO detalleDTO =
                new DetalleCompraDTO();

            detalleDTO.setIdDetalleCompra(
                detalle.getId()
            );

            detalleDTO.setIdProducto(
                detalle.getProducto().getId()
            );

            detalleDTO.setNombreProducto(
                detalle.getProducto().getNombre()
            );

            detalleDTO.setDescripcionProducto(
                detalle.getProducto().getDescripcion()
            );

            detalleDTO.setCantidad(
                detalle.getCantidad()
            );

            detalleDTO.setPrecioUnitario(
                detalle.getPrecioUnitario()
            );

            detalleDTO.setSubtotal(
                detalle.getSubTotal()
            );

            detallesDTO.add(detalleDTO);
        }

        obtenerCompraDTO.setDetalles(detallesDTO);

        return obtenerCompraDTO;
    }


    //========================
    //Validar clave de idempotencia
    //========================
    private void validarClaveIdempotencia(
            String claveIdempotencia) {

        if(claveIdempotencia == null ||
           claveIdempotencia.length() != 36) {

            throw new BusinessException(
                "La clave de idempotencia es inválida"
            );
        }

        try {

            UUID uuid = UUID.fromString(
                claveIdempotencia
            );

            if(!uuid.toString().equals(
                    claveIdempotencia)) {

                throw new BusinessException(
                    "La clave de idempotencia debe ser un UUID válido"
                );
            }

        } catch (IllegalArgumentException e) {

            throw new BusinessException(
                "La clave de idempotencia debe ser un UUID válido"
            );
        }
    }

    //========================
    //Obtener historial de compras
    //========================
    @Override
    @Transactional
    public List<ObtenerCompraDTO> obtenerMisCompras(
            Long idCliente) {

        //========================
        //Validar cliente
        //========================
        if(idCliente == null || idCliente <= 0) {
            throw new BusinessException(
                "El ID del cliente es inválido"
            );
        }

        //========================
        //Buscar cliente
        //========================
        ClienteEntity cliente = clienteRepository
            .findById(idCliente)
            .orElseThrow(() ->
                new BusinessException("Cliente no encontrado")
            );

        //========================
        //Obtener compras del cliente
        //========================
        List<CompraEntity> compras = compraRepository
            .findByCliente(cliente);

        //========================
        //Ordenar por fecha descendente
        //Y convertir a DTO
        //========================
        return compras.stream()
            .sorted(
                Comparator.comparing(
                    CompraEntity::getFecha,
                    Comparator.nullsLast(
                        Comparator.reverseOrder()
                    )
                )
            )
            .map(this::toMap)
            .toList();
    }


    //========================
    //Obtener una compra del cliente
    //========================
    @Override
    @Transactional
    public ObtenerCompraDTO obtenerMiCompra(
            Long idCliente,
            Long idCompra) {

        //========================
        //Validar cliente
        //========================
        if(idCliente == null || idCliente <= 0) {
            throw new BusinessException(
                "El ID del cliente es inválido"
            );
        }

        //========================
        //Validar compra
        //========================
        if(idCompra == null || idCompra <= 0) {
            throw new BusinessException(
                "El ID de la compra es inválido"
            );
        }

        //========================
        //Buscar compra
        //========================
        CompraEntity compra = compraRepository
            .findById(idCompra)
            .orElseThrow(() ->
                new BusinessException(
                    "Compra no encontrada"
                )
            );

        //========================
        //Validar pertenencia
        //========================
        if(compra.getCliente() == null ||
           !idCliente.equals(
               compra.getCliente().getId()
           )) {

            throw new BusinessException(
                "Compra no encontrada"
            );
        }

        //========================
        //Convertir a DTO
        //========================
        return toMap(compra);
    }


}
