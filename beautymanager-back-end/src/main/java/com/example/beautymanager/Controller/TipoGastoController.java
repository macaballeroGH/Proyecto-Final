package com.example.beautymanager.Controller;

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

import com.example.beautymanager.Modelo.DTO.ActualizarTipoGastoDTO;
import com.example.beautymanager.Modelo.DTO.CrearTipoGastoDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerTipoGastoDTO;
import com.example.beautymanager.Servicio.TipoGastoService;

@RestController
@RequestMapping("/tipo-gasto")
@PreAuthorize("hasRole('ADMIN')")
public class TipoGastoController {

    @Autowired
    private TipoGastoService tipoGastoService;

    //=======================
    //Crear tipo de gasto
    //=======================
    @PostMapping("/crear")
    public ResponseEntity<ObtenerTipoGastoDTO> crearTipoGasto(@RequestBody CrearTipoGastoDTO crearTipoGasto){
        return ResponseEntity.ok(tipoGastoService.crearTipoGasto(crearTipoGasto));
    }

    //===========================
    //Actualizar tipo de gasto
    //===========================
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<ObtenerTipoGastoDTO> actualizarTipoGasto(@PathVariable Long id, @RequestBody ActualizarTipoGastoDTO actualizarTipoGasto){
        return ResponseEntity.ok(tipoGastoService.actualizarTipoGasto(id, actualizarTipoGasto));
    }

    //=========================
    //Eliminar tipo de gasto
    //=========================
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminarTipoGasto(@PathVariable Long id){

        tipoGastoService.eliminarTipoGasto(id);

        return ResponseEntity.ok("Tipo de gasto eliminado correctamente");
    }

    //=======================
    //Buscar po ID
    //=======================
    @GetMapping("/id/{id}")
    public ResponseEntity<ObtenerTipoGastoDTO> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(tipoGastoService.buscarPorId(id));
    }

    //=======================
    //Listar todos
    //=======================
    @GetMapping("/listar")
    public ResponseEntity<List<ObtenerTipoGastoDTO>> listarTodos() {
        return ResponseEntity.ok(tipoGastoService.listarTodos());
    }

    //=======================
    //Listar ordenados
    //=======================
    @GetMapping("/listar-ordenados")
    public ResponseEntity<List<ObtenerTipoGastoDTO>> listarOrdenados(){
        return ResponseEntity.ok(tipoGastoService.listarOrdenados());
    }

    //=======================
    //Buscar por nombre
    //=======================
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<List<ObtenerTipoGastoDTO>> buscarPorNombre(@PathVariable String nombre){
        return ResponseEntity.ok(tipoGastoService.buscarPorNombre(nombre));
    }

    //=========================
    //Buscar por descripcion
    //=========================
    @GetMapping("/descripcion")
    public ResponseEntity<List<ObtenerTipoGastoDTO>> buscarPorDescripcion(@RequestParam String descripcion){
        return ResponseEntity.ok(tipoGastoService.buscarPorDescripcion(descripcion));
    }

    //=======================
    //Verificar existencia
    //=======================
    @GetMapping("/existe")
    public ResponseEntity<Boolean> existePorNombre(@RequestParam String nombre){
        return ResponseEntity.ok(tipoGastoService.existePorNombre(nombre));
    }
}
