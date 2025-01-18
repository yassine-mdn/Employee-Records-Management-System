package com.erms.model;

import com.erms.model.enums.Department;
import com.erms.model.enums.EmploymentStatus;
import com.erms.model.enums.Role;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDto {
  private String fullName;

  private String jobTitle;

  private LocalDate hireDate;

  private Department department;

  private EmploymentStatus employmentStatus;

  private String contactInformation;

  private String address;

  private String email;

  private Role role;

}
