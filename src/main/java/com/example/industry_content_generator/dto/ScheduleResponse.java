
package com.example.industry_content_generator.dto;

public class ScheduleResponse {

    private boolean success;
    private String message;
    private String posts;

    public ScheduleResponse() {
    }

    public ScheduleResponse(boolean success, String message, String posts) {
        this.success = success;
        this.message = message;
        this.posts = posts;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPosts() {
        return posts;
    }

    public void setPosts(String posts) {
        this.posts = posts;
    }
}
