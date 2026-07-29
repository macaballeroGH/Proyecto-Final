package com.example.beautymanager.Servicio.servicioImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.DTO.DetalleCompraDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerCompraDTO;
import com.example.beautymanager.Modelo.Entidad.BilleteraEntity;
import com.example.beautymanager.Modelo.Entidad.CarritoEntity;
import com.example.beautymanager.Modelo.Entidad.CarritoItemEntity;
import com.example.beautymanager.Modelo.Entidad.ClienteEntity;
import com.example.beautymanager.Modelo.Entidad.CompraEntity;
import com.example.beautymanager.Modelo.Entidad.DetalleCompraEntity;
import com.example.beautymanager.Modelo.Entidad.MovimientoBilleteraEntity;
import com.example.beautymanager.Modelo.Entidad.MovimientoFinanzasEntity;
import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;
import com.example.beautymanager.Modelo.Enums.EstadoCarritoEnums;
import com.example.beautymanager.Modelo.Enums.EstadoCompraEnums;
import com.example.beautymanager.Modelo.Enums.EstadoMovimientoEnums;
import com.example.beautymanager.Modelo.Enums.MovimientoBilleteraEnums;
import com.example.beautymanager.Modelo.Enums.TipoMovimientoEnums;
import com.example.beautymanager.Repositorio.BilleteraRepository;
import com.example.beautymanager.Repositorio.CarritoItemRepository;
import com.example.beautymanager.Repositorio.CarritoRepository;
import com.example.beautymanager.Repositorio.ClienteRepository;
import com.example.beautymanager.Repositorio.CompraRepository;
import com.example.beautymanager.Repositorio.DetalleCompraRepository;
import com.example.beautymanager.Repositorio.MovimientoBilleteraRepository;
import com.example.beautymanager.Repositorio.MovimientoFinanzasRepository;
import com.example.beautymanager.Servicio.CompraService;
import com.example.beautymanager.Servicio.ProductoService;
import com.example.beautymanager.exception.BusinessException;

import jakarta.transaction.Transactional;

