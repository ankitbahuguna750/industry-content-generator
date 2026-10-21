
package com.example.industry_content_generator.dto;

/**
 * Scheduling preferences received from the frontend.
 * DAILY: Run every day.
 * WEEKLY: Run on the selected day.
 */
public class ScheduleRequest {

    private String scheduleType = "DAILY";

    // Time format: HH:mm, for example 09:00
    private String scheduleTime = "09:00";

    // Required only when scheduleType is WEEKLY
    private String scheduleDay;

    // Returns DAILY or WEEKLY
    public String getScheduleType() {
        return scheduleType;
    }

    // Sets the scheduling frequency
    public void setScheduleType(String scheduleType) {
        this.scheduleType = scheduleType;
    }

    // Returns the selected time
    public String getScheduleTime() {
        return scheduleTime;
    }

    // Sets the selected time
    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    // Returns the selected weekday
    public String getScheduleDay() {
        return scheduleDay;
    }

    // Sets the selected weekday
    public void setScheduleDay(String scheduleDay) {
        this.scheduleDay = scheduleDay;
    }
}
