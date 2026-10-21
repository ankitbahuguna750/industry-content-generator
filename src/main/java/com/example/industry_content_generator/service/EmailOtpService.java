
package com.example.industry_content_generator.service;

import com.example.industry_content_generator.entity.EmailOtp;
import com.example.industry_content_generator.repository.EmailOtpRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class EmailOtpService {

    private final EmailOtpRepository emailOtpRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    private final SecureRandom random = new SecureRandom();

    public EmailOtpService(
            EmailOtpRepository emailOtpRepository,
            EmailService emailService,
            PasswordEncoder passwordEncoder) {
        this.emailOtpRepository = emailOtpRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    // Generate OTP, save its hash and send it to the user's email.
    @Transactional
    public void sendOtp(String email) {

        String normalizedEmail = email.trim().toLowerCase();

        // Generate a six-digit OTP.
        String otp = String.format("%06d", random.nextInt(1_000_000));

        // Store only the hashed OTP, not the original OTP.
        EmailOtp emailOtp = emailOtpRepository
                .findByEmailIgnoreCase(normalizedEmail)
                .orElse(new EmailOtp());

        emailOtp.setEmail(normalizedEmail);
        emailOtp.setOtpHash(passwordEncoder.encode(otp));
        emailOtp.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        emailOtp.setAttempts(0);
        emailOtp.setVerified(false);

        emailOtpRepository.save(emailOtp);

        // EmailService must contain this method.
        emailService.sendOtpEmail(normalizedEmail, otp);
    }

    // Verify OTP and reject expired or incorrect OTPs.
    @Transactional
    public boolean verifyOtp(String email, String enteredOtp) {

        String normalizedEmail = email.trim().toLowerCase();

        Optional<EmailOtp> optionalOtp =
                emailOtpRepository.findByEmailIgnoreCase(normalizedEmail);

        if (optionalOtp.isEmpty()) {
            return false;
        }

        EmailOtp emailOtp = optionalOtp.get();

        // Reject an expired OTP.
        if (emailOtp.getExpiryTime() == null
                || LocalDateTime.now().isAfter(emailOtp.getExpiryTime())) {
            emailOtpRepository.delete(emailOtp);
            return false;
        }

        // Allow a maximum of five incorrect attempts.
        if (emailOtp.getAttempts() >= 5) {
            emailOtpRepository.delete(emailOtp);
            return false;
        }

        // Check whether the entered OTP matches the stored hash.
        if (enteredOtp == null
                || !passwordEncoder.matches(
                enteredOtp.trim(), emailOtp.getOtpHash())) {

            emailOtp.setAttempts(emailOtp.getAttempts() + 1);
            emailOtpRepository.save(emailOtp);
            return false;
        }

        // Mark the email as verified.
        emailOtp.setVerified(true);
        emailOtpRepository.save(emailOtp);

        return true;
    }

    // Check whether this email has passed OTP verification.
    @Transactional(readOnly = true)
    public boolean isEmailVerified(String email) {

        if (email == null || email.isBlank()) {
            return false;
        }

        return emailOtpRepository.findByEmailIgnoreCase(
                        email.trim().toLowerCase())
                .map(EmailOtp::isVerified)
                .orElse(false);
    }

    // Delete the OTP record after successful registration.
    @Transactional
    public void removeOtpRecord(String email) {

        if (email != null && !email.isBlank()) {
            emailOtpRepository.deleteByEmailIgnoreCase(
                    email.trim().toLowerCase());
        }
    }
}
