package com.erms.back.service;

import com.erms.back.Exception.EmployeeNotFoundException;
import com.erms.back.model.Employee;
import com.erms.back.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


/**
 * Handles getting the authenticated user details
 */
@Service
@RequiredArgsConstructor
public class AuthenticatedDetailsService {

    private final EmployeeRepository repository;

    /**
     * Gets the authenticated user details
     * @return the authenticated user details
     */
    public Employee getAuthenticatedEmployee(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            return repository.findByEmail(currentUserName).orElseThrow(EmployeeNotFoundException::new);
        }else{
            throw new RuntimeException("No User");
        }

    }
}
