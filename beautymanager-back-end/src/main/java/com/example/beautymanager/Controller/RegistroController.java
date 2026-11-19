package com.example.beautymanager.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.example.beautymanager.Modelo.DTO.RegistroDTO;
import com.example.beautymanager.Modelo.DTO.ResponseDTO;
import com.example.beautymanager.Servicio.RegistroUsuarioService;

@RestController
@RequestMapping("/registro")
public class RegistroController {
    
    private final RegistroUsuarioService registroService;

    public RegistroController(RegistroUsuarioService registroService){
        this.registroService = registroService;
    }

    @PostMapping("/nuevo")
    public ResponseEntity<ResponseDTO> nuevoUsuario(@RequestBody RegistroDTO registroDTO) throws Exception {

        ResponseDTO response = registroService.registrarUsuario(registroDTO);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
