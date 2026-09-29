
        package com.example.industry_content_generator.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class LinkedInService {

    @Value("${linkedin.client.id}")
    private String clientId;

    @Value("${linkedin.client.secret}")
    private String clientSecret;

    @Value("${linkedin.redirect.uri}")
    private String redirectUri;

    private final RestClient restClient = RestClient.create();

    private String accessToken;

    // Get Access Token
    public String getAccessToken(String code) {

        MultiValueMap<String, String> formData =
                new LinkedMultiValueMap<>();

        formData.add("grant_type", "authorization_code");
        formData.add("code", code);
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("redirect_uri", redirectUri);

        Map<String, Object> response = restClient.post()
                .uri("https://www.linkedin.com/oauth/v2/accessToken")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .body(Map.class);

        if (response == null ||
                response.get("access_token") == null) {

            throw new RuntimeException(
                    "LinkedIn access token was not received."
            );
        }

        accessToken =
                (String) response.get("access_token");

        return accessToken;
    }

    // Check Access Token
    public String getStoredAccessToken() {

        if (accessToken == null ||
                accessToken.isBlank()) {

            throw new RuntimeException(
                    "LinkedIn account is not connected."
            );
        }

        return accessToken;
    }

    // Get LinkedIn Person ID using OpenID Connect
    public String getPersonId() {

        if (accessToken == null ||
                accessToken.isBlank()) {

            throw new RuntimeException(
                    "LinkedIn account is not connected."
            );
        }

        Map<String, Object> response = restClient.get()
                .uri("https://api.linkedin.com/v2/userinfo")
                .header(
                        "Authorization",
                        "Bearer " + accessToken
                )
                .header(
                        "Linkedin-Version",
                        "202601"
                )
                .retrieve()
                .body(Map.class);

        if (response == null ||
                response.get("sub") == null) {

            throw new RuntimeException(
                    "LinkedIn Person ID was not received."
            );
        }

        return (String) response.get("sub");
    }

    // Publish Post on Personal LinkedIn Account
    public String publishPost(
            String postText,
            String personId) {

        if (accessToken == null ||
                accessToken.isBlank()) {

            throw new RuntimeException(
                    "LinkedIn account is not connected."
            );
        }

        Map<String, Object> body = Map.of(
                "author",
                "urn:li:person:" + personId,

                "commentary",
                postText,

                "visibility",
                "PUBLIC",

                "distribution",
                Map.of(
                        "feedDistribution",
                        "MAIN_FEED",

                        "targetEntities",
                        new Object[]{},

                        "thirdPartyDistributionChannels",
                        new Object[]{}
                ),

                "lifecycleState",
                "PUBLISHED",

                "isReshareDisabledByAuthor",
                false
        );

        restClient.post()
                .uri("https://api.linkedin.com/rest/posts")
                .header(
                        "Authorization",
                        "Bearer " + accessToken
                )
                .header(
                        "Linkedin-Version",
                        "202601"
                )
                .header(
                        "X-Restli-Protocol-Version",
                        "2.0.0"
                )
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity();

        return "LinkedIn post published successfully!";
    }
}

