package com.example.backend.services;

import com.example.backend.utils.JWTUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private JWTUtil jwtUtil;

    public boolean sendEmail(String to, String type) {
        MimeMessage message = mailSender.createMimeMessage();

        String token = jwtUtil.generateToken(to);

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject("Change your Password");

            String resetUrl = "http://localhost:4200/change-password?token=" + token;
            String htmlContent = null;

            if (type.equalsIgnoreCase("reset")) {
                htmlContent = "<p>Your password has been reset. Please <a href='" + resetUrl + "'>click here</a> to change your password.</p>";
            } else {
                htmlContent = "<p>Your <b>Mega City Cab</b> account has created. Please <a href='" + resetUrl + "'>click here</a> to change your password.</p>";
            }

            helper.setText(htmlContent, true);
            mailSender.send(message);

            return true;
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }
}
