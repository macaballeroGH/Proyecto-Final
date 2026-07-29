package com.example.beautymanager.Controller;

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

import com.example.beautymanager.Modelo.DTO.ActualizarCategoriaServicioDTO;
import com.example.beautymanager.Modelo.DTO.CrearCategoriaServicioDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerCategoriaServicioDTO;
import com.example.beautymanager.Servicio.CategoriaServicioService;

@RestController
@RequestMapping("/categoria-servicio")
public class CategoriaServicioController {

    @Autowired
    private CategoriaServicioService categoriaServicioService;
    
    //=======================
    //Crear categoria
    //=======================
    @PostMapping("/crear")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ObtenerCategoriaServicioDTO> crearCategoria(@RequestBody CrearCategoriaServicioDTO crearCategoria){
        
        ObtenerCategoriaServicioDTO response = categoriaServicioService.crearCategoria(crearCategoria);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //=======================
    //Actualizar categoria
    //=======================
    @PutMapping("/actualizar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ObtenerCategoriaServicioDTO> actualizarCategoria(@PathVariable Long id, @RequestBody ActualizarCategoriaServicioDTO actualizarCategoria){
        return ResponseEntity.ok(categoriaServicioService.actualizarCategoria(id, actualizarCategoria));
    }

    //=======================
    //Eliminar categoria
    //=======================
    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> eliminarCCategoria(@PathVariable Long id){

        categoriaServicioService.eliminarCategoria(id);

        return ResponseEntity.ok("Categoria eliminada correctamente");
    }

    //=======================
    //Obtener por id
    //=======================
    @GetMapping("/id/{id}")
    public ResponseEntity<ObtenerCategoriaServicioDTO> obtenerPorId(@PathVariable Long id){
        return ResponseEntity.ok(categoriaServicioService.obtenerPorId(id));
    }

    //=======================
    //Listar todas
    //=======================
    @GetMapping("/listar")
    public ResponseEntity<List<ObtenerCategoriaServicioDTO>> listarTodas(){
        return ResponseEntity.ok(categoriaServicioService.listarTodas());
    }

    //=======================
    //Listar activas
    //=======================
    @GetMapping("/listar-activas")
    public ResponseEntity<List<ObtenerCategoriaServicioDTO>> listarActivas(){
        return ResponseEntity.ok(categoriaServicioService.listarActivas());
    }

    //=======================
    //Listar inactivas
    //=======================
    @GetMapping("/listar-inactivas")
    public ResponseEntity<List<ObtenerCategoriaServicioDTO>> listarInactivas(){
        return ResponseEntity.ok(categoriaServicioService.listarInactivas());
    }

    //=======================
    //Buscar por nombre
    //=======================
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<List<ObtenerCategoriaServicioDTO>> buscarPorNombre(@PathVariable String nombre){
        return ResponseEntity.ok(categoriaServicioService.buscarPorNombre(nombre));
    }

    //=======================
    //Buscar por descripcion
    //=======================
    @GetMapping("/descripcion/{descripcion}")
    public ResponseEntity<List<ObtenerCategoriaServicioDTO>> buscarPorDescripcion(@PathVariable String descripcion){
        return ResponseEntity.ok(categoriaServicioService.buscarPorDescripcion(descripcion));
    }
}
