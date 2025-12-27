package com.prpo.chat.service.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.prpo.chat.service.dtos.IndexUserRequestDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SearchClient {

    private final RestTemplate restTemplate;

    @Value("${search.service.base-url}")
    private String baseUrl;

    public void indexUser(IndexUserRequestDto indexUser) {
        try {
            String url = baseUrl + "/index/user";
            restTemplate.postForLocation(url, indexUser);
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to sent user to search service", e);
        }
    }
}