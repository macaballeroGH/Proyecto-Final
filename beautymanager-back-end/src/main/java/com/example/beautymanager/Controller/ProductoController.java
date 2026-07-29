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

import com.example.beautymanager.Modelo.DTO.ActualizarProductoDTO;
import com.example.beautymanager.Modelo.DTO.CrearProductoDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerProductoDTO;
import com.example.beautymanager.Servicio.ProductoService;

@RestController
@RequestMapping("/producto")
@PreAuthorize("hasRole('ADMIN')")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    //=======================
    //Crear producto
    //=======================
    @PostMapping("/crear")
    public ResponseEntity<ObtenerProductoDTO> crearProducto(@RequestBody CrearProductoDTO crearProducto){

        ObtenerProductoDTO response = productoService.crearProducto(crearProducto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //=======================
    //Actualizar producto
    //=======================
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<ObtenerProductoDTO> actualizarProducto(@PathVariable Long id, @RequestBody ActualizarProductoDTO actualizarProducto){
        return ResponseEntity.ok(productoService.actualizarProducto(id, actualizarProducto));
    }

    //=======================
    //Eliminar producto
    //=======================
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminarProducto(@PathVariable Long id){

        productoService.eliminarProducto(id);

        return ResponseEntity.ok("Producto eliminado correctamente");
    }

    //=======================
    //Obtener por id
    //=======================
    @GetMapping("/id/{id}")
    public ResponseEntity<ObtenerProductoDTO> obtenerPorId(@PathVariable Long id){
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    //=======================
    //Listar productos
    //=======================
    @GetMapping("/lista")
    public ResponseEntity<List<ObtenerProductoDTO>> listarProducto(){
        return ResponseEntity.ok(productoService.listarProducto());
    }

    //=======================
    //Aumentar stock
    //=======================
    @PutMapping("/aumentar-stock/{idProducto}/{cantidad}")
    public ResponseEntity<String> aumentarStock(@PathVariable Long idProducto, @PathVariable Integer cantidad){

        productoService.aumentarStock(idProducto, cantidad);

        return ResponseEntity.ok("Stock aumentado correctamente");
    }
}
