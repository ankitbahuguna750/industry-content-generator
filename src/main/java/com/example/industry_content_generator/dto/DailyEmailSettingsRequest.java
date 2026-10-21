package com.example.industry_content_generator.dto;

import java.time.LocalTime;

// User ki email settings receive karta hai.
public class DailyEmailSettingsRequest {

    private boolean dailyEmailEnabled;
    private String emailFrequency;
    private String weeklyDay;
    private LocalTime dailyEmailTime;
    private String industry;

    public boolean isDailyEmailEnabled() {
        return dailyEmailEnabled;
    }

    public void setDailyEmailEnabled(boolean dailyEmailEnabled) {
        this.dailyEmailEnabled = dailyEmailEnabled;
    }

    public String getEmailFrequency() {
        return emailFrequency;
    }

    public void setEmailFrequency(String emailFrequency) {
        this.emailFrequency = emailFrequency;
    }

    public String getWeeklyDay() {
        return weeklyDay;
    }

    public void setWeeklyDay(String weeklyDay) {
        this.weeklyDay = weeklyDay;
    }

    public LocalTime getDailyEmailTime() {
        return dailyEmailTime;
    }

    public void setDailyEmailTime(LocalTime dailyEmailTime) {
        this.dailyEmailTime = dailyEmailTime;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }
}