package com.campus.complaint.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public void sendVerificationEmail(String toEmail, String fullName, String verificationToken) {
        try {
            System.out.println("Attempting to send verification email to: " + toEmail);
            SimpleMailMessage message = new SimpleMailMessage();
            
            // Use a valid email format for 'from'. Mailtrap username is often not an email.
            String from = (fromEmail != null && fromEmail.contains("@")) ? fromEmail : "no-reply@campus-complaints.edu";
            message.setFrom(from);
            message.setTo(toEmail);
            message.setSubject("COMSATS University - Verify Your Email Address");
            
            String verificationLink = frontendUrl + "/verify-email?token=" + verificationToken;
            
            String emailBody = String.format(
                "Dear %s,\n\n" +
                "Thank you for registering with the COMSATS University Campus Complaint System.\n\n" +
                "To complete your registration and activate your account, please verify your email address by clicking the link below:\n\n" +
                "%s\n\n" +
                "This verification link will expire in 24 hours.\n\n" +
                "If you did not create this account, please ignore this email.\n\n" +
                "Best regards,\n" +
                "COMSATS University\n" +
                "Campus Complaint System Team",
                fullName,
                verificationLink
            );
            
            message.setText(emailBody);
            mailSender.send(message);
            
            System.out.println("✅ Verification email sent successfully to: " + toEmail);
        } catch (Exception e) {
            System.err.println("❌ Failed to send verification email to: " + toEmail);
            System.err.println("Error details: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send verification email: " + e.getMessage());
        }
    }
}
