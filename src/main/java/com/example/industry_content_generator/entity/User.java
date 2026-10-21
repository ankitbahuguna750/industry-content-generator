
package com.example.industry_content_generator.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String industry = "Technology";

    // Enables or disables recurring email generation
    @Column(nullable = false)
    private boolean dailyEmailEnabled = false;

    // DAILY or WEEKLY
    @Column(nullable = false)
    private String emailFrequency = "DAILY";

    // Used only for WEEKLY scheduling
    @Column(nullable = false)
    private String weeklyDay = "MONDAY";

    // User-selected time for scheduled generation
    private LocalTime dailyEmailTime = LocalTime.of(9, 0);

    // Stores the next scheduled run for this user
    private LocalDateTime nextRunAt;

    public User() {
    }

    // Returns the user ID
    public Long getId() {
        return id;
    }

    // Sets the user ID
    public void setId(Long id) {
        this.id = id;
    }

    // Returns the user's name
    public String getName() {
        return name;
    }

    // Sets the user's name
    public void setName(String name) {
        this.name = name;
    }

    // Returns the user's email
    public String getEmail() {
        return email;
    }

    // Sets the user's email
    public void setEmail(String email) {
        this.email = email;
    }

    // Returns the stored password
    public String getPassword() {
        return password;
    }

    // Sets the stored password
    public void setPassword(String password) {
        this.password = password;
    }

    // Returns the selected industry
    public String getIndustry() {
        return industry;
    }

    // Sets the selected industry
    public void setIndustry(String industry) {
        this.industry = industry;
    }

    // Checks whether recurring emails are enabled
    public boolean isDailyEmailEnabled() {
        return dailyEmailEnabled;
    }

    // Enables or disables recurring emails
    public void setDailyEmailEnabled(boolean dailyEmailEnabled) {
        this.dailyEmailEnabled = dailyEmailEnabled;
    }

    // Returns DAILY or WEEKLY
    public String getEmailFrequency() {
        return emailFrequency;
    }

    // Sets the schedule frequency
    public void setEmailFrequency(String emailFrequency) {
        this.emailFrequency = emailFrequency;
    }

    // Returns the selected weekday
    public String getWeeklyDay() {
        return weeklyDay;
    }

    // Sets the selected weekday
    public void setWeeklyDay(String weeklyDay) {
        this.weeklyDay = weeklyDay;
    }

    // Returns the selected schedule time
    public LocalTime getDailyEmailTime() {
        return dailyEmailTime;
    }

    // Sets the selected schedule time
    public void setDailyEmailTime(LocalTime dailyEmailTime) {
        this.dailyEmailTime = dailyEmailTime;
    }

    // Returns the next scheduled run
    public LocalDateTime getNextRunAt() {
        return nextRunAt;
    }

    // Sets the next scheduled run
    public void setNextRunAt(LocalDateTime nextRunAt) {
        this.nextRunAt = nextRunAt;
    }
}
