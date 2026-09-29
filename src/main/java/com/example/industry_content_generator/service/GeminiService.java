package com.example.industry_content_generator.service;

import com.example.industry_content_generator.dto.ContentRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    private final RestClient restClient =
            RestClient.create();


    // =========================================
    // GENERATE LINKEDIN POSTS
    // =========================================

    public String generatePost(
            ContentRequest request,
            String news) {


        // =====================================
        // CREATE PROMPT
        // =====================================

        String prompt = """

                Create 3 different LinkedIn posts
                based on the latest industry news.

                Industry:
                %s

                Tone:
                %s

                Latest industry news:
                %s


                Requirements for EACH post:

                - Use the latest industry news as the main context.
                - Start with a strong and interesting hook.
                - Keep paragraphs short.
                - Make the post visually attractive.
                - Use relevant emojis such as 🚀 🔥 💡 👇 ✅ when appropriate.
                - Use bullet points where useful.
                - Add an interesting question or curiosity.
                - Explain the main takeaway.
                - Keep the language simple and professional.
                - Do not write large paragraphs.
                - Do not use unnecessary emojis.
                - Add 3 to 5 relevant hashtags.
                - Do not mention that AI generated the post.
                - Do not invent facts that are not supported by the news.
                - Make all 3 posts different in wording and style.


                Use exactly this format:


                POST 1

                [first LinkedIn post]


                POST 2

                [second LinkedIn post]


                POST 3

                [third LinkedIn post]


                Return only these 3 posts.

                """.formatted(

                request.getIndustry(),

                request.getTone(),

                news

        );


        // =====================================
        // REQUEST BODY
        // =====================================

        Map<String, Object> body =

                Map.of(

                        "contents",

                        List.of(

                                Map.of(

                                        "parts",

                                        List.of(

                                                Map.of(

                                                        "text",
                                                        prompt

                                                )

                                        )

                                )

                        )

                );


        // =====================================
        // GEMINI URL
        // =====================================

        String url =

                "https://generativelanguage.googleapis.com/v1beta/models/"

                        + model

                        + ":generateContent?key="

                        + apiKey;



        // =====================================
        // MAX RETRIES
        // =====================================

        int maxAttempts = 3;



        // =====================================
        // RETRY LOOP
        // =====================================

        for (

                int attempt = 1;

                attempt <= maxAttempts;

                attempt++

        ) {


            try {


                System.out.println(
                        "Gemini request attempt "
                                + attempt
                                + " of "
                                + maxAttempts
                );


                // =================================
                // SEND REQUEST
                // =================================

                Map response =

                        restClient.post()

                                .uri(url)

                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )

                                .body(body)

                                .retrieve()

                                .body(Map.class);



                // =================================
                // CHECK RESPONSE
                // =================================

                if (response == null) {

                    throw new RuntimeException(
                            "Gemini returned an empty response."
                    );

                }



                // =================================
                // GET CANDIDATES
                // =================================

                List candidates =

                        (List)
                                response.get(
                                        "candidates"
                                );



                if (

                        candidates == null

                                ||

                                candidates.isEmpty()

                ) {


                    throw new RuntimeException(

                            "Gemini did not return any content."

                    );

                }



                // =================================
                // FIRST CANDIDATE
                // =================================

                Map firstCandidate =

                        (Map)
                                candidates.get(0);



                // =================================
                // RESPONSE CONTENT
                // =================================

                Map responseContent =

                        (Map)
                                firstCandidate.get(
                                        "content"
                                );



                if (responseContent == null) {


                    throw new RuntimeException(

                            "Gemini response content is empty."

                    );

                }



                // =================================
                // RESPONSE PARTS
                // =================================

                List parts =

                        (List)
                                responseContent.get(
                                        "parts"
                                );



                if (

                        parts == null

                                ||

                                parts.isEmpty()

                ) {


                    throw new RuntimeException(

                            "Gemini response parts are empty."

                    );

                }



                // =================================
                // FIRST PART
                // =================================

                Map firstPart =

                        (Map)
                                parts.get(0);



                // =================================
                // GENERATED TEXT
                // =================================

                String generatedText =

                        (String)
                                firstPart.get(
                                        "text"
                                );



                if (

                        generatedText == null

                                ||

                                generatedText.isBlank()

                ) {


                    throw new RuntimeException(

                            "Gemini generated empty text."

                    );

                }



                // =================================
                // SUCCESS
                // =================================

                System.out.println(
                        "Gemini response received successfully."
                );


                return generatedText;



            }

            // =====================================
            // 503 SERVICE UNAVAILABLE
            // =====================================

            catch (
                    HttpServerErrorException.ServiceUnavailable e
            ) {


                System.out.println(
                        "Gemini returned 503 Service Unavailable."
                );


                if (attempt == maxAttempts) {


                    throw new RuntimeException(

                            "Gemini is temporarily unavailable. "
                                    + "The AI model is currently busy. "
                                    + "Please try again after a few minutes."

                    );

                }


                waitBeforeRetry(attempt);

            }



            // =====================================
            // OTHER 5XX ERRORS
            // =====================================

            catch (
                    HttpServerErrorException e
            ) {


                System.out.println(

                        "Gemini server error: "
                                + e.getStatusCode()

                );


                if (attempt == maxAttempts) {


                    throw new RuntimeException(

                            "Gemini server is currently unavailable. "
                                    + "Please try again later."

                    );

                }


                waitBeforeRetry(attempt);

            }



            // =====================================
            // 429 TOO MANY REQUESTS
            // =====================================

            catch (
                    HttpClientErrorException.TooManyRequests e
            ) {


                System.out.println(

                        "Gemini API rate limit reached."

                );


                throw new RuntimeException(

                        "Gemini API limit has been reached. "
                                + "Please try again later."

                );

            }



            // =====================================
            // 400 BAD REQUEST
            // =====================================

            catch (
                    HttpClientErrorException.BadRequest e
            ) {


                System.out.println(

                        "Gemini returned 400 Bad Request."

                );


                throw new RuntimeException(

                        "Gemini rejected the request. "
                                + "Please check the model name and request settings."

                );

            }



            // =====================================
            // 401 / 403
            // =====================================

            catch (
                    HttpClientErrorException.Unauthorized
                    | HttpClientErrorException.Forbidden e
            ) {


                System.out.println(

                        "Gemini API authentication/permission error."

                );


                throw new RuntimeException(

                        "Gemini API authentication failed. "
                                + "Please check your API key."

                );

            }



            // =====================================
            // OTHER 4XX ERRORS
            // =====================================

            catch (
                    HttpClientErrorException e
            ) {


                System.out.println(

                        "Gemini API error: "
                                + e.getStatusCode()

                );


                throw new RuntimeException(

                        "Gemini API request failed. "
                                + "Please check your API settings."

                );

            }



            // =====================================
            // OTHER ERRORS
            // =====================================

            catch (Exception e) {


                System.out.println(

                        "Unexpected Gemini error: "
                                + e.getMessage()

                );


                throw new RuntimeException(

                        "Something went wrong while generating "
                                + "LinkedIn content."

                );

            }

        }



        // =========================================
        // FINAL ERROR
        // =========================================

        throw new RuntimeException(

                "Unable to generate LinkedIn posts."

        );

    }



    // =========================================
    // WAIT BEFORE RETRY
    // =========================================

    private void waitBeforeRetry(int attempt) {


        try {


            // 3 seconds after first attempt
            // 6 seconds after second attempt

            long waitTime =
                    attempt * 3000L;


            System.out.println(

                    "Waiting "
                            + waitTime
                            + " ms before retry..."

            );


            Thread.sleep(waitTime);


        }

        catch (InterruptedException e) {


            Thread.currentThread().interrupt();


            throw new RuntimeException(

                    "Gemini retry was interrupted."

            );

        }

    }

}