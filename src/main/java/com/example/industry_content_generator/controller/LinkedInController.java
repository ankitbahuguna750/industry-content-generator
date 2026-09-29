package com.example.industry_content_generator.controller;

import com.example.industry_content_generator.service.LinkedInService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

@RestController
public class LinkedInController {

    @Value("${linkedin.client.id}")
    private String clientId;

    @Value("${linkedin.redirect.uri}")
    private String redirectUri;

    private final LinkedInService linkedInService;

    public LinkedInController(
            LinkedInService linkedInService) {

        this.linkedInService = linkedInService;
    }


    // ==========================================
    // LinkedIn Login
    // ==========================================

    @GetMapping("/linkedin/login")
    public String linkedinLogin() {

        String state =
                UUID.randomUUID().toString();

        String authorizationUrl =
                "https://www.linkedin.com/oauth/v2/authorization"
                        + "?response_type=code"
                        + "&client_id="
                        + URLEncoder.encode(
                        clientId,
                        StandardCharsets.UTF_8
                )
                        + "&redirect_uri="
                        + URLEncoder.encode(
                        redirectUri,
                        StandardCharsets.UTF_8
                )
                        + "&state="
                        + URLEncoder.encode(
                        state,
                        StandardCharsets.UTF_8
                )
                        + "&scope="
                        + URLEncoder.encode(
                        "openid profile email w_member_social",
                        StandardCharsets.UTF_8
                );

        return """
                <h2>Connect LinkedIn</h2>

                <a href="%s">
                    Connect LinkedIn Account
                </a>
                """.formatted(
                authorizationUrl
        );
    }


    // ==========================================
    // LinkedIn OAuth Callback
    // ==========================================

    @GetMapping("/oauth/linkedin/callback")
    public String linkedinCallback(
            @RequestParam String code,
            @RequestParam(required = false) String state) {

        linkedInService.getAccessToken(code);

        return """
                <h2>LinkedIn authorization successful!</h2>

                <p>
                Your LinkedIn account is connected.
                </p>

                <a href="/index.html">
                    Go back to AI LinkedIn Content Generator
                </a>
                """;
    }


    // ==========================================
    // Get LinkedIn Person ID
    // ==========================================

    @GetMapping("/linkedin/person-id")
    public String getPersonId() {

        return linkedInService.getPersonId();
    }


    // ==========================================
    // Test LinkedIn Post
    // ==========================================

    @GetMapping("/linkedin/test-post")
    public String testPost() {

        String personId =
                linkedInService.getPersonId();

        String testPost = """
                🚀 Test Post

                This post was published automatically
                using my Java Spring Boot application.

                #Java #SpringBoot #LinkedIn
                """;

        return linkedInService.publishPost(
                testPost,
                personId
        );
    }


    // ==========================================
    // Publish Selected Post From Website
    // ==========================================

    @PostMapping("/linkedin/publish")
    public String publishSelectedPost(
            @RequestBody Map<String, String> request) {

        String postText =
                request.get("postText");

        if (
                postText == null
                        ||
                        postText.isBlank()
        ) {

            return "Post content is empty.";
        }

        String personId =
                linkedInService.getPersonId();

        return linkedInService.publishPost(
                postText,
                personId
        );
    }


    // ==========================================
    // Publish Post From Gmail Button
    // ==========================================

    @GetMapping("/linkedin/publish-from-email")
    public String publishFromEmail(
            @RequestParam String post) {

        if (
                post == null
                        ||
                        post.isBlank()
        ) {

            return "Post content is empty.";
        }

        String personId =
                linkedInService.getPersonId();

        return linkedInService.publishPost(
                post,
                personId
        );
    }
}