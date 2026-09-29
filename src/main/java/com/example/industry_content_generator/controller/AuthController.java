package com.example.industry_content_generator.controller;

import com.example.industry_content_generator.dto.LoginRequest;
import com.example.industry_content_generator.dto.RegisterRequest;
import com.example.industry_content_generator.entity.User;
import com.example.industry_content_generator.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // =========================
    // REGISTER
    // =========================

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody RegisterRequest request) {

        // Check email already exists
        if (userRepository
                .findByEmail(request.getEmail())
                .isPresent()) {

            return ResponseEntity.badRequest()
                    .body("Email already registered!");
        }

        // Create new user
        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        // Encrypt password before saving
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        // Save user in MySQL
        userRepository.save(user);

        return ResponseEntity.ok(
                "Registration successful!"
        );
    }


    // =========================
    // LOGIN
    // =========================

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody LoginRequest request,
            HttpSession session) {

        // Find user by email
        User user = userRepository
                .findByEmail(request.getEmail())
                .orElse(null);

        // Email not found
        if (user == null) {

            return ResponseEntity.badRequest()
                    .body("Email not registered!");
        }

        // Check password
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            return ResponseEntity.badRequest()
                    .body("Wrong password!");
        }

        // Save logged-in user information in session
        session.setAttribute(
                "userId",
                user.getId()
        );

        session.setAttribute(
                "userName",
                user.getName()
        );

        session.setAttribute(
                "userEmail",
                user.getEmail()
        );

        return ResponseEntity.ok(
                "Login successful!"
        );
    }


    // =========================
    // LOGOUT
    // =========================

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            HttpSession session) {

        // Destroy current session
        session.invalidate();

        return ResponseEntity.ok(
                "Logout successful!"
        );
    }

    @GetMapping("/current-user")
    public ResponseEntity<String> currentUser(
            HttpSession session) {

        Object userId = session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401)
                    .body("Not logged in");
        }

        String userName =
                (String) session.getAttribute("userName");

        return ResponseEntity.ok(userName);
    }
}

