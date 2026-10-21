
package com.example.industry_content_generator.controller;

import com.example.industry_content_generator.dto.LoginRequest;
import com.example.industry_content_generator.dto.RegisterRequest;
import com.example.industry_content_generator.dto.SendOtpRequest;
import com.example.industry_content_generator.dto.VerifyOtpRequest;

import com.example.industry_content_generator.entity.User;
import com.example.industry_content_generator.repository.UserRepository;
import com.example.industry_content_generator.service.EmailOtpService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final EmailOtpService emailOtpService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(
            UserRepository userRepository,
            EmailOtpService emailOtpService,
            BCryptPasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.emailOtpService = emailOtpService;
        this.passwordEncoder = passwordEncoder;
    }

    // Sends OTP after checking that the email is not registered
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(
            @Valid @RequestBody SendOtpRequest request) {

        String email = normalizeEmail(request.getEmail());

        if (userRepository.existsByEmailIgnoreCase(email)) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "This email is already registered.")
            );
        }

        try {
            emailOtpService.sendOtp(email);

            return ResponseEntity.ok(
                    Map.of("message",
                            "OTP sent to your email. It expires in 5 minutes.")
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                    Map.of("message",
                            "OTP could not be sent. Check your email settings.")
            );
        }
    }

    // Verifies the OTP entered by the user
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {

        boolean verified = emailOtpService.verifyOtp(
                request.getEmail(),
                request.getOtp()
        );

        if (!verified) {
            return ResponseEntity.badRequest().body(
                    Map.of("message",
                            "OTP is incorrect, expired, or the attempt limit was reached.")
            );
        }

        return ResponseEntity.ok(
                Map.of("message", "Email verified successfully.")
        );
    }

    // Creates the account only after email verification
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request) {

        String email = normalizeEmail(request.getEmail());

        if (userRepository.existsByEmailIgnoreCase(email)) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "This email is already registered.")
            );
        }

        if (!emailOtpService.isEmailVerified(email)) {
            return ResponseEntity.badRequest().body(
                    Map.of("message",
                            "Please verify your email before registering.")
            );
        }

        User user = new User();

        user.setName(request.getName().trim());
        user.setEmail(email);

        // Save the encoded password, never the plain password
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setIndustry(request.getIndustry().trim());

        // The user must choose and enable email scheduling later
        user.setDailyEmailEnabled(false);

        userRepository.save(user);
        emailOtpService.removeOtpRecord(email);

        return ResponseEntity.ok(
                Map.of("message",
                        "Registration successful. Please log in.")
        );
    }

    // Logs in the user and creates a session
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request,
            HttpSession session) {

        String email = normalizeEmail(request.getEmail());

        User user = userRepository
                .findByEmailIgnoreCase(email)
                .orElse(null);

        if (user == null
                || !passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("message", "Invalid email or password.")
            );
        }

        session.setAttribute("userId", user.getId());

        return ResponseEntity.ok(
                Map.of(
                        "message", "Login successful.",
                        "name", user.getName(),
                        "email", user.getEmail()
                )
        );
    }

    // Returns details for the currently logged-in user
    @GetMapping("/me")
    public ResponseEntity<?> currentUser(HttpSession session) {

        Object id = session.getAttribute("userId");

        if (!(id instanceof Long userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("message", "Please log in.")
            );
        }

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            session.invalidate();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("message", "Please log in.")
            );
        }

        return ResponseEntity.ok(
                Map.of(
                        "id", user.getId(),
                        "name", user.getName(),
                        "email", user.getEmail(),
                        "industry", user.getIndustry()
                )
        );
    }

    // Logs out the current user
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {

        session.invalidate();

        return ResponseEntity.ok(
                Map.of("message", "Logged out successfully.")
        );
    }

    // Normalizes email addresses before database operations
    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
