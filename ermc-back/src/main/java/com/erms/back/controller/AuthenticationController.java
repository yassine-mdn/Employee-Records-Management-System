package com.erms.back.controller;


import com.erms.back.auth.AuthenticationRequest;
import com.erms.back.auth.AuthenticationResponse;
import com.erms.back.auth.RegisterRequest;
import com.erms.back.model.Employee;
import com.erms.back.repository.EmployeeRepository;
import com.erms.back.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final EmployeeRepository employeeRepository;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authenticationService.refreshToken(request, response);
    }

    @PostMapping("/register")
    public ResponseEntity<Employee> register(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(authenticationService.register(request.getUserId(), request.getPassword()));
    }

}