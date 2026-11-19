package com.example.beautymanager.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;
import com.example.beautymanager.Modelo.Enums.EstadoUsuarioEnums;
import com.example.beautymanager.Repositorio.UsuarioRepository;
import com.example.beautymanager.Servicio.CarritoService;
import com.example.beautymanager.exception.BusinessException;

@RestController
@RequestMapping("/carrito")
@PreAuthorize("hasRole('CLIENTE')")
public class CarritoController {

    private final CarritoService carritoService;

    private final UsuarioRepository usuarioRepository;

    public CarritoController(CarritoService carritoService, UsuarioRepository usuarioRepository){

        this.carritoService = carritoService;

        this.usuarioRepository = usuarioRepository;
    }

    private String obtenerEmailAutenticado(
            Authentication authentication) {

        //========================
        //Validar autenticación
        //========================
        if(authentication == null ||
           !authentication.isAuthenticated() ||
           !(authentication.getPrincipal() instanceof Long idUsuario)) {

            throw new BusinessException(
                "No se pudo identificar a la clienta autenticada"
            );
        }

        //========================
        //Buscar usuario
        //========================
        UsuarioEntity usuario = usuarioRepository
            .findById(idUsuario)
            .orElseThrow(() ->
                new BusinessException("Usuario no encontrado")
            );

        //========================
        //Validar estado
        //========================
        if(usuario.getEstadoUsuario() != EstadoUsuarioEnums.ACTIVO) {

            throw new BusinessException(
                "La cuenta de usuario no está activa"
            );
        }

        //========================
        //Obtener email
        //========================
        return usuario.getEmail();
    }

    //========================
    //Obtener carrito
    //========================
    @GetMapping("/mi-carrito")
    public ResponseEntity<ObtenerCarritoDTO> obtenerCarrito(
            Authentication authentication) {

        String email = obtenerEmailAutenticado(authentication);

        return ResponseEntity.ok(
            carritoService.obtenerCarrito(email)
        );
    }

    //========================
    //Agregar producto
    //========================
    @PostMapping("/agregar-producto")
    public ResponseEntity<ObtenerCarritoDTO> agregarProducto(
            @RequestBody AgregarProductoCarritoDTO agregarProductoCarrito,
            Authentication authentication) {

        String email = obtenerEmailAutenticado(authentication);

        return ResponseEntity.ok(
            carritoService.agregarProducto(
                email,
                agregarProductoCarrito
            )
        );
    }

    //========================
    //Eliminar producto
    //========================
    @DeleteMapping("/eliminar-producto/{idProducto}")
    public ResponseEntity<ObtenerCarritoDTO> eliminarProducto(
            @PathVariable Long idProducto,
            Authentication authentication) {

        String email = obtenerEmailAutenticado(authentication);

        return ResponseEntity.ok(
            carritoService.eliminarProducto(
                email,
                idProducto
            )
        );
    }

    //========================
    //Actualizar cantidad
    //========================
    @PutMapping("/actualizar-cantidad")
    public ResponseEntity<ObtenerCarritoDTO> actualizarCantidad(
            @RequestBody ActualizarCantidadCarritoDTO actualizarCantidadCarrito,
            Authentication authentication) {

        String email = obtenerEmailAutenticado(authentication);

        return ResponseEntity.ok(
            carritoService.actualizarCantidad(
                email,
                actualizarCantidadCarrito
            )
        );
    }
}
