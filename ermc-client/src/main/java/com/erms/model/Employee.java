package com.erms.model;

import com.erms.model.enums.Department;
import com.erms.model.enums.EmploymentStatus;
import com.erms.model.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    @JsonProperty("id")
    private String id;

    @JsonProperty("fullName")
    private String fullName;

    @JsonProperty("jobTitle")
    private String jobTitle;

    @JsonProperty("hireDate")
    private LocalDate hireDate;

    @JsonProperty("department")
    private Department department;

    @JsonProperty("employmentStatus")
    private EmploymentStatus employmentStatus;

    @JsonProperty("contactInformation")
    private String contactInformation;

    @JsonProperty("address")
    private String address;

    @JsonProperty("email")
    private String email;

    @JsonProperty("role")
    private Role role;

}
