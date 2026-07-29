package com.example.beautymanager.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.beautymanager.Modelo.DTO.ActualizarUsuarioDTO;
import com.example.beautymanager.Modelo.DTO.CambiarPasswordDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerUsuarioDTO;
import com.example.beautymanager.Modelo.Enums.EstadoUsuarioEnums;
import com.example.beautymanager.Servicio.UsuarioService;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    //=======================
    //Listar usuarios
    //=======================
    @GetMapping("/listar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ObtenerUsuarioDTO>> listarUsuarios(){
        return  ResponseEntity.ok(usuarioService.findAllUsuario());
    }

    //=======================
    //Obtener por id
    //=======================
    @GetMapping("/id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ObtenerUsuarioDTO> obtenerPorId(@PathVariable Long id){
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    //=======================
    //Obtener por email
    //=======================
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ObtenerUsuarioDTO> obtenerPorEmail(@PathVariable String email){
        return ResponseEntity.ok(usuarioService.obtenerPorEmail(email));
    }

    //=======================
    //Buscar por nombre
    //=======================
    @GetMapping("/nombre/{nombre}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ObtenerUsuarioDTO>> buscarPorNombre(@PathVariable String nombre){
        return ResponseEntity.ok(usuarioService.buscarPorNombre(nombre));
    }

    //=======================
    //Buscar por apellido
    //=======================
    @GetMapping("/apellido/{apellido}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ObtenerUsuarioDTO>> buscarPorApellido(@PathVariable String apellido){
        return ResponseEntity.ok(usuarioService.buscarPorApellido(apellido));
    }

    //=======================
    //Buscar por rol
    //=======================
    @GetMapping("/rol/{idRol}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ObtenerUsuarioDTO>> buscarPorRol(@PathVariable Long idRol){
        return ResponseEntity.ok(usuarioService.buscarPorRol(idRol));
    }

    //=======================
    //Buscar por estado
    //=======================
    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ObtenerUsuarioDTO>> buscarPorEstado(@PathVariable EstadoUsuarioEnums estado){
        return ResponseEntity.ok(usuarioService.buscarPorEstado(estado));
    }

    //=======================
    //Mi perfil
    //=======================
    @GetMapping("/mi-perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ObtenerUsuarioDTO> obtenerMiPerfil(){

        Long idUsuario = (Long) SecurityContextHolder

                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ResponseEntity.ok(usuarioService.obtenerPorId(idUsuario));
    }

    //=======================
    //Actualizar usuario
    //=======================
    @PutMapping("/actualizar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ObtenerUsuarioDTO> actualizarUsuario(@RequestBody ActualizarUsuarioDTO actualizarUsuario){

        Long idUsuario = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ResponseEntity.ok(usuarioService.actualizarUsuario(idUsuario, actualizarUsuario));
    }

    //=======================
    //Verificar email
    //=======================
    @GetMapping("/existe-email/{email}")
    public ResponseEntity<Boolean> existeEmail(@PathVariable String email){
        return ResponseEntity.ok(usuarioService.existeEmail(email));
    }

    //=======================
    //Cambiar password
    //=======================
    @PutMapping("/cambiar-password/")
    public ResponseEntity<String> cambiarPassword(@RequestBody CambiarPasswordDTO  cambiarPassword){
        
        Long idUsuario = (Long) SecurityContextHolder

                .getContext()
                .getAuthentication()
                .getPrincipal();

        usuarioService.cambiarPassword(idUsuario, cambiarPassword);

        return ResponseEntity.ok("Contraseña actualizada correctamente");
    }

    //=======================
    //Actualizar foto perfil
    //=======================
    @PostMapping("/foto-perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> actualizarFotoPerfil(@RequestParam("foto") MultipartFile foto){

        Long idUsuario = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        usuarioService.actualizarFotoPerfil(idUsuario, foto);

        return ResponseEntity.ok("Foto de perfil actualizada correctamente");
    }

    //=======================
    //Eliminar foto perfil
    //=======================
    @DeleteMapping("/foto-perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> eliminarFotoPerfil(){

        Long idUsuario = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        usuarioService.eliminarFotoPerfil(idUsuario);

        return ResponseEntity.ok("Foto de perfil eliminada correctamente");
    }
}
