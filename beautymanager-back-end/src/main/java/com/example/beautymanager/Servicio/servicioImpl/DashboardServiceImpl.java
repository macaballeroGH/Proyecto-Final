package com.example.beautymanager.Servicio.servicioImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.DTO.ObtenerDashboardDTO;
import com.example.beautymanager.Repositorio.ClienteRepository;
import com.example.beautymanager.Repositorio.CompraRepository;
import com.example.beautymanager.Repositorio.EmpleadoRepository;
import com.example.beautymanager.Repositorio.ProductoRepository;
import com.example.beautymanager.Repositorio.TurnoRepository;
import com.example.beautymanager.Servicio.DashboardService;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final ClienteRepository clienteRepository;

    private final EmpleadoRepository empleadoRepository;

    private final ProductoRepository productoRepository;

    private final CompraRepository compraRepository;

    private final TurnoRepository turnoRepository;

    public DashboardServiceImpl(ClienteRepository clienteRepository, EmpleadoRepository empleadoRepository, ProductoRepository productoRepository, CompraRepository compraRepository, TurnoRepository turnoRepository){

        this.clienteRepository = clienteRepository;

        this.empleadoRepository = empleadoRepository;

        this.productoRepository = productoRepository;

        this.compraRepository = compraRepository;

        this.turnoRepository = turnoRepository;
    }

    @Override
    public ObtenerDashboardDTO obtenerDashboard() {

        ObtenerDashboardDTO obtenerDashboardDTO = new ObtenerDashboardDTO();

        obtenerDashboardDTO.setTotalClientes(clienteRepository.count());
        obtenerDashboardDTO.setTotalEmpleados(empleadoRepository.count());
        obtenerDashboardDTO.setTotalProductos(productoRepository.count());
        obtenerDashboardDTO.setTotalCompras(compraRepository.count());
        obtenerDashboardDTO.setTotalTurnos(turnoRepository.count());

        //===================
        //Ingresos totales
        //===================
        BigDecimal ingresos = compraRepository.findAll().stream().map(compra -> compra.getTotal()).reduce(BigDecimal.ZERO, BigDecimal::add);
        obtenerDashboardDTO.setIngresosTotales(ingresos);

        //======================
        //Productos sin stock
        //======================
        Long sinStock = productoRepository.findAll().stream().filter(producto -> producto.getStock() != null && producto.getStock() <= 0).count();
        obtenerDashboardDTO.setProductosSinStock(sinStock);

        //===================
        //Turnos de hoy
        //===================
        LocalDate hoy = LocalDate.now();

        LocalDateTime inicio = hoy.atStartOfDay();

        LocalDateTime fin = hoy.atTime(23,59,59);

        Long turnosHoy = turnoRepository.findByFechaHoraInicioBetween(inicio, fin).stream().count();

        obtenerDashboardDTO.setTurnosHoy(turnosHoy);

        return obtenerDashboardDTO;
    }

    
}
