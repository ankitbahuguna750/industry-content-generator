
package com.example.industry_content_generator.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    // Spring provides the email sender automatically.
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    // Sends OTP to verify the user's email address.
    public void sendOtpEmail(String toEmail, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(senderEmail);
        message.setTo(toEmail);
        message.setSubject("Email Verification OTP");

        message.setText(
                "Hello,\n\n" +
                        "Your email verification OTP is: " + otp + "\n\n" +
                        "This OTP is valid for 5 minutes.\n" +
                        "Do not share this OTP with anyone.\n\n" +
                        "AI LinkedIn Content Generator"
        );

        javaMailSender.send(message);
    }

    // Sends a test email to check email configuration.
    public void sendTestEmail(String toEmail) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(senderEmail);
        message.setTo(toEmail);
        message.setSubject(
                "AI LinkedIn Content Generator - Test Email"
        );

        message.setText(
                "Hello!\n\n" +
                        "This is a test email from AI LinkedIn Content Generator.\n\n" +
                        "If you received this email, email sending is working.\n\n" +
                        "Thank you."
        );

        javaMailSender.send(message);
    }

    // Sends generated LinkedIn posts to the user's email.
    public void sendLinkedInPosts(String toEmail, String posts) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(senderEmail);
        message.setTo(toEmail);
        message.setSubject("Your AI-Generated LinkedIn Posts");

        message.setText(
                "Hello!\n\n" +
                        "Here are your generated LinkedIn posts:\n\n" +
                        posts + "\n\n" +
                        "Please review the content before publishing it on LinkedIn.\n\n" +
                        "AI LinkedIn Content Generator"
        );

        javaMailSender.send(message);
    }

    // Compatible method used by the existing scheduler.
    public void sendPostsEmail(
            String toEmail,
            String industry,
            String posts) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(senderEmail);
        message.setTo(toEmail);
        message.setSubject(
                "Your " + industry + " Industry LinkedIn Posts"
        );

        message.setText(
                "Hello!\n\n" +
                        "Here are your latest AI-generated LinkedIn posts.\n\n" +
                        "Industry: " + industry + "\n\n" +
                        posts + "\n\n" +
                        "Please review the content before publishing it on LinkedIn.\n\n" +
                        "AI LinkedIn Content Generator"
        );

        javaMailSender.send(message);
    }
}
