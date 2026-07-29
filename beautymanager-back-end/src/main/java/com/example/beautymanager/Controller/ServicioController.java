package com.example.beautymanager.Controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.beautymanager.Modelo.DTO.ActualizarServicioDTO;
import com.example.beautymanager.Modelo.DTO.CrearServicioDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerServicioDTO;
import com.example.beautymanager.Servicio.ServicioService;

@RestController
@RequestMapping("/servicio")
public class ServicioController {

    @Autowired
    private ServicioService servicioService;

    //=======================
    //Crear servicio
    //=======================
    @PostMapping("/crear")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ObtenerServicioDTO> crearServicio(@RequestBody CrearServicioDTO crearServicio){
        
        ObtenerServicioDTO response = servicioService.crearServicio(crearServicio);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //=======================
    //Actualizar servicio
    //=======================
    @PutMapping("/actualizar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ObtenerServicioDTO> actualizarServicio(@PathVariable Long id, @RequestBody ActualizarServicioDTO actualizarServicio){
        return ResponseEntity.ok(servicioService.actualizarServicio(id, actualizarServicio));
    }

    //=======================
    //Eliminar servicio
    //=======================
    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> eliminarServicio(@PathVariable Long id){

        servicioService.eliminarServicio(id);

        return ResponseEntity.ok("Servicio eliminado correctamente");
    }

    //=======================
    //Obtener por id
    //=======================
    @GetMapping("/id/{id}")
    public ResponseEntity<ObtenerServicioDTO> obtenerPorId(@PathVariable Long id){
        return ResponseEntity.ok(servicioService.obtenerPorId(id));
    }

    //=======================
    //Listar activos
    //=======================
    @GetMapping("/listar-activos")
    public ResponseEntity<List<ObtenerServicioDTO>> listarActivos(){
        return ResponseEntity.ok(servicioService.listarActivos());
    }

    //=======================
    //Listar inactivos
    //=======================
    @GetMapping("/listar-inactivos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ObtenerServicioDTO>> listarInactivos(){
        return ResponseEntity.ok(servicioService.listarInactivos());
    }

    //=======================
    //Buscar por nombre
    //=======================
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<List<ObtenerServicioDTO>> buscarPorNombre(@PathVariable String nombre){
        return ResponseEntity.ok(servicioService.buscarPorNombre(nombre));
    }

    //=======================
    //Buscar por descripcion
    //=======================
    @GetMapping("/descripcion/{descripcion}")
    public ResponseEntity<List<ObtenerServicioDTO>> buscarPorDescripcion(@PathVariable String descripcion){
        return ResponseEntity.ok(servicioService.buscarPorDescripcion(descripcion));
    }

    //=======================
    //Buscar por categoria
    //=======================
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ObtenerServicioDTO>> buscarPorCategoria(@PathVariable String categoria){
        return ResponseEntity.ok(servicioService.buscarPorCategoria(categoria));
    }

    //===============================
    //Buscar activos por categoria
    //===============================
    @GetMapping("/categoria-activa/{categoria}")
    public ResponseEntity<List<ObtenerServicioDTO>> buscarActivosPorCategoria(@PathVariable String categoria){
        return ResponseEntity.ok(servicioService.buscarActivosPorCategoria(categoria));
    }

    //=======================
    //Buscar rango de precio
    //=======================
    @GetMapping("/precio-rango/{min}/{max}")
    public ResponseEntity<List<ObtenerServicioDTO>> buscarPorRangoPrecio(@PathVariable BigDecimal min, @PathVariable BigDecimal max){
        return ResponseEntity.ok(servicioService.buscarPorRangoPrecio(min, max));
    }

    //=======================
    //Precio maximo
    //=======================
    @GetMapping("/precio-max/{precio}")
    public ResponseEntity<List<ObtenerServicioDTO>> buscarPrecioMaximo(@PathVariable BigDecimal precio){
        return ResponseEntity.ok(servicioService.buscarPrecioMaximo(precio));
    }

    //=======================
    //Precio minimo
    //=======================
    @GetMapping("/precio-min/{precio}")
    public ResponseEntity<List<ObtenerServicioDTO>> buscarPrecioMinimo(@PathVariable BigDecimal precio){
        return ResponseEntity.ok(servicioService.buscarPrecioMinimo(precio));
    }

    //=======================
    //Buscar por duracion
    //=======================
    @GetMapping("/duracion/{minutos}")
    public ResponseEntity<List<ObtenerServicioDTO>> buscarPorDuracion(@PathVariable Integer minutos){
        return ResponseEntity.ok(servicioService.buscarPorDuracion(minutos));
    }

    //=======================
    //Duracion maxima
    //=======================
    @GetMapping("/duracion-max/{minutos}")
    public ResponseEntity<List<ObtenerServicioDTO>> buscarDuracionMaxima(@PathVariable Integer minutos){
        return ResponseEntity.ok(servicioService.buscarDuracionMaxima(minutos));
    }

    //=======================
    //Duracion minima
    //=======================
    @GetMapping("/duracion-min/{minutos}")
    public ResponseEntity<List<ObtenerServicioDTO>> buscarDuracionMinima(@PathVariable Integer minutos){
        return ResponseEntity.ok(servicioService.buscarDuracionMinima(minutos));
    }

    //=======================
    //Rango duracion
    //=======================
    @GetMapping("/duracion-rango/{min}/{max}")
    public ResponseEntity<List<ObtenerServicioDTO>> buscarPorRangoDuracion(@PathVariable Integer min, @PathVariable Integer max){
        return ResponseEntity.ok(servicioService.buscarPorRangoDuracion(min, max));
    }
}
