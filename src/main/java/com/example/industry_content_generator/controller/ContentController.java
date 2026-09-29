package com.example.industry_content_generator.controller;

import com.example.industry_content_generator.dto.ContentRequest;
import com.example.industry_content_generator.service.EmailService;
import com.example.industry_content_generator.service.GeminiService;
import com.example.industry_content_generator.service.NewsService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContentController {

    private final GeminiService geminiService;
    private final NewsService newsService;
    private final EmailService emailService;

    public ContentController(
            GeminiService geminiService,
            NewsService newsService,
            EmailService emailService) {

        this.geminiService = geminiService;
        this.newsService = newsService;
        this.emailService = emailService;
    }

    // Generate 3 LinkedIn posts
    @PostMapping("/api/content")
    public String createContent(@RequestBody ContentRequest request) {

        String news = newsService.getNews(request.getIndustry());

        return geminiService.generatePost(request, news);
    }

    // Test email
    @GetMapping("/api/email/test")
    public String sendTestEmail(@RequestParam String email) {

        emailService.sendTestEmail(email);

        return "Test email sent successfully!";
    }

    // Generate posts and send them to email
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
}