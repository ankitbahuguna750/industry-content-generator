
package com.example.industry_content_generator.repository;

import com.example.industry_content_generator.entity.EmailOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailOtpRepository extends JpaRepository<EmailOtp, Long> {

    Optional<EmailOtp> findByEmailIgnoreCase(String email);

    void deleteByEmailIgnoreCase(String email);
}
