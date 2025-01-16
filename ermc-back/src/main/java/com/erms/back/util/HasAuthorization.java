package com.erms.back.util;

import com.erms.back.Exception.NonAuthorizedException;
import com.erms.back.dto.EmployeeDto;
import com.erms.back.model.Employee;
import com.erms.back.model.enums.Role;
import com.erms.back.service.AuthenticatedDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HasAuthorization {

    private final AuthenticatedDetailsService authenticatedDetailsService;

    public void canEdit(EmployeeDto body) {
        Employee authenticatedEmployee = authenticatedDetailsService.getAuthenticatedEmployee();
        if (authenticatedEmployee.getRole() == Role.ADMIN && authenticatedEmployee.getDepartment() != body.department()) {
            throw new NonAuthorizedException();
        }
    }

    public void canEditRole(EmployeeDto body) {
        Employee authenticatedEmployee = authenticatedDetailsService.getAuthenticatedEmployee();
        if (!authenticatedEmployee.hasRole(Role.ADMIN) && body.role() != null && body.role() != Role.NO_ROLE) {
            throw new NonAuthorizedException();
        }
    }

    public void canDelete(Employee employee) {
        Employee authenticatedEmployee = authenticatedDetailsService.getAuthenticatedEmployee();
        if (!authenticatedEmployee.hasRole(Role.ADMIN) && employee.getRole() != Role.NO_ROLE) {
            throw new NonAuthorizedException();
        }
    }
}
