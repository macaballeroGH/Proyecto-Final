package com.example.beautymanager.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.beautymanager.Modelo.DTO.CrearTurnoDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerTurnoDTO;
import com.example.beautymanager.Modelo.DTO.ResponseDTO;
import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;
import com.example.beautymanager.Repositorio.UsuarioRepository;
import com.example.beautymanager.Servicio.TurnoService;
import com.example.beautymanager.exception.BusinessException;

@RestController
@RequestMapping("/turno")
public class TurnoController {

    @Autowired
    private TurnoService turnoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    //=======================
    //Crear turno
    //=======================
    @PostMapping("/reservar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ObtenerTurnoDTO> reservarTurno(@RequestBody CrearTurnoDTO crearTurno){

        Long idUsuario = (Long) SecurityContextHolder

                .getContext()
                .getAuthentication()
                .getPrincipal();

        ObtenerTurnoDTO response = turnoService.crearTurno(crearTurno, idUsuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //=======================
    //Crear turno admin
    //=======================
    @PostMapping("/admin/reservar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ObtenerTurnoDTO> crearTurnoAdmin(@RequestBody CrearTurnoDTO crearTurno){

        ObtenerTurnoDTO response = turnoService.crearTurnoAdmin(crearTurno);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //=======================
    //Cancelar turno
    //=======================
    @DeleteMapping("/cancelar/{idTurno}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ResponseDTO> cancelarTurno(@PathVariable Long idTurno, Authentication auth){

        Long idUsuario = (Long) auth.getPrincipal();

        turnoService.cancelarTurno(idTurno, idUsuario);

        ResponseDTO response = new ResponseDTO();
        response.setSuccess(true);
        response.setMensaje("Turno cancelado correctamente");
        response.setNumOfErrors(0);

        return ResponseEntity.ok(response);
    }

    //==============================
    //Aceptar turno (por empleado)
    //==============================
    @PutMapping("/aceptar/{idTurno}")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<ResponseDTO> aceptarTurno(@PathVariable Long idTurno, Authentication auth){

        Long idUsuario = (Long) auth.getPrincipal();

        turnoService.aceptarTurno(idTurno, idUsuario);

        ResponseDTO response = new ResponseDTO();
        response.setSuccess(true);
        response.setMensaje("Turno aceptado");
        response.setNumOfErrors(0);

        return ResponseEntity.ok(response);
    }

    //==============================
    //Rechazar turno (por empleado)
    //==============================
    @PutMapping("/rechazar/{idTurno}")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<ResponseDTO> rechazarTurno(@PathVariable Long idTurno, Authentication auth){

        Long idUsuario = (Long) auth.getPrincipal();

        turnoService.rechazarTurno(idTurno, idUsuario);

        ResponseDTO response = new ResponseDTO();
        response.setSuccess(true);
        response.setMensaje("Turno rechazado");
        response.setNumOfErrors(0);

        return ResponseEntity.ok(response);
    }

    //==============================================
    //Marcar ausencia del cliente (por empleado)
    //==============================================
    @PutMapping("/ausente/{idTurno}")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<ResponseDTO> marcarAusente(@PathVariable Long idTurno, Authentication auth){

       Long idUsuario = (Long) auth.getPrincipal();

        turnoService.marcarAusente(idTurno, idUsuario);

        ResponseDTO response = new ResponseDTO();
        response.setSuccess(true);
        response.setMensaje("Turno marcado como ausente");
        response.setNumOfErrors(0);

        return ResponseEntity.ok(response);
    }

    //=======================
    //Iniciar turno
    //=======================
    @PutMapping("/iniciar/{idTurno}")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<ResponseDTO> iniciarTurno(@PathVariable Long idTurno, Authentication auth){

        Long idUsuario = (Long) auth.getPrincipal();

        turnoService.iniciarTurno(idTurno, idUsuario);

        ResponseDTO response = new ResponseDTO();
        response.setSuccess(true);
        response.setMensaje("Turno iniciado");
        response.setNumOfErrors(0);

        return ResponseEntity.ok(response);
    }

    //=======================
    //Finalizar turno
    //=======================
    @PutMapping("/finalizar/{idTurno}")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<ResponseDTO> finalizarTurno(@PathVariable Long idTurno, Authentication auth){

        Long idUsuario = (Long) auth.getPrincipal();

        turnoService.finalizarTurno(idTurno, idUsuario);

        ResponseDTO response = new ResponseDTO();
        response.setSuccess(true);
        response.setMensaje("Turno finalizado");
        response.setNumOfErrors(0);

        return ResponseEntity.ok(response);
    }

    //=======================
    //Turnos por cliente
    //=======================
    @GetMapping("/cliente/mis-turnos")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<ObtenerTurnoDTO>> misTurnosCliente(Authentication auth){

        Long idUsuario = (Long) auth.getPrincipal();

        return ResponseEntity.ok(turnoService.obtenerTurnosPorCliente(idUsuario));
    }

    //=======================
    //Turnos por empleado
    //=======================
    @GetMapping("/empleado/mis-turnos")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<List<ObtenerTurnoDTO>> misTurnosEmpleado(Authentication auth){

        Long idUsuario = (Long) auth.getPrincipal();

        UsuarioEntity usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        Long idEmpleado = usuario.getEmpleado().getId();

        return ResponseEntity.ok(turnoService.obtenerTurnosPorEmpleado(idEmpleado));
    }

    //==============================
    //Todos los turnos por admin
    //==============================
    @GetMapping("/admin/todos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ObtenerTurnoDTO>> todosLosTurnos(){

        return ResponseEntity.ok(turnoService.obtenerTodosLosTurnos());
    }
}
