package com.example.beautymanager.Servicio.servicioImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.Entidad.BloqueoAgendaEntity;
import com.example.beautymanager.Modelo.Entidad.EmpleadoEntity;
import com.example.beautymanager.Modelo.Entidad.HorarioEmpleadoEntity;
import com.example.beautymanager.Modelo.Entidad.ServicioEntity;
import com.example.beautymanager.Modelo.Entidad.TurnoEntity;
import com.example.beautymanager.Repositorio.BloqueoAgendaRepository;
import com.example.beautymanager.Repositorio.EmpleadoRepository;
import com.example.beautymanager.Repositorio.HorarioEmpleadoRepository;
import com.example.beautymanager.Repositorio.ServicioRepository;
import com.example.beautymanager.Repositorio.TurnoRepository;
import com.example.beautymanager.Servicio.AgendaService;
import com.example.beautymanager.exception.BusinessException;

@Service
public class AgendaServiceImpl implements AgendaService {

   @Autowired
   private TurnoRepository turnoRepository;

   @Autowired
   private ServicioRepository servicioRepository;

   @Autowired
   private HorarioEmpleadoRepository horarioEmpleadoRepository;

   @Autowired
   private BloqueoAgendaRepository bloqueoAgendaRepository;

   @Autowired
   private EmpleadoRepository empleadoRepository;

   //===========================
   //Disponibilidad por dia
   //===========================
   @Override
   public List<LocalDateTime> obtenerDisponibilidad(Long idEmpleado, List<Long> serviciosIds, LocalDate fecha) {

    if (idEmpleado == null || serviciosIds == null || serviciosIds.isEmpty() || fecha == null) {
        throw new BusinessException("Datos invalidos para calcular disponibilidad");
    }

    List<LocalDateTime> disponibles = new ArrayList<>();

    int duracion = calcularDuracionTotal(serviciosIds);

    List<HorarioEmpleadoEntity> horarios = horarioEmpleadoRepository.findByEmpleadoIdAndDiaSemana(idEmpleado, fecha.getDayOfWeek().getValue());

    if(horarios.isEmpty()) {
        return disponibles;
    }

    for(HorarioEmpleadoEntity h : horarios) {

        LocalDateTime inicio = fecha.atTime(h.getHoraInicio());
        LocalDateTime fin = fecha.atTime(h.getHoraFin());

        while(!inicio.plusMinutes(duracion).isAfter(fin)) {

            LocalDateTime finTurno = inicio.plusMinutes(duracion);

            if(validarDisponibilidad(idEmpleado, inicio, finTurno) && validarBloqueos(idEmpleado, inicio, finTurno)) {
                disponibles.add(inicio);
            }
            inicio = inicio.plusMinutes(30);
        }
    }
    return disponibles;
   }

   //==============================
   //Disponibilidad por rango
   //==============================
   @Override
   public List<LocalDateTime> obtenerDisponibilidadRango(Long idEmpleado, List<Long> serviciosIds, LocalDate inicio, LocalDate fin) {

    if (idEmpleado == null || serviciosIds == null || serviciosIds.isEmpty() || inicio == null || fin == null) {
        throw new BusinessException("Datos invalidos para calcular disponibilidad");
    }

    if (inicio.isAfter(fin)) {
        throw new BusinessException("La fecha de inicio no puede ser posterior a la fecha de fin");
    }

    List<LocalDateTime> resultado = new ArrayList<>();

    while (!inicio.isAfter(fin)) {
        resultado.addAll(obtenerDisponibilidad(idEmpleado, serviciosIds, inicio));
        inicio = inicio.plusDays(1);
    }
    return resultado;
   }

   //=======================================
   //Obtener disponibilidad por empleado
   //=======================================
   @Override
   public List<LocalDateTime> obtenerDisponibilidadPorEmpleado(Long idUsuario, List<Long> serviciosIds, LocalDate fecha){

    EmpleadoEntity empleado = empleadoRepository.findByUsuario_Id(idUsuario)
            .orElseThrow(() -> new BusinessException("El usuario no pertenece a un empleado"));

    return obtenerDisponibilidad(empleado.getId(), serviciosIds, fecha);
   }

   //===========================
   //Validar disponibilidad
   //===========================
   @Override
   public boolean validarDisponibilidad(Long idEmpleado, LocalDateTime inicio, LocalDateTime fin){

    List<TurnoEntity> turnos = turnoRepository.findByEmpleadoIdAndFechaHoraInicioLessThanAndFechaHoraFinGreaterThan(idEmpleado, fin, inicio);

    return turnos.isEmpty();
   }

   //========================
   //Validad bloqueo
   //========================
   private boolean validarBloqueos(Long idEmpleado, LocalDateTime inicio, LocalDateTime fin) {

    List<BloqueoAgendaEntity> bloqueos = bloqueoAgendaRepository.findByEmpleadoIdAndInicioLessThanAndFinGreaterThan(idEmpleado, fin, inicio);

    return bloqueos.isEmpty();
   }

   //=============================
   //Calcular duracion total
   //=============================
   @Override
   public Integer calcularDuracionTotal(List<Long> serviciosIds) {
    
    if(serviciosIds == null || serviciosIds.isEmpty()) {
        throw new BusinessException("Debe seleccionar al menos un servicio");
    }

    int total = 0;

    for (Long id : serviciosIds) {
        ServicioEntity servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Servicio no encontrado: " + id));

        total += servicio.getDuracionMinutos();
    }
    return total;
   }
}
