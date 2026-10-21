
package com.example.industry_content_generator.service;

import com.example.industry_content_generator.entity.User;
import com.example.industry_content_generator.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
public class RecurringScheduleScheduler {

    private static final Logger logger =
            LoggerFactory.getLogger(RecurringScheduleScheduler.class);

    private static final ZoneId INDIA_ZONE =
            ZoneId.of("Asia/Kolkata");

    private final UserRepository userRepository;

    public RecurringScheduleScheduler(
            UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Checks for due schedules every minute
    @Scheduled(fixedDelay = 60000)
    public void runScheduledPosts() {

        LocalDateTime now = LocalDateTime.now(INDIA_ZONE);

        List<User> users =
                userRepository.findByDailyEmailEnabledTrueAndNextRunAtLessThanEqual(now);

        for (User user : users) {
            try {
                /*
                 * TODO: Generate posts using the existing
                 * NewsService and GeminiService, then email
                 * them using EmailService.
                 *
                 * Only calculate the next run after a successful
                 * generation and email delivery.
                 *
                 * Do not disable the user's recurring schedule.
                 */

                logger.info(
                        "Schedule is due for user ID {}",
                        user.getId()
                );

            } catch (Exception e) {
                logger.error(
                        "Scheduled generation failed for user ID {}",
                        user.getId(),
                        e
                );
            }
        }
    }
}
