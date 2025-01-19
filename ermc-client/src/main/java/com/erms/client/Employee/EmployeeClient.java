package com.erms.client.Employee;

import com.erms.Application;
import com.erms.context.AuthenticatedEmployee;
import com.erms.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import raven.toast.Notifications;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class EmployeeClient {

    private final static String BASE_URL = "http://localhost:8080";
    private final HttpClient client;
    private final ObjectMapper objectMapper;
    private final String token;

    public EmployeeClient() {
        client = HttpClient.newHttpClient();
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        token = AuthenticatedEmployee.getInstance().getAuthenticationResponse().getAccessToken();
        if (token == null) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.BOTTOM_RIGHT,"Session Expired Please Login Again");
            Application.logout();
        }
    }

    public Object addEmployee(EmployeeDto requestBody) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL+"/api/v1/employees"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer "+token)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 401) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.BOTTOM_RIGHT,"Session Expired Please Login Again");
            Application.logout();
        }
        if (response.statusCode() == 201) {
            return objectMapper.readValue(response.body(), Employee.class);
        }
        return objectMapper.readValue(response.body(), ApiError.class);
    }

    public Object getEmployees() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL+"/api/v1/employees?page=0&size=10"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer "+token)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 401) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.BOTTOM_RIGHT,"Session Expired Please Login Again");
            Application.logout();
        }
        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), new TypeReference<PageWrapper<Employee>>() {
            });
        }
        return objectMapper.readValue(response.body(), ApiError.class);
    }

    public Object getEmployeeById(String id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL+"/api/v1/employees/"+id))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer "+token)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 401) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.BOTTOM_RIGHT,"Session Expired Please Login Again");
            Application.logout();
        }
        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), Employee.class);
        }
        return objectMapper.readValue(response.body(), ApiError.class);
    }

    public Object updateEmployee(String id, EmployeeDto requestBody) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL+"/api/v1/employees/"+id))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer "+token)
                .PUT(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 401) {
            Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.BOTTOM_RIGHT,"Session Expired Please Login Again");
            Application.logout();
        }
        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), Employee.class);
        }
        return objectMapper.readValue(response.body(), ApiError.class);
    }

    public Object deleteEmployee(String id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL+"/api/v1/employees/"+id))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer "+token)
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 401) {
                Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.BOTTOM_RIGHT,"Session Expired Please Login Again");
                Application.logout();
            }
            if (response.statusCode() == 204) {
                return "Deleted";
            }
            return objectMapper.readValue(response.body(), ApiError.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
