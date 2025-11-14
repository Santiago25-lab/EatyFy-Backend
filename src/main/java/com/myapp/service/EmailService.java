package com.myapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.name}")
    private String appName;

    @Value("${app.url}")
    private String appUrl;

    public void sendRegistrationConfirmation(String toEmail, String userName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Bienvenido a " + appName + " - Confirma tu registro");
        message.setText(buildRegistrationEmail(userName));

        try {
            mailSender.send(message);
        } catch (Exception e) {
            // Log error but don't fail registration
            System.err.println("Error sending email: " + e.getMessage());
        }
    }

    private String buildRegistrationEmail(String userName) {
        return String.format(
            "¡Hola %s!\n\n" +
            "Gracias por registrarte en %s.\n\n" +
            "Tu cuenta ha sido creada exitosamente. Ahora puedes:\n" +
            "• Descubrir restaurantes que se ajustan a tu presupuesto\n" +
            "• Recibir recomendaciones personalizadas basadas en tus gustos\n" +
            "• Dejar reseñas y calificaciones\n\n" +
            "Accede a tu cuenta en: %s\n\n" +
            "¡Disfruta explorando nuevos sabores!\n\n" +
            "Equipo de %s",
            userName, appName, appUrl, appName
        );
    }
}