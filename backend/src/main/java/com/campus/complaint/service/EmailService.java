package com.campus.complaint.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class EmailService {

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${resend.api.key:${RESEND_API_KEY:}}")
    private String resendApiKey;

    @Async
    public void sendVerificationEmail(String toEmail, String fullName, String verificationToken) {
        try {
            System.out.println("Attempting to send verification email via Resend to: " + toEmail);
            
            if (resendApiKey == null || resendApiKey.isBlank()) {
                System.err.println("❌ Resend API Key is missing! Set RESEND_API_KEY environment variable.");
                return;
            }

            String verificationLink = frontendUrl + "/verify-email?token=" + verificationToken;
            
            // Build modern HTML email body for a beautiful look in Gmail
            String emailHtml = String.format(
                "<h3>COMSATS University - Email Verification</h3>" +
                "<p>Dear %s,</p>" +
                "<p>Thank you for registering with the COMSATS University Campus Complaint System.</p>" +
                "<p>To complete your registration and activate your account, please verify your email address by clicking the link below:</p>" +
                "<p><a href='%s' style='display:inline-block;background-color:#4F46E5;color:white;padding:10px 20px;text-decoration:none;border-radius:5px;font-weight:bold;'>Verify My Email Address</a></p>" +
                "<p>Or copy and paste this link in your browser:</p>" +
                "<p>%s</p>" +
                "<p>This verification link will expire in 24 hours.</p>" +
                "<br/>" +
                "<p>Best regards,<br/>" +
                "COMSATS University<br/>" +
                "Campus Complaint System Team</p>",
                fullName,
                verificationLink,
                verificationLink
            );

            // Construct JSON request body for Resend API
            // Note: Resend Free plan allows sending from onboarding@resend.dev to your verified/account emails.
            String jsonPayload = String.format(
                "{\"from\":\"onboarding@resend.dev\",\"to\":\"%s\",\"subject\":\"COMSATS University - Verify Your Email Address\",\"html\":\"%s\"}",
                toEmail,
                emailHtml.replace("\"", "\\\"") // Escape quotes for valid JSON
            );

            // Use Java's native HttpClient (built into Java 11+) over secure Port 443 (HTTP)
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .header("Authorization", "Bearer " + resendApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                System.out.println("✅ Verification email sent successfully via Resend to: " + toEmail);
            } else {
                System.err.println("❌ Resend API returned error code " + response.statusCode() + ": " + response.body());
            }

        } catch (Exception e) {
            System.err.println("❌ Failed to send verification email to: " + toEmail);
            System.err.println("Error details: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