@Service
public class CompraServiceImpl implements CompraService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private CarritoItemRepository carritoItemRepository;

    @Autowired
    private BilleteraRepository billeteraRepository;

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private DetalleCompraRepository detalleCompraRepository;

    @Autowired
    private MovimientoBilleteraRepository movimientoBilleteraRepository;

    @Autowired
    private MovimientoFinanzasRepository movimientoFinanzasRepository;

    @Autowired
    private ProductoService productoService;

    //==================
    //Realizar Compra
    //==================
    @Override
    @Transactional
    public ObtenerCompraDTO realizarCompra(Long idCliente) {

        //==================
        //Cliente
        //==================
        ClienteEntity cliente = clienteRepository.findById(idCliente)
            .orElseThrow(() -> new BusinessException("Cliente no encontrado"));

        //===================
        //Usuario
        //===================
        UsuarioEntity usuario = cliente.getUsuario();

        //==================
        //Carrito
        //==================
        CarritoEntity carrito = carritoRepository.findByUsuarioAndEstado(usuario, EstadoCarritoEnums.ACTIVO)
            .orElseThrow(() -> new BusinessException("Carrito no encontrado"));

        List<CarritoItemEntity> items = carritoItemRepository.findByCarrito(carrito);

        if (items.isEmpty()) {
            throw new BusinessException("El carrito esta vacio");
        }

        //==================
        //Validar stock
        //==================
        for (CarritoItemEntity item : items){
            if (item.getProducto().getStock() < item.getCantidad()) {
                throw new BusinessException("Stock insuficiente: " + item.getProducto().getNombre());
            }
        }

        //==================
        //Calcular total
        //==================
        BigDecimal total = BigDecimal.ZERO;

        for (CarritoItemEntity item : items) {
            BigDecimal subtotal = item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad()));

            total = total.add(subtotal);
        }

        //==================
        //Billetera
        //==================
        BilleteraEntity billetera = billeteraRepository.findByUsuario(usuario)
            .orElseThrow(() -> new BusinessException("Billetera no encontrada"));

        if (billetera.getSaldo().compareTo(total) < 0) {
            throw new BusinessException("Saldo insuficiente");
        }

        //descontar saldo
        billetera.setSaldo(billetera.getSaldo().subtract(total));
        billeteraRepository.save(billetera);

        //==================
        //Crear compra
        //==================
        CompraEntity compra = new CompraEntity();

        compra.setCliente(cliente);
        compra.setTotal(total);
        compra.setEstadoCompra(EstadoCompraEnums.PAGADA);

        compraRepository.save(compra);

        //==================
        //Detalle compra
        //==================
        for (CarritoItemEntity item : items) {

            productoService.descontarStock(item.getProducto().getId(), item.getCantidad());

            DetalleCompraEntity detalle = new DetalleCompraEntity();
            detalle.setCompra(compra);
            detalle.setProducto(item.getProducto());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecioUnitario());

            detalleCompraRepository.save(detalle);
        }

        //=======================
        //Movimiento billetera
        //=======================
        MovimientoBilleteraEntity movimientoBilletera = new MovimientoBilleteraEntity();
        movimientoBilletera.setBilletera(billetera);
        movimientoBilletera.setMonto(total);
        movimientoBilletera.setTipoMovimiento(MovimientoBilleteraEnums.DEBITO);
        movimientoBilletera.setDescripcion("Compra realizada");
        movimientoBilletera.setCompra(compra);

        movimientoBilleteraRepository.save(movimientoBilletera);

        //=======================
        //Movimiento finanzas
        //=======================
        MovimientoFinanzasEntity movimientoFinanzas = new MovimientoFinanzasEntity();
        movimientoFinanzas.setCompra(compra);
        movimientoFinanzas.setMonto(total);
        movimientoFinanzas.setTipoMovimiento(TipoMovimientoEnums.INGRESO);
        movimientoFinanzas.setEstado(EstadoMovimientoEnums.CONFIRMADO);
        movimientoFinanzas.setDescripcion("Compra realizada");
        movimientoFinanzas.setUsuario(usuario);
        movimientoFinanzas.setTipoGasto(null);

        movimientoFinanzasRepository.save(movimientoFinanzas);

        //=======================
        //Limpiar carrito
        //=======================
        carritoItemRepository.deleteAll(items);

        return toMap(compra);
    }

    @Override
    public ObtenerCompraDTO toMap(CompraEntity compra) {

        ObtenerCompraDTO obtenerCompraDTO = new ObtenerCompraDTO();

        obtenerCompraDTO.setIdCompra(compra.getId());
        obtenerCompraDTO.setFecha(compra.getFecha());
        obtenerCompraDTO.setTotal(compra.getTotal());
        obtenerCompraDTO.setEstadoCompra(compra.getEstadoCompra());

        //===================
        //Cliente
        //===================
        obtenerCompraDTO.setIdCliente(compra.getCliente().getId());
        obtenerCompraDTO.setNombreCliente(compra.getCliente().getUsuario().getNombre());
        obtenerCompraDTO.setApellidoCliente(compra.getCliente().getUsuario().getApellido());

        //===================
        //Detalles
        //===================
        List<DetalleCompraDTO> detallesDTO = new ArrayList<>();

        for (DetalleCompraEntity detalle : compra.getDetalleCompra()) {

            DetalleCompraDTO detalleDTO = new DetalleCompraDTO();

            detalleDTO.setIdDetalleCompra(detalle.getId());
            detalleDTO.setIdProducto(detalle.getProducto().getId());
            detalleDTO.setNombreProducto(detalle.getProducto().getNombre());
            detalleDTO.setDescripcionProducto(detalle.getProducto().getDescripcion());
            detalleDTO.setCantidad(detalle.getCantidad());
            detalleDTO.setPrecioUnitario(detalle.getPrecioUnitario());
            detalleDTO.setSubtotal(detalle.getSubTotal());

            detallesDTO.add(detalleDTO);
        }

        obtenerCompraDTO.setDetalles(detallesDTO);

        return obtenerCompraDTO;
    } 
}
