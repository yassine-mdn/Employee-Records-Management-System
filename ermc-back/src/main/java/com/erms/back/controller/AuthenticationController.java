package com.erms.back.controller;


import com.erms.back.auth.AuthenticationRequest;
import com.erms.back.auth.AuthenticationResponse;
import com.erms.back.auth.RegisterRequest;
import com.erms.back.model.Employee;
import com.erms.back.repository.EmployeeRepository;
import com.erms.back.service.AuthenticationService;
import com.erms.back.util.ErrorHandling.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication Controller", description = "Handles all authentication related operations")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final EmployeeRepository employeeRepository;

    @Operation(summary = "login using credentials")
    @SecurityRequirements()
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful"),
            @ApiResponse(responseCode = "401", description = "Dont have the necessary authorization")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @Operation(summary = "refresh token" , description = "the refresh token should be placed in the headers in place of the access token")
    @PostMapping("/refresh-token")
    @SecurityRequirements()
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authenticationService.refreshToken(request, response);
    }

    @Operation(summary = "Add the ability to login to a pre-existing employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful"),
            @ApiResponse(responseCode = "401", description = "Dont have the necessary authorization"),
            @ApiResponse(
                    responseCode = "404",
                    description = "invalid employee id",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = "{   \"status\": \"NOT_FOUND\",   \"timestamp\": \"15-01-2025 06:28:33\",   \"message\": \"Employee Not Found\",   \"debugMessage\": null,   \"subErrors\": null }")),

                    }
            )
    })
    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Employee> register(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(authenticationService.register(request.getUserId(), request.getPassword()));
    }

}