
package com.example.industry_content_generator.controller;

import com.example.industry_content_generator.entity.User;
import com.example.industry_content_generator.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final UserRepository userRepository;

    // Constructor: UserRepository ko controller mein use karne ke liye
    public DashboardController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Dashboard khulne par logged-in user ki profile load karta hai
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpSession session) {

        Object sessionUserId = session.getAttribute("userId");

        if (!(sessionUserId instanceof Long)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Please login first."));
        }

        Long userId = (Long) sessionUserId;

        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found."));
        }

        User user = optionalUser.get();

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("industry", user.getIndustry());

        return ResponseEntity.ok(response);
    }

    // User ki profile update karta hai
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestBody Map<String, String> request,
            HttpSession session) {

        Object sessionUserId = session.getAttribute("userId");

        if (!(sessionUserId instanceof Long)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Please login first."));
        }

        Long userId = (Long) sessionUserId;

        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found."));
        }

        User user = optionalUser.get();

        String name = request.get("name");
        String industry = request.get("industry");

        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Name is required."));
        }

        if (industry == null || industry.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Industry is required."));
        }

        user.setName(name.trim());
        user.setIndustry(industry.trim());

        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Profile updated successfully.");
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("industry", user.getIndustry());

        return ResponseEntity.ok(response);
    }
}
