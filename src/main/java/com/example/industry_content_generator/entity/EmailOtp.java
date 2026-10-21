
package com.example.industry_content_generator.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "email_otps")
public class EmailOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String otpHash;

    @Column(nullable = false)
    private LocalDateTime expiryTime;

    @Column(nullable = false)
    private int attempts = 0;

    @Column(nullable = false)
    private boolean verified = false;

    public EmailOtp() {
    }

    // Returns the record ID
    public Long getId() {
        return id;
    }

    // Returns the email address
    public String getEmail() {
        return email;
    }

    // Sets the email address
    public void setEmail(String email) {
        this.email = email;
    }

    // Returns the hashed OTP
    public String getOtpHash() {
        return otpHash;
    }

    // Sets the hashed OTP
    public void setOtpHash(String otpHash) {
        this.otpHash = otpHash;
    }

    // Returns the OTP expiry time
    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    // Sets the OTP expiry time
    public void setExpiryTime(LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }

    // Returns the number of failed attempts
    public int getAttempts() {
        return attempts;
    }

    // Sets the number of failed attempts
    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    // Checks whether the email was verified
    public boolean isVerified() {
        return verified;
    }

    // Updates the verification status
    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
