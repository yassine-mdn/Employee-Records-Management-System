package com.erms.back.dto;

import com.erms.back.model.enums.Role;

import java.time.LocalDate;

public record EmployeeDto(
        String fullName,
        String jobTitle,
        String department,
        LocalDate hireDate,
        String employmentStatus,
        String contactInformation,
        String address,
        Role role
) {
}
