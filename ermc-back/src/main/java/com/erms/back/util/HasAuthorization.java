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
        if (authenticatedEmployee.getRole() == Role.MANAGER && !authenticatedEmployee.getDepartment().equals(body.department())) {
            throw new NonAuthorizedException("Managers can only act on people within their own department");
        }
    }

    public void canEditRole(EmployeeDto body) {
        Employee authenticatedEmployee = authenticatedDetailsService.getAuthenticatedEmployee();
        if (authenticatedEmployee.hasRole(Role.ADMIN)) {
            return;
        }

        if (body.role() != null && body.role() != Role.NO_ROLE) {
            throw new NonAuthorizedException("Only Admin can edit roles");
        }
    }

    public void canDelete(Employee employee) {
        Employee authenticatedEmployee = authenticatedDetailsService.getAuthenticatedEmployee();
        if (!authenticatedEmployee.hasRole(Role.ADMIN) && employee.getRole() != Role.NO_ROLE) {
            throw new NonAuthorizedException("Can't delete Employee with higher authorities");
        }
    }

    public boolean isManager() {
        Employee authenticatedEmployee = authenticatedDetailsService.getAuthenticatedEmployee();
        return authenticatedEmployee.hasRole(Role.MANAGER);
    }

    public Employee getEmployee() {
        return authenticatedDetailsService.getAuthenticatedEmployee();
    }
}
