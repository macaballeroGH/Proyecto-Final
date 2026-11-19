package com.example.beautymanager.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.beautymanager.Modelo.DTO.ConfirmarCompraDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerCompraDTO;
import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;
import com.example.beautymanager.Modelo.Enums.EstadoUsuarioEnums;
import com.example.beautymanager.Repositorio.UsuarioRepository;
import com.example.beautymanager.Servicio.CompraService;
import com.example.beautymanager.exception.BusinessException;

@RestController
@RequestMapping("/compra")
public class CompraController {

    private final CompraService compraService;

    private final UsuarioRepository usuarioRepository;

    public CompraController(
            CompraService compraService,
            UsuarioRepository usuarioRepository) {

        this.compraService = compraService;

        this.usuarioRepository = usuarioRepository;
    }

    //=======================
    //Obtener ID del cliente autenticado
    //=======================
    private Long obtenerIdClienteAutenticado(
            Authentication authentication) {

        //=======================
        //Validar autenticación
        //=======================
        if(authentication == null ||
           !authentication.isAuthenticated() ||
           !(authentication.getPrincipal() instanceof Long idUsuario)) {

            throw new BusinessException(
                "No se pudo identificar al usuario autenticado"
            );
        }

        //=======================
        //Buscar usuario
        //=======================
        UsuarioEntity usuario = usuarioRepository
            .findById(idUsuario)
            .orElseThrow(() ->
                new BusinessException("Usuario no encontrado")
            );

        //=======================
        //Validar usuario activo
        //=======================
        if(usuario.getEstadoUsuario() != EstadoUsuarioEnums.ACTIVO) {

            throw new BusinessException(
                "El usuario no está activo"
            );
        }

        //=======================
        //Validar cliente
        //=======================
        if(usuario.getCliente() == null ||
           usuario.getCliente().getId() == null) {

            throw new BusinessException(
                "El usuario no tiene un perfil de cliente válido"
            );
        }

        return usuario.getCliente().getId();
    }

    //=======================
    //Realizar compra
    //=======================
    @PostMapping("/realizar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ObtenerCompraDTO> realizarCompra(
            @RequestBody ConfirmarCompraDTO confirmarCompra,
            Authentication authentication) {

        //=======================
        //Validar solicitud
        //=======================
        if(confirmarCompra == null) {

            throw new BusinessException(
                "Los datos de la compra son obligatorios"
            );
        }

        //=======================
        //Cliente autenticado
        //=======================
        Long idCliente = obtenerIdClienteAutenticado(
            authentication
        );

        //=======================
        //Obtener clave de idempotencia
        //=======================
        String claveIdempotencia =
            confirmarCompra.getClaveIdempotencia();

        //=======================
        //Realizar compra
        //=======================
        ObtenerCompraDTO response =
            compraService.realizarCompra(
                idCliente,
                claveIdempotencia
            );

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
    }

    //=======================
    //Obtener mis compras
    //=======================
    @GetMapping("/mis-compras")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<ObtenerCompraDTO>> obtenerMisCompras(
            Authentication authentication) {

        //=======================
        //Cliente autenticado
        //=======================
        Long idCliente = obtenerIdClienteAutenticado(
            authentication
        );

        //=======================
        //Consultar historial
        //=======================
        List<ObtenerCompraDTO> compras =
            compraService.obtenerMisCompras(
                idCliente
            );

        return ResponseEntity.ok(compras);
    }

    //=======================
    //Obtener detalle de mi compra
    //=======================
    @GetMapping("/mis-compras/{idCompra}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ObtenerCompraDTO> obtenerMiCompra(
            @PathVariable Long idCompra,
            Authentication authentication) {

        //=======================
        //Cliente autenticado
        //=======================
        Long idCliente = obtenerIdClienteAutenticado(
            authentication
        );

        //=======================
        //Consultar compra
        //=======================
        ObtenerCompraDTO compra =
            compraService.obtenerMiCompra(
                idCliente,
                idCompra
            );

        return ResponseEntity.ok(compra);
    }
}
