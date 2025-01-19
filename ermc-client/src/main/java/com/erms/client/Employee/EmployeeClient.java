package com.erms.client.Employee;

import com.erms.Application;
import com.erms.context.AuthenticatedEmployee;
import com.erms.model.*;
import com.erms.model.enums.Department;
import com.erms.model.enums.EmploymentStatus;
import com.erms.utils.EnumUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import raven.toast.Notifications;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

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

    public Object getEmployees(int pageNumber) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL+"/api/v1/employees?page="+pageNumber+"&size=10"))
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

    public Object searchEmployees(String keyword, int pageNumber) throws IOException, InterruptedException {
        String encodedFilter = URLEncoder.encode(filterFormater(keyword), StandardCharsets.UTF_8.toString());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL+"/api/v1/employees?page="+pageNumber+"&size=10&filter="+encodedFilter))
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

    private String filterFormater(String keyword) {
        if (keyword.contains("query=")) {
            return keyword.replace("query=", "");
        } else if (EnumUtils.isEnumName(keyword, Department.class)) {
            return String.format("department : '%s'",keyword);
        } else return String.format("fullName ~~ '*%1$s*' or id ~~ '*%1$s*' or jobTitle ~~ '*%1$s*' or email ~~ '*%1$s*'",keyword);
    }
}
