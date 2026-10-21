
package com.example.industry_content_generator.service;

import com.example.industry_content_generator.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Locale;

@Service
public class ScheduleService {

    public void updateSchedule(
            User user,
            String scheduleType,
            String scheduleTime,
            String scheduleDay) {

        // Check schedule frequency
        if (scheduleType == null || scheduleType.isBlank()) {
            throw new IllegalArgumentException(
                    "Please select a schedule frequency."
            );
        }

        String frequency = scheduleType.trim()
                .toUpperCase(Locale.ROOT);

        if (!frequency.equals("DAILY")
                && !frequency.equals("WEEKLY")) {
            throw new IllegalArgumentException(
                    "Invalid schedule frequency. Select Daily or Weekly."
            );
        }

        // Check schedule time
        if (scheduleTime == null || scheduleTime.isBlank()) {
            throw new IllegalArgumentException(
                    "Please select a schedule time."
            );
        }

        LocalTime time;

        try {
            time = LocalTime.parse(scheduleTime.trim());
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException(
                    "Invalid schedule time. Use HH:mm format, e.g. 09:00."
            );
        }

        // Check day for weekly scheduling
        String day = "MONDAY";

        if ("WEEKLY".equals(frequency)) {
            if (scheduleDay == null || scheduleDay.isBlank()) {
                throw new IllegalArgumentException(
                        "Please select a day for the weekly schedule."
                );
            }

            day = scheduleDay.trim().toUpperCase(Locale.ROOT);

            switch (day) {
                case "MONDAY":
                case "TUESDAY":
                case "WEDNESDAY":
                case "THURSDAY":
                case "FRIDAY":
                case "SATURDAY":
                case "SUNDAY":
                    break;

                default:
                    throw new IllegalArgumentException(
                            "Please select a valid weekday."
                    );
            }
        }

        // Save schedule in the User entity
        user.setEmailFrequency(frequency);
        user.setDailyEmailTime(time);
        user.setWeeklyDay(day);
        user.setDailyEmailEnabled(true);
    }
}
