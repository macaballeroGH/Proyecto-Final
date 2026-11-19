package com.example.beautymanager.Controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.beautymanager.Modelo.DTO.ActualizarProductoDTO;
import com.example.beautymanager.Modelo.DTO.CrearProductoDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerProductoDTO;
import com.example.beautymanager.Servicio.ProductoService;

@RestController
@RequestMapping("/producto")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService){
        this.productoService = productoService;
    }

    //=======================
    //Crear producto
    //=======================
    @PostMapping("/crear")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ObtenerProductoDTO> crearProducto(@RequestBody CrearProductoDTO crearProducto){

        ObtenerProductoDTO response = productoService.crearProducto(crearProducto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //=======================
    //Actualizar producto
    //=======================
    @PutMapping("/actualizar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ObtenerProductoDTO> actualizarProducto(@PathVariable Long id, @RequestBody ActualizarProductoDTO actualizarProducto){
        return ResponseEntity.ok(productoService.actualizarProducto(id, actualizarProducto));
    }

    //=======================
    //Eliminar producto
    //=======================
    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> eliminarProducto(@PathVariable Long id){

        productoService.eliminarProducto(id);

        return ResponseEntity.ok("Producto eliminado correctamente");
    }

    //=======================
    //Obtener por id
    //=======================
    @GetMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<ObtenerProductoDTO> obtenerPorId(@PathVariable Long id){
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    //=======================
    //Listar productos
    //=======================
    @GetMapping("/lista")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<List<ObtenerProductoDTO>> listarProducto(){
        return ResponseEntity.ok(productoService.listarProducto());
    }

    //=======================
    //Actualizar foto producto
    //=======================
    @PostMapping("/{idProducto}/foto")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> actualizarFotoProducto(@PathVariable Long idProducto, @RequestParam("foto") MultipartFile foto) {

        productoService.actualizarFotoProducto(idProducto, foto);

        return ResponseEntity.ok("Fotografía del producto actualizada correctamente");
    }

    //=======================
    //Eliminar foto producto
    //=======================
    @DeleteMapping("/{idProducto}/foto")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> eliminarFotoProducto(@PathVariable Long idProducto) {

        productoService.eliminarFotoProducto(idProducto);

        return ResponseEntity.ok("Fotografía del producto eliminada correctamente");
    }

    //=======================
    //Aumentar stock
    //=======================
    @PutMapping("/aumentar-stock/{idProducto}/{cantidad}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> aumentarStock(@PathVariable Long idProducto, @PathVariable Integer cantidad){

        productoService.aumentarStock(idProducto, cantidad);

        return ResponseEntity.ok("Stock aumentado correctamente");
    }
}
