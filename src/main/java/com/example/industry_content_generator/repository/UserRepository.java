
package com.example.industry_content_generator.repository;

import com.example.industry_content_generator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Find a user by email, ignoring uppercase and lowercase differences.
    Optional<User> findByEmailIgnoreCase(String email);

    // Check whether an email is already registered.
    boolean existsByEmailIgnoreCase(String email);

    // Find users whose dailyEmailEnabled field is true
    // and whose nextRunAt time has arrived.
    List<User> findByDailyEmailEnabledTrueAndNextRunAtLessThanEqual(
            LocalDateTime currentTime
    );

}
