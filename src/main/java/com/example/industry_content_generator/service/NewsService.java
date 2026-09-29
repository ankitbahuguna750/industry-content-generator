package com.example.industry_content_generator.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class NewsService {

    private final RestClient client = RestClient.create();

    public String getNews(String industry) {

        String url = "https://news.google.com/rss/search?q="
                + industry
                + "&hl=en-IN&gl=IN&ceid=IN:en";

        return client.get()
                .uri(url)
                .retrieve()
                .body(String.class);
    }
}