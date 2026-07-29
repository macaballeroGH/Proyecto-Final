package com.example.beautymanager.Controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.beautymanager.Modelo.DTO.ActualizarMovimientoFinanzasDTO;
import com.example.beautymanager.Modelo.DTO.CrearMovimientoFinanzasDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerMovimientoFinanzasDTO;
import com.example.beautymanager.Modelo.Enums.EstadoMovimientoEnums;
import com.example.beautymanager.Modelo.Enums.TipoMovimientoEnums;
import com.example.beautymanager.Servicio.MovimientoFinanzasService;

@RestController
@RequestMapping("/movimiento-finanzas")
@PreAuthorize("hasRole('ADMIN')")
public class MovimientoFinanzasController {

    @Autowired
    private MovimientoFinanzasService movimientoFinanzasService;

    //=======================
    //Crear movimiento
    //=======================
    @PostMapping("/crear")
    public ResponseEntity<ObtenerMovimientoFinanzasDTO> crearMovimiento(@RequestBody CrearMovimientoFinanzasDTO crearMovimiento){
        return ResponseEntity.ok(movimientoFinanzasService.crearMovimiento(crearMovimiento));
    }

    //=======================
    //Actualizar movimiento
    //=======================
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<ObtenerMovimientoFinanzasDTO> actualizarMovimiento(@PathVariable Long id, @RequestBody ActualizarMovimientoFinanzasDTO actualizarMovimiento){
        return ResponseEntity.ok(movimientoFinanzasService.actualizarMovimiento(id, actualizarMovimiento));
    }

    //=======================
    //Buscar por ID
    //=======================
    @GetMapping("/id/{id}")
    public ResponseEntity<ObtenerMovimientoFinanzasDTO> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(movimientoFinanzasService.buscarPorId(id));
    }

    //=======================
    //Listar todos
    //=======================
    @GetMapping("/listar")
    public ResponseEntity<List<ObtenerMovimientoFinanzasDTO>> listarTodos(){
        return ResponseEntity.ok(movimientoFinanzasService.listarTodos());
    }

    //=======================
    //Eliminar movimiento
    //=======================
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminarMovimiento(@PathVariable Long id){

        movimientoFinanzasService.eliminarMovimiento(id);

        return ResponseEntity.ok("Movimiento eliminado correctamente");
    }

    //=======================
    //Buscar por fecha
    //=======================
    @GetMapping("/fecha")
    public ResponseEntity<List<ObtenerMovimientoFinanzasDTO>> buscarPorFecha(@RequestParam LocalDateTime fecha){
        return ResponseEntity.ok(movimientoFinanzasService.buscarPorFecha(fecha));
    }

    //===========================
    //Buscar por rango fechas
    //===========================
    @GetMapping("/rango-fechas")
    public ResponseEntity<List<ObtenerMovimientoFinanzasDTO>> buscarPorRangoFechas(@RequestParam LocalDateTime inicio, @RequestParam LocalDateTime fin){
        return ResponseEntity.ok(movimientoFinanzasService.buscarPorRangoFechas(inicio, fin));
    }

    //===============================
    //Buscar por tipo movimiento
    //===============================
    @GetMapping("/tipo/{tipoMovimiento}")
    public ResponseEntity<List<ObtenerMovimientoFinanzasDTO>> buscarPorTipoMovimiento(@PathVariable TipoMovimientoEnums tipoMovimiento){
        return ResponseEntity.ok(movimientoFinanzasService.buscarPorTipoMovimiento(tipoMovimiento));
    }

    //=======================
    //Buscar por usuario
    //=======================
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<ObtenerMovimientoFinanzasDTO>> buscarPorUsuario(@PathVariable Long idUsuario){
        return ResponseEntity.ok(movimientoFinanzasService.buscarPorUsuario(idUsuario));
    }

    //=======================
    //Buscar por tipo gasto
    //=======================
    @GetMapping("/tipo-gasto/{idTipoGasto}")
    public ResponseEntity<List<ObtenerMovimientoFinanzasDTO>> buscarPorTipoGasto(@PathVariable Long idTipoGasto){
        return ResponseEntity.ok(movimientoFinanzasService.buscarPorTipoGasto(idTipoGasto));
    }

    //=======================
    //Buscar por compra
    //=======================
    @GetMapping("/compra/{idCompra}")
    public ResponseEntity<List<ObtenerMovimientoFinanzasDTO>> buscarPorCompra(@PathVariable Long idCompra){
        return ResponseEntity.ok(movimientoFinanzasService.buscarPorCompra(idCompra));
    }

    //=======================
    //Buscar por estado
    //=======================
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ObtenerMovimientoFinanzasDTO>> buscarPorEstado(@PathVariable EstadoMovimientoEnums estado){
        return ResponseEntity.ok(movimientoFinanzasService.buscarPorEstado(estado));
    }

    //=========================
    //Buscar por descripcion
    //=========================
    @GetMapping("/descripcion")
    public ResponseEntity<List<ObtenerMovimientoFinanzasDTO>> buscarPorDescripcion(@RequestParam String descripcion){
        return ResponseEntity.ok(movimientoFinanzasService.buscarPorDescripcion(descripcion));
    }

    //=======================
    //Total ingresos
    //=======================
    @GetMapping("/total-ingresos")
    public ResponseEntity<BigDecimal> totalIngresos(@RequestParam LocalDateTime inicio, @RequestParam LocalDateTime fin){
        return ResponseEntity.ok(movimientoFinanzasService.totalIngresos(inicio, fin));
    }

    //=======================
    //Total egresos
    //=======================
    @GetMapping("/total-egresos")
    public ResponseEntity<BigDecimal> totalEgresos(@RequestParam LocalDateTime inicio, @RequestParam LocalDateTime fin){
        return ResponseEntity.ok(movimientoFinanzasService.totalEgresos(inicio, fin));
    }

    //=======================
    //Balance
    //=======================
    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> balance(@RequestParam LocalDateTime inicio, @RequestParam LocalDateTime fin){
        return ResponseEntity.ok(movimientoFinanzasService.balance(inicio, fin));
    }
}