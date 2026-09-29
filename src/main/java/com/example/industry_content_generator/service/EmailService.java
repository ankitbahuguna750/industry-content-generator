package com.example.industry_content_generator.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendTestEmail(String receiverEmail) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setFrom(senderEmail);

        message.setTo(receiverEmail);

        message.setSubject(
                "AI LinkedIn Content Generator - Test Email"
        );

        message.setText("""
                Hello!

                This is a test email from your
                AI LinkedIn Content Generator.

                Email service is working successfully. ✅

                Regards,
                AI LinkedIn Content Generator
                """);

        mailSender.send(message);
    }


    public void sendLinkedInPosts(
            String receiverEmail,
            String posts) {

        try {

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true,
                            "UTF-8"
                    );

            helper.setFrom(senderEmail);

            helper.setTo(receiverEmail);

            helper.setSubject(
                    "AI LinkedIn Content - Choose Your Post"
            );

            String[] postArray =
                    posts.split(
                            "(?=POST [1-3])"
                    );

            StringBuilder html =
                    new StringBuilder();

            html.append("""
                    <html>
                    <body style="font-family: Arial, sans-serif;">

                    <h2>🚀 AI LinkedIn Content Generator</h2>

                    <p>
                    Your AI-generated LinkedIn posts are ready.
                    Choose one post and publish it directly on LinkedIn.
                    </p>

                    """);


            for (int i = 0; i < postArray.length; i++) {

                String post = postArray[i].trim();

                if (post.isEmpty()) {
                    continue;
                }

                int postNumber = i + 1;

                html.append(
                        "<div style='"
                                + "border:1px solid #ddd;"
                                + "padding:20px;"
                                + "margin-bottom:20px;"
                                + "border-radius:10px;"
                                + "'>"
                );

                html.append(
                        "<h3>POST "
                                + postNumber
                                + "</h3>"
                );

                html.append(
                        "<p>"
                                + post
                                .replace(
                                        "\n",
                                        "<br>"
                                )
                                + "</p>"
                );

                String publishUrl =
                        "http://localhost:8081/linkedin/publish-from-email"
                                + "?post="
                                + java.net.URLEncoder.encode(
                                post,
                                java.nio.charset.StandardCharsets.UTF_8
                        );

                html.append(
                        "<a href='"
                                + publishUrl
                                + "' "
                                + "style='"
                                + "display:inline-block;"
                                + "padding:12px 20px;"
                                + "background:#0A66C2;"
                                + "color:white;"
                                + "text-decoration:none;"
                                + "border-radius:6px;"
                                + "font-weight:bold;"
                                + "'>"
                                + "🚀 Publish Post "
                                + postNumber
                                + " on LinkedIn"
                                + "</a>"
                );

                html.append("</div>");
            }


            html.append("""
                    <p>
                    Regards,<br>
                    AI LinkedIn Content Generator
                    </p>

                    </body>
                    </html>
                    """);


            helper.setText(
                    html.toString(),
                    true
            );

            mailSender.send(message);

        } catch (MessagingException e) {

            throw new RuntimeException(
                    "Failed to create LinkedIn posts email.",
                    e
            );
        }
    }
}