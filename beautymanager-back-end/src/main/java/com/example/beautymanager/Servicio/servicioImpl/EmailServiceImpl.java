package com.example.beautymanager.Servicio.servicioImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.Entidad.RecordatorioEntity;
import com.example.beautymanager.Servicio.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void enviarEmail(RecordatorioEntity r) {

        if (r == null || r.getTurno() == null || r.getTurno().getCliente() == null || r.getTurno().getCliente().getUsuario() == null) {
            throw new IllegalArgumentException("Recordatorio no valido para envio de email");
        }

        String destino = r.getTurno()
                          .getCliente()
                          .getUsuario()
                          .getEmail();

        String nombre = r.getTurno()
                         .getCliente()
                         .getUsuario()
                         .getNombre();

        String fecha = r.getTurno()
                        .getFechaHoraInicio()
                        .toString();

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(destino);
        mail.setSubject("Recordatorio de turno");

        mail.setText(
            "Hola " + nombre + ",\n\n" +
            "Te recordamos que tienes un turno el día:\n " + fecha + "\n\n" +
            "Por favor, asistir con puntualidad.\n\n"
        );
        
        mailSender.send(mail);
    }
}
