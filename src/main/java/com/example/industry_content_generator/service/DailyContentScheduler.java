
package com.example.industry_content_generator.service;

import com.example.industry_content_generator.dto.ContentRequest;
import com.example.industry_content_generator.entity.User;
import com.example.industry_content_generator.repository.UserRepository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class DailyContentScheduler {

    private static final ZoneId TIME_ZONE =
            ZoneId.of("Asia/Kolkata");

    private final UserRepository userRepository;
    private final NewsService newsService;
    private final GeminiService geminiService;
    private final EmailService emailService;

    // Initializes the services required for scheduled content generation.
    public DailyContentScheduler(
            UserRepository userRepository,
            NewsService newsService,
            GeminiService geminiService,
            EmailService emailService) {

        this.userRepository = userRepository;
        this.newsService = newsService;
        this.geminiService = geminiService;
        this.emailService = emailService;
    }

    // Checks every minute whether any user's scheduled time has arrived.

    @Scheduled(cron = "0 * * * * *", zone = "Asia/Kolkata")
    public void checkSchedules() {

        LocalDateTime now = LocalDateTime.now(TIME_ZONE)
                .withSecond(0)
                .withNano(0);

        List<User> users =
                userRepository
                        .findByDailyEmailEnabledTrueAndNextRunAtLessThanEqual(now);

        for (User user : users) {
            processScheduledUser(user, now);
        }
    }

    // Processes one user's recurring schedule.
    private void processScheduledUser(User user, LocalDateTime now) {

        try {
            if (user.getDailyEmailTime() == null
                    || user.getEmailFrequency() == null
                    || user.getEmail() == null
                    || user.getEmail().isBlank()
                    || user.getIndustry() == null
                    || user.getIndustry().isBlank()) {

                System.err.println(
                        "Skipping schedule because required user settings are missing. User ID: "
                                + user.getId());

                return;
            }

            // For weekly schedules, run only on the selected weekday.
            if ("WEEKLY".equalsIgnoreCase(user.getEmailFrequency())) {

                if (user.getWeeklyDay() == null
                        || user.getWeeklyDay().isBlank()) {

                    System.err.println(
                            "Weekly day is missing for user ID: " + user.getId());

                    return;
                }

                DayOfWeek selectedDay =
                        DayOfWeek.valueOf(user.getWeeklyDay().toUpperCase());

                if (now.getDayOfWeek() != selectedDay) {
                    return;
                }

            } else if (!"DAILY".equalsIgnoreCase(user.getEmailFrequency())) {

                System.err.println(
                        "Unsupported schedule frequency for user ID: "
                                + user.getId());

                return;
            }

            // Generate posts and send them to the user's registered email.
            generateAndEmailNow(user);

            System.out.println(
                    "Scheduled LinkedIn posts sent to: " + user.getEmail());

        } catch (Exception exception) {

            System.err.println(
                    "Scheduled post generation failed for user ID: "
                            + user.getId());

            exception.printStackTrace();

        } finally {

            // Move to the next scheduled run so the same run is not repeated.
            user.setNextRunAt(calculateNextRun(user, now));
            userRepository.save(user);
        }
    }

    // Generates posts and emails them immediately when Proceed is clicked.
    // This method does not change the user's saved recurring schedule.
    public String generateAndEmailNow(User user) {

        if (user == null) {
            throw new IllegalArgumentException("User is required.");
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalStateException("User email is missing.");
        }

        if (user.getIndustry() == null || user.getIndustry().isBlank()) {
            throw new IllegalStateException("User industry is missing.");
        }

        // Get current news related to the user's industry.
        String news = newsService.getNews(user.getIndustry());

        // Prepare the request for the existing Gemini service.
        ContentRequest request = new ContentRequest();
        request.setIndustry(user.getIndustry());
        request.setTone("Professional");

        // Generate LinkedIn post content.
        String posts = geminiService.generatePost(request, news);

        if (posts == null || posts.isBlank()) {
            throw new IllegalStateException(
                    "The content service returned empty posts.");
        }

        // Email the generated posts to the registered user.
        emailService.sendPostsEmail(
                user.getEmail(),
                user.getIndustry(),
                posts
        );

        return posts;
    }

    // Calculates the next daily or weekly execution time.
    private LocalDateTime calculateNextRun(
            User user,
            LocalDateTime now) {

        LocalTime selectedTime = user.getDailyEmailTime();

        if (selectedTime == null) {
            selectedTime = LocalTime.of(9, 0);
        }

        LocalDateTime nextRun =
                now.toLocalDate().atTime(selectedTime);

        if ("DAILY".equalsIgnoreCase(user.getEmailFrequency())) {

            if (!nextRun.isAfter(now)) {
                nextRun = nextRun.plusDays(1);
            }

            return nextRun;
        }

        if ("WEEKLY".equalsIgnoreCase(user.getEmailFrequency())) {

            DayOfWeek selectedDay =
                    DayOfWeek.valueOf(user.getWeeklyDay().toUpperCase());

            while (nextRun.getDayOfWeek() != selectedDay
                    || !nextRun.isAfter(now)) {

                nextRun = nextRun.plusDays(1);
            }

            return nextRun;
        }

        // Fallback for invalid frequency values.
        return now.plusDays(1);
    }
}
