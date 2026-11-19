package com.example.beautymanager.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
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

import com.example.beautymanager.Modelo.DTO.CrearTurnoDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerReprogramacionTurnoDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerTurnoDTO;
import com.example.beautymanager.Modelo.DTO.ReprogramarTurnoDTO;
import com.example.beautymanager.Modelo.DTO.ResponseDTO;
import com.example.beautymanager.Repositorio.UsuarioRepository;
import com.example.beautymanager.Servicio.TurnoService;

@RestController
@RequestMapping("/turno")
public class TurnoController {

    private final TurnoService turnoService;

    public TurnoController(TurnoService turnoService, UsuarioRepository usuarioRepository){

        this.turnoService = turnoService;
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


    //========================================
    //Reprogramar turno (EMPLEADO)
    //========================================
    @PutMapping("/reprogramar/{idTurno}")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<ObtenerTurnoDTO> reprogramarTurno(
            @PathVariable Long idTurno,
            @RequestBody ReprogramarTurnoDTO datos,
            Authentication auth) {

        Long idUsuario = (Long) auth.getPrincipal();

        ObtenerTurnoDTO response = turnoService.reprogramarTurno(
            idTurno,
            idUsuario,
            datos
        );

        return ResponseEntity.ok(response);
    }


    //========================================
    //Aceptar propuesta de reprogramación
    //========================================
    @PutMapping("/reprogramacion/{idReprogramacion}/aceptar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ObtenerTurnoDTO> aceptarReprogramacion(
            @PathVariable Long idReprogramacion,
            Authentication auth) {

        Long idUsuario = (Long) auth.getPrincipal();

        ObtenerTurnoDTO response =
            turnoService.aceptarReprogramacion(
                idReprogramacion,
                idUsuario
            );

        return ResponseEntity.ok(response);
    }


    //========================================
    //Rechazar propuesta de reprogramación
    //========================================
    @PutMapping("/reprogramacion/{idReprogramacion}/rechazar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ResponseDTO> rechazarReprogramacion(
            @PathVariable Long idReprogramacion,
            Authentication auth) {

        Long idUsuario = (Long) auth.getPrincipal();

        turnoService.rechazarReprogramacion(
            idReprogramacion,
            idUsuario
        );

        ResponseDTO response = new ResponseDTO();

        response.setSuccess(true);
        response.setMensaje("Propuesta rechazada correctamente");
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


    //========================================
    //Reprogramaciones pendientes de CLIENTE
    //========================================
    @GetMapping("/cliente/reprogramaciones-pendientes")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<ObtenerReprogramacionTurnoDTO>>
        obtenerReprogramacionesPendientesCliente(Authentication auth) {

        Long idUsuario = (Long) auth.getPrincipal();

        List<ObtenerReprogramacionTurnoDTO> response =
            turnoService.obtenerReprogramacionesPendientesCliente(
                idUsuario
            );

        return ResponseEntity.ok(response);
    }


    //=======================
    //Turnos por empleado
    //=======================
    @GetMapping("/empleado/mis-turnos")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<List<ObtenerTurnoDTO>> misTurnosEmpleado(Authentication auth){

        Long idUsuario = (Long) auth.getPrincipal();

        return ResponseEntity.ok(
            turnoService.obtenerTurnosPorEmpleado(idUsuario)
        );
    }

    //==============================
    //Todos los turnos por admin
    //==============================
    @GetMapping("/admin/todos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ObtenerTurnoDTO>> todosLosTurnos(){

        return ResponseEntity.ok(turnoService.obtenerTodosLosTurnos());
    }


    //========================================
    //Confirmar turno (ADMIN)
    //========================================
    @PutMapping("/admin/confirmar/{idTurno}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> confirmarTurnoAdmin(
            @PathVariable Long idTurno) {

        turnoService.confirmarTurnoAdmin(idTurno);

        ResponseDTO response = new ResponseDTO();

        response.setSuccess(true);
        response.setMensaje("Turno confirmado correctamente");
        response.setNumOfErrors(0);

        return ResponseEntity.ok(response);
    }


    //========================================
    //Rechazar turno (ADMIN)
    //========================================
    @PutMapping("/admin/rechazar/{idTurno}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO> rechazarTurnoAdmin(
            @PathVariable Long idTurno) {

        turnoService.rechazarTurnoAdmin(idTurno);

        ResponseDTO response = new ResponseDTO();

        response.setSuccess(true);
        response.setMensaje("Turno rechazado correctamente");
        response.setNumOfErrors(0);

        return ResponseEntity.ok(response);
    }

}
