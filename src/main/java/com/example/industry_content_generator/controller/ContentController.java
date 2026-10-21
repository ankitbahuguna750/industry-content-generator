
        package com.example.industry_content_generator.controller;

import com.example.industry_content_generator.dto.ContentRequest;
import com.example.industry_content_generator.entity.User;
import com.example.industry_content_generator.repository.UserRepository;
import com.example.industry_content_generator.service.EmailService;
import com.example.industry_content_generator.service.GeminiService;
import com.example.industry_content_generator.service.NewsService;
import com.example.industry_content_generator.service.ScheduleService;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ContentController {

    private final GeminiService geminiService;
    private final NewsService newsService;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final ScheduleService scheduleService;

    // Initializes the services required by this controller.
    public ContentController(
            GeminiService geminiService,
            NewsService newsService,
            EmailService emailService,
            UserRepository userRepository,
            ScheduleService scheduleService) {

        this.geminiService = geminiService;
        this.newsService = newsService;
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.scheduleService = scheduleService;
    }

    // Generates LinkedIn posts using industry news.
    @PostMapping("/api/content")
    public String createContent(@RequestBody ContentRequest request) {

        String news = newsService.getNews(request.getIndustry());

        return geminiService.generatePost(request, news);
    }

    // Sends a test email.
    @GetMapping("/api/email/test")
    public String sendTestEmail(@RequestParam String email) {

        emailService.sendTestEmail(email);

        return "Test email sent successfully!";
    }

    // Generates posts and emails them to the requested address.
    @GetMapping("/api/email/posts")
    public String sendPostsByEmail(
            @RequestParam String email,
            @RequestParam String industry) {

        ContentRequest request = new ContentRequest();
        request.setIndustry(industry);
        request.setTone("Professional");

        String news = newsService.getNews(industry);
        String posts = geminiService.generatePost(request, news);

        emailService.sendLinkedInPosts(email, posts);

        return "LinkedIn posts generated and sent to email successfully!";
    }

    // Returns the saved schedule of the logged-in user.
    @GetMapping("/api/schedule")
    public ResponseEntity<?> getSchedule(HttpSession session) {

        Object sessionUserId = session.getAttribute("userId");

        if (sessionUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "success", false,
                            "message", "Please log in first."
                    ));
        }

        try {
            Long userId = Long.valueOf(sessionUserId.toString());

            User user = userRepository.findById(userId)
                    .orElseThrow(() ->
                            new IllegalArgumentException("User not found."));

            Map<String, Object> response = new HashMap<>();

            response.put("success", true);
            response.put("scheduleType", user.getEmailFrequency());
            response.put("scheduleTime", user.getDailyEmailTime());
            response.put("scheduleDay", user.getWeeklyDay());
            response.put("enabled", user.isDailyEmailEnabled());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "message", exception.getMessage()
                    ));
        }
    }

    // Saves scheduling preferences for the logged-in user.
    @PostMapping("/api/schedule/save")
    public ResponseEntity<?> saveSchedule(
            @RequestBody Map<String, String> request,
            HttpSession session) {

        Object sessionUserId = session.getAttribute("userId");

        if (sessionUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "success", false,
                            "message", "Please log in first."
                    ));
        }

        try {
            Long userId = Long.valueOf(sessionUserId.toString());

            User user = userRepository.findById(userId)
                    .orElseThrow(() ->
                            new IllegalArgumentException("User not found."));

            scheduleService.updateSchedule(
                    user,
                    request.get("scheduleType"),
                    request.get("scheduleTime"),
                    request.get("scheduleDay")
            );

            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Schedule saved successfully."
            ));

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "message", exception.getMessage()
                    ));
        }
    }

    // Generates posts immediately and emails them to the logged-in user.
    @PostMapping("/api/schedule/proceed")
    public ResponseEntity<?> proceed(HttpSession session) {

        Object sessionUserId = session.getAttribute("userId");

        if (sessionUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "success", false,
                            "message", "Please log in first."
                    ));
        }

        try {
            Long userId = Long.valueOf(sessionUserId.toString());

            User user = userRepository.findById(userId)
                    .orElseThrow(() ->
                            new IllegalArgumentException("User not found."));

            if (!user.isDailyEmailEnabled()) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "message", "Please save your schedule first."
                        ));
            }

            if (user.getIndustry() == null
                    || user.getIndustry().isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "message", "Please select your industry."
                        ));
            }

            if (user.getEmail() == null || user.getEmail().isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "message", "Your account email is missing."
                        ));
            }

            ContentRequest contentRequest = new ContentRequest();
            contentRequest.setIndustry(user.getIndustry());
            contentRequest.setTone("Professional");

            String news = newsService.getNews(user.getIndustry());

            String posts = geminiService.generatePost(
                    contentRequest,
                    news
            );

            emailService.sendLinkedInPosts(user.getEmail(), posts);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Posts generated and emailed successfully.",
                    "posts", posts
            ));

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "message", exception.getMessage()
                    ));

        } catch (Exception exception) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Could not generate or email posts. Please try again."
                    ));
        }
    }
}
