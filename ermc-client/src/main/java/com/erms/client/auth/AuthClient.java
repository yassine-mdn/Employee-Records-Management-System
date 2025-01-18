package com.erms.client.auth;

import com.erms.model.ApiError;
import com.erms.model.AuthenticationRequest;
import com.erms.model.AuthenticationResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class AuthClient {

    private final static String BASE_URL = "http://localhost:8080";
    private final HttpClient client;
    private final ObjectMapper objectMapper;

    public AuthClient() {
        client = HttpClient.newHttpClient();
        objectMapper = new ObjectMapper();
    }


    public Object login(AuthenticationRequest requestBody) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL+"/api/v1/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), AuthenticationResponse.class);
        }
        return "Unauthorized";
    }




}
