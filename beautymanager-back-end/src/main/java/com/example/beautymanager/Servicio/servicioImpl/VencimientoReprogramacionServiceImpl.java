package com.example.beautymanager.Servicio.servicioImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.Entidad.ReprogramacionTurnoEntity;
import com.example.beautymanager.Modelo.Entidad.TurnoEntity;
import com.example.beautymanager.Modelo.Enums.EstadoReprogramacionEnums;
import com.example.beautymanager.Modelo.Enums.EstadoTurnoEnums;
import com.example.beautymanager.Repositorio.ReprogramacionTurnoRepository;
import com.example.beautymanager.Servicio.VencimientoReprogramacionService;

import jakarta.transaction.Transactional;

@Service 
public class VencimientoReprogramacionServiceImpl implements VencimientoReprogramacionService {

    private final ReprogramacionTurnoRepository reprogramacionTurnoRepository;

    //========================================
    //Constructor
    //========================================
    public VencimientoReprogramacionServiceImpl(
            ReprogramacionTurnoRepository reprogramacionTurnoRepository) {

        this.reprogramacionTurnoRepository =
            reprogramacionTurnoRepository;
    }

    //========================================
    //Anular propuestas vencidas
    //========================================
    @Override
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void anularPropuestasVencidas() {

        LocalDateTime ahora = LocalDateTime.now();

        //====================================
        //Buscar propuestas pendientes
        //====================================

        List<ReprogramacionTurnoEntity> pendientes =
            reprogramacionTurnoRepository.findByEstadoPropuesta(
                EstadoReprogramacionEnums.PENDIENTE
            );

        List<ReprogramacionTurnoEntity> anuladas =
            new ArrayList<>();

        for (ReprogramacionTurnoEntity propuesta : pendientes) {

            //================================
            //Obtener turno original
            //================================

            TurnoEntity turno = propuesta.getTurno();

            //================================
            //Comprobar fechas
            //================================

            boolean fechasInvalidas =
                propuesta.getInicioAnterior() == null ||
                propuesta.getFinAnterior() == null ||
                propuesta.getInicioNuevo() == null;

            boolean horarioVencido = !fechasInvalidas && (
                !propuesta.getInicioAnterior().isAfter(ahora) ||
                !propuesta.getInicioNuevo().isAfter(ahora)
            );

            //================================
            //Comprobar estado del turno
            //================================

            boolean turnoInvalido =
                turno == null ||
                (
                    turno.getEstadoTurno() != EstadoTurnoEnums.PENDIENTE &&
                    turno.getEstadoTurno() != EstadoTurnoEnums.CONFIRMADO
                );

            //================================
            //Detectar cambios del horario
            //================================

            boolean horarioOriginalModificado = false;

            if (turno != null && !fechasInvalidas) {

                horarioOriginalModificado =
                    !propuesta.getInicioAnterior().equals(
                        turno.getFechaHoraInicio()
                    ) ||
                    !propuesta.getFinAnterior().equals(
                        turno.getFechaHoraFin()
                    );
            }

            //================================
            //Anular propuestas inválidas
            //================================

            if (
                fechasInvalidas ||
                horarioVencido ||
                turnoInvalido ||
                horarioOriginalModificado
            ) {

                propuesta.setEstadoPropuesta(
                    EstadoReprogramacionEnums.ANULADA
                );

                propuesta.setFechaRespuesta(ahora);

                anuladas.add(propuesta);
            }
        }

        //====================================
        //Guardar propuestas anuladas
        //====================================

        if (!anuladas.isEmpty()) {

            reprogramacionTurnoRepository.saveAll(anuladas);
        }
    }
}
