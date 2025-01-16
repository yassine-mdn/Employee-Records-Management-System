package com.erms.back.service;


import com.erms.back.Exception.EmployeeNotFoundException;
import com.erms.back.Exception.NonAuthorizedException;
import com.erms.back.auth.AuthenticationRequest;
import com.erms.back.auth.AuthenticationResponse;
import com.erms.back.model.Employee;
import com.erms.back.model.enums.Role;
import com.erms.back.repository.EmployeeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {


    private final EmployeeRepository employeeRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;



    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = employeeRepository.findByEmail(request.getEmail())
                .orElseThrow(EmployeeNotFoundException::new);
        if (user.getRole().equals(Role.NO_ROLE))
            throw new NonAuthorizedException();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        return AuthenticationResponse.builder()
                .id(user.getId())
                .role(user.getRole())
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public Employee register(@NotNull String id, @NotNull String password) {
        Employee employee = employeeRepository.findById(id).map(oldEmployeeData -> {
            oldEmployeeData.setPassword(password);
            return oldEmployeeData;
        }).orElseThrow(EmployeeNotFoundException::new);
        return employeeRepository.save(employee);
    }


    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            UserDetails userDetails = this.employeeRepository.findByEmail(userEmail).orElseThrow();
            if (jwtService.isTokenValid(refreshToken , userDetails)) {
                var jwtToken = jwtService.generateToken(userDetails);
                // revoke old refresh token
                // save new refresh token
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(jwtToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
