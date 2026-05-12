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

    @Value("${emailjs.service.id:${EMAILJS_SERVICE_ID:}}")
    private String serviceId;

    @Value("${emailjs.template.id:${EMAILJS_TEMPLATE_ID:}}")
    private String templateId;

    @Value("${emailjs.public.key:${EMAILJS_PUBLIC_KEY:}}")
    private String publicKey;

    @Value("${emailjs.private.key:${EMAILJS_PRIVATE_KEY:}}")
    private String privateKey;

    @Async
    public void sendVerificationEmail(String toEmail, String fullName, String verificationToken) {
        try {
            System.out.println("Attempting to send verification email via EmailJS to: " + toEmail);

            if (publicKey == null || publicKey.isBlank()) {
                System.err.println("❌ EmailJS keys are missing! Set EMAILJS_PUBLIC_KEY environment variable.");
                return;
            }

            String verificationLink = frontendUrl + "/verify-email?token=" + verificationToken;

            // Build JSON payload for EmailJS REST API
            String jsonPayload = String.format(
                "{" +
                "\"service_id\":\"%s\"," +
                "\"template_id\":\"%s\"," +
                "\"user_id\":\"%s\"," +
                "\"accessToken\":\"%s\"," +
                "\"template_params\":{" +
                    "\"to_email\":\"%s\"," +
                    "\"to_name\":\"%s\"," +
                    "\"verification_link\":\"%s\"" +
                "}" +
                "}",
                serviceId,
                templateId,
                publicKey,
                privateKey,
                toEmail,
                fullName,
                verificationLink
            );

            // Send via EmailJS REST API (HTTPS Port 443 - never blocked!)
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.emailjs.com/api/v1.0/email/send"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("✅ Verification email sent successfully via EmailJS to: " + toEmail);
            } else {
                System.err.println("❌ EmailJS API returned error code " + response.statusCode() + ": " + response.body());
            }

        } catch (Exception e) {
            System.err.println("❌ Failed to send verification email to: " + toEmail);
            System.err.println("Error details: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Async
    public void sendPasswordResetEmail(String toEmail, String fullName, String resetToken) {
        try {
            System.out.println("Attempting to send password reset email via EmailJS to: " + toEmail);

            String resetLink = frontendUrl + "/reset-password?token=" + resetToken;
            System.out.println("🔗 [DEVELOPMENT/TEST ONLY] Password reset link is: " + resetLink);

            if (publicKey == null || publicKey.isBlank()) {
                System.err.println("⚠️ EmailJS keys are missing on local machine! Bypassing email send. Copy the printed [TEST ONLY] link above to test on your localhost!");
                return;
            }

            // Build JSON payload for EmailJS REST API
            String jsonPayload = String.format(
                "{" +
                "\"service_id\":\"%s\"," +
                "\"template_id\":\"%s\"," +
                "\"user_id\":\"%s\"," +
                "\"accessToken\":\"%s\"," +
                "\"template_params\":{" +
                    "\"to_email\":\"%s\"," +
                    "\"to_name\":\"%s\"," +
                    "\"verification_link\":\"%s\"," +
                    "\"reset_link\":\"%s\"" +
                "}" +
                "}",
                serviceId,
                templateId,
                publicKey,
                privateKey,
                toEmail,
                fullName,
                resetLink,
                resetLink
            );

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.emailjs.com/api/v1.0/email/send"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("✅ Password reset email sent successfully via EmailJS to: " + toEmail);
            } else {
                System.err.println("❌ EmailJS API returned error code " + response.statusCode() + ": " + response.body());
            }

        } catch (Exception e) {
            System.err.println("❌ Failed to send password reset email to: " + toEmail);
            System.err.println("Error details: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
