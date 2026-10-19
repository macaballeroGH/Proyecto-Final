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

        String destino = r.getTurno().getCliente().getUsuario().getEmail();

        String nombre = r.getTurno().getCliente().getUsuario().getNombre();

        String fecha = r.getTurno().getFechaHoraInicio().toString();

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(destino);
        mail.setSubject("Recordatorio de turno");

        mail.setText("Hola " + nombre + ",\n\n" + "Te recordamos que tienes un turno el día:\n " + fecha + "\n\n" + "Por favor, asistir con puntualidad.\n\n");
        
        mailSender.send(mail);
    }

    
@Override
public void enviarEmailPrueba(String destino) {

    if (destino == null || destino.isBlank()) {
        throw new IllegalArgumentException(
            "La dirección de correo es obligatoria"
        );
    }

    destino = destino.trim();

    if (!destino.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
        throw new IllegalArgumentException(
            "La dirección de correo no es válida"
        );
    }

    SimpleMailMessage mail = new SimpleMailMessage();

    mail.setTo(destino);
    mail.setSubject("Prueba de correo - BeautyManager");
    mail.setText(
        "Hola,\n\n" +
        "Este es un correo de prueba de BeautyManager.\n\n" +
        "Si recibiste este mensaje, el envío de correos " +
        "desde el sistema está funcionando.\n\n" +
        "Este mensaje no corresponde a un turno ni a un recordatorio real."
    );

    mailSender.send(mail);
}

}
