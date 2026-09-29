package com.example.industry_content_generator.service;

import com.example.industry_content_generator.dto.ContentRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DailyContentScheduler {

    private final NewsService newsService;
    private final GeminiService geminiService;
    private final EmailService emailService;

    public DailyContentScheduler(
            NewsService newsService,
            GeminiService geminiService,
            EmailService emailService) {

        this.newsService = newsService;
        this.geminiService = geminiService;
        this.emailService = emailService;
    }

    // Daily at 9:00 AM India time
    @Scheduled(
            cron = "0 0 9 * * *",
            zone = "Asia/Kolkata")

            // curent test ke liye
//            @Scheduled(
//                    cron = "0 * * * * *",
//                    zone = "Asia/Kolkata"
//            )



    public void generateDailyContent() {

        String industry = "technology";
        String email = "bahugunaankit710@gmail.com";

        ContentRequest request = new ContentRequest();

        request.setIndustry(industry);
        request.setTone("Professional");

        // Get latest industry news
        String news = newsService.getNews(industry);

        // Generate 3 LinkedIn posts
        String posts = geminiService.generatePost(request, news);

        // Send posts by email
        emailService.sendLinkedInPosts(email, posts);

        System.out.println(
                "Daily LinkedIn posts generated and sent successfully!"
        );
    }
}