package com.example.beautymanager.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.beautymanager.Modelo.DTO.ActualizarClienteDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerClienteDTO;
import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;
import com.example.beautymanager.Repositorio.UsuarioRepository;
import com.example.beautymanager.Servicio.ClienteService;
import com.example.beautymanager.exception.BusinessException;

@RestController
@RequestMapping("/cliente")
public class ClienteController {

    private final ClienteService clienteService;

    private final UsuarioRepository usuarioRepository;

    public ClienteController(ClienteService clienteService, UsuarioRepository usuarioRepository){

        this.clienteService = clienteService;

        this.usuarioRepository = usuarioRepository;
    }

    //=======================
    //Obtener email autenticado
    //=======================
    private String obtenerEmailAutenticado(
            Authentication authentication) {

        if(authentication == null ||
           !authentication.isAuthenticated() ||
           !(authentication.getPrincipal() instanceof Long idUsuario)) {

            throw new BusinessException(
                "No se pudo identificar a la clienta autenticada"
            );
        }

        UsuarioEntity usuario = usuarioRepository
            .findById(idUsuario)
            .orElseThrow(() ->
                new BusinessException("Usuario no encontrado")
            );

        return usuario.getEmail();
    }

    //=======================
    //Actualizar cliente
    //=======================
    @PutMapping("/mi-perfil")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ObtenerClienteDTO> actualizarCliente(
            @RequestBody ActualizarClienteDTO actualizarCliente,
            Authentication authentication) {

        String email = obtenerEmailAutenticado(authentication);

        return ResponseEntity.ok(
            clienteService.actualizarCliente(
                email,
                actualizarCliente
            )
        );
    }

    //=======================
    //Eliminar cliente
    //=======================
    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> eliminarCliente(
            @PathVariable Long id) {

        clienteService.eliminarCliente(id);

        return ResponseEntity.ok(
            "Cliente eliminado correctamente"
        );
    }

    //=======================
    //Buscar por ID
    //=======================
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLEADO', 'ADMIN')")
    public ResponseEntity<ObtenerClienteDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
            clienteService.buscarPorId(id)
        );
    }

    //=======================
    //Mi perfil
    //=======================
    @GetMapping("/mi-perfil")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ObtenerClienteDTO> miPerfil(
            Authentication authentication) {

        String email = obtenerEmailAutenticado(authentication);

        return ResponseEntity.ok(
            clienteService.buscarPorEmail(email)
        );
    }

    //=======================
    //Buscar por email
    //=======================
    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('EMPLEADO', 'ADMIN')")
    public ResponseEntity<ObtenerClienteDTO> buscarPorEmail(
            @PathVariable String email) {

        return ResponseEntity.ok(
            clienteService.buscarPorEmail(email)
        );
    }

    //=======================
    //Listar todos
    //=======================
    @GetMapping("/listar")
    @PreAuthorize("hasAnyRole('EMPLEADO', 'ADMIN')")
    public ResponseEntity<List<ObtenerClienteDTO>> listarTodos() {

        return ResponseEntity.ok(
            clienteService.listarTodos()
        );
    }
}
