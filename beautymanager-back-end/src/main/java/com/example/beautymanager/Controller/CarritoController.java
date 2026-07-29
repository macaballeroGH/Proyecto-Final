package com.example.beautymanager.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.beautymanager.Modelo.DTO.ActualizarCantidadCarritoDTO;
import com.example.beautymanager.Modelo.DTO.AgregarProductoCarritoDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerCarritoDTO;
import com.example.beautymanager.Servicio.CarritoService;

@RestController
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    //=======================
    //Obtener carrito
    //=======================
    @GetMapping("/mi-carrito")
    public ResponseEntity<ObtenerCarritoDTO> obtenerCarrito(){

        String email = SecurityContextHolder

                .getContext()
                .getAuthentication()
                .getName();

        return ResponseEntity.ok(carritoService.obtenerCarrito(email));
    }

    //=======================
    //Agregar producto
    //=======================
    @PostMapping("/agregar-producto")
    public ResponseEntity<ObtenerCarritoDTO> agregarProducto(@RequestBody AgregarProductoCarritoDTO agregarProductoCarrito){

        String email = SecurityContextHolder

                .getContext()
                .getAuthentication()
                .getName();

        return ResponseEntity.ok(carritoService.agregarProducto(email, agregarProductoCarrito));
    }

    //=======================
    //Eliminar producto
    //=======================
    @DeleteMapping("/eliminar-producto/{idProducto}")
    public ResponseEntity<ObtenerCarritoDTO> eliminarProducto(@PathVariable Long idProducto){

        String email = SecurityContextHolder

                .getContext()
                .getAuthentication()
                .getName();

        return ResponseEntity.ok(carritoService.eliminarProducto(email, idProducto));
    }

    //=======================
    //Actualizar cantidad
    //=======================
    @PutMapping("/actualizar-cantidad")
    public ResponseEntity<ObtenerCarritoDTO> actualizarCantidad(@RequestBody ActualizarCantidadCarritoDTO actualizarCantidadCarrito){

        String email = SecurityContextHolder
            
                .getContext()
                .getAuthentication()
                .getName();

        return ResponseEntity.ok(carritoService.actualizarCantidad(email, actualizarCantidadCarrito));
    }
}
