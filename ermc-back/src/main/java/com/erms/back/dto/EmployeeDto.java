package com.erms.back.dto;

import com.erms.back.model.enums.Department;
import com.erms.back.model.enums.EmploymentStatus;
import com.erms.back.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record EmployeeDto(
        @NotBlank
        String fullName,
        @NotBlank
        String jobTitle,

        Department department,
        @NotNull
        LocalDate hireDate,

        EmploymentStatus employmentStatus,
        @NotBlank
        String contactInformation,
        @NotBlank
        String address,
        @Email
        String email,

        Role role
) {
}
