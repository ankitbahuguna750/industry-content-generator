
package com.example.industry_content_generator.service;

import com.example.industry_content_generator.dto.ContentRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    private final RestClient restClient = RestClient.create();

    private static final String API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/";

    // Generates three professional LinkedIn posts using industry news.
    public String generatePost(ContentRequest request, String news) {

        if (apiKey == null || apiKey.isBlank()) {
            throw new RuntimeException("Gemini API key is missing.");
        }

        if (model == null || model.isBlank()) {
            throw new RuntimeException("Gemini model name is missing.");
        }

        String prompt = """
                You are a professional LinkedIn content writer.

                Create exactly 3 different, high-quality LinkedIn posts
                based on the industry news provided below.

                Industry: %s

                Tone: %s

                Latest industry news:
                %s

                IMPORTANT FORMATTING RULES:

                1. Give each post a heading exactly like:
                   POST 1
                   POST 2
                   POST 3

                2. Start each post with an interesting and professional hook.

                3. Write 2 or 3 short paragraphs explaining the news.
                   Keep every paragraph on a separate line.
                   Leave one blank line between paragraphs.

                4. Include this section heading:
                   Key developments shaping this shift include:

                   Add 3 relevant bullet points below it.
                   Put each bullet point on its own line and start it with •.

                5. Include this section:
                   💡 Curiosity to ponder:
                   Ask one thoughtful question related to the news.

                6. Include this section:
                   ✅ The Takeaway:
                   Write a useful, practical conclusion.

                7. Add 3 to 5 relevant hashtags at the end.
                   Put hashtags together on their own final line.

                8. Leave a blank line between each major section.

                9. Use simple, professional English suitable for LinkedIn.
                   Use relevant emojis only where appropriate.

                10. Make all three posts meaningfully different.
                    Use different hooks and wording for each post.

                11. Do not invent statistics, quotes, dates, facts,
                    announcements or sources that are not supported
                    by the supplied news.

                12. If the news does not support a specific detail,
                    do not present that detail as a confirmed fact.

                REQUIRED OUTPUT STRUCTURE:

                POST 1

                [Interesting opening hook.]

                [Short paragraph explaining the news.]

                [Another short paragraph with context.]

                Key developments shaping this shift include:
                • [First development]
                • [Second development]
                • [Third development]

                💡 Curiosity to ponder:
                [One thoughtful question]

                ✅ The Takeaway:
                [Practical conclusion]

                #[RelevantHashtag] #[RelevantHashtag] #[RelevantHashtag]

                POST 2

                [Write a different post using the same structure.]

                POST 3

                [Write another different post using the same structure.]

                Return only the three posts.
                Do not include an introduction or explanation outside them.
                """.formatted(
                request.getIndustry(),
                request.getTone(),
                news == null || news.isBlank()
                        ? "No news text was provided. Do not invent specific news events."
                        : news
        );

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", prompt)
                                )
                        )
                )
        );

        // Removes the models/ prefix if it is already present.
        String modelName = model.trim();

        if (modelName.startsWith("models/")) {
            modelName = modelName.substring("models/".length());
        }

        String url = API_URL + modelName + ":generateContent";

        try {
            Map<?, ?> response = restClient.post()
                    .uri(url)
                    .header("x-goog-api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            if (response == null) {
                throw new RuntimeException(
                        "Gemini returned an empty response."
                );
            }

            Object candidatesObject = response.get("candidates");

            if (!(candidatesObject instanceof List<?> candidates)
                    || candidates.isEmpty()) {
                throw new RuntimeException(
                        "Gemini returned no candidates: " + response
                );
            }

            Object candidateObject = candidates.get(0);

            if (!(candidateObject instanceof Map<?, ?> candidate)) {
                throw new RuntimeException(
                        "Invalid Gemini candidate response."
                );
            }

            Object contentObject = candidate.get("content");

            if (!(contentObject instanceof Map<?, ?> content)) {
                throw new RuntimeException(
                        "Gemini response content is missing."
                );
            }

            Object partsObject = content.get("parts");

            if (!(partsObject instanceof List<?> parts)
                    || parts.isEmpty()) {
                throw new RuntimeException(
                        "Gemini response parts are missing."
                );
            }

            // Collects the generated text from Gemini's response.
            StringBuilder generatedText = new StringBuilder();

            for (Object partObject : parts) {
                if (partObject instanceof Map<?, ?> part) {
                    Object textObject = part.get("text");

                    if (textObject instanceof String text) {
                        if (!generatedText.isEmpty()) {
                            generatedText.append("\n");
                        }

                        generatedText.append(text);
                    }
                }
            }

            String result = generatedText.toString().trim();

            if (result.isBlank()) {
                throw new RuntimeException(
                        "Gemini returned empty generated text."
                );
            }

            System.out.println(
                    "Gemini generated LinkedIn posts successfully."
            );

            return result;

        } catch (HttpStatusCodeException e) {

            // Prints Google's error details in the IntelliJ console.
            System.err.println(
                    "Gemini HTTP status: " + e.getStatusCode()
            );

            System.err.println(
                    "Gemini error response: " + e.getResponseBodyAsString()
            );

            throw new RuntimeException(
                    "Gemini API request failed with HTTP "
                            + e.getStatusCode()
                            + ". Check the IntelliJ console for details."
            );
        }
    }
}
