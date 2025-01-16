package com.erms.back.dto;

import com.erms.back.model.enums.Department;
import com.erms.back.model.enums.EmploymentStatus;
import com.erms.back.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public record EmployeeDto(
        @NotBlank
        String fullName,
        @NotBlank
        String jobTitle,
        @NotBlank
        Department department,
        @NotNull
        Date hireDate,
        @NotBlank
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
