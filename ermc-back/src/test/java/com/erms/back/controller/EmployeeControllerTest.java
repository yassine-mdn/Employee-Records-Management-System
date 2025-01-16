package com.erms.back.controller;

import com.erms.back.Exception.EmployeeNotFoundException;
import com.erms.back.Exception.PageOutOfBoundException;
import com.erms.back.config.ModelMapperConfig;
import com.erms.back.config.SecurityConfig;
import com.erms.back.dto.EmployeeDto;
import com.erms.back.model.Employee;
import com.erms.back.model.enums.Department;
import com.erms.back.model.enums.EmploymentStatus;
import com.erms.back.model.enums.Role;
import com.erms.back.service.EmployeeService;
import com.erms.back.util.ErrorHandling.RestExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
@ContextConfiguration(classes = {EmployeeController.class, SecurityConfig.class, RestExceptionHandler.class, ModelMapperConfig.class})
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    Employee employee;
    EmployeeDto requestBody;
    String employeeId = "52397a9d-aa05-4ef2-baed-95a0651f0b38";
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {

        LocalDate localDate = LocalDate.of(1995, 9, 13);
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Role role = Role.NO_ROLE;
        employee = Employee.builder()
                .id(employeeId)
                .address("Address")
                .email("email")
                .department(Department.IT)
                .employmentStatus(EmploymentStatus.ACTIVE)
                .contactInformation("ContactInformation")
                .hireDate(date)
                .fullName("jordan teller carter")
                .password("password")
                .role(role)
                .build();
        requestBody = new EmployeeDto(
                "John Doe",
                "Software Engineer",
                Department.IT,
                date,
                EmploymentStatus.ACTIVE,
                "06666666666",
                "123 Elm Street, Springfield, USA",
                "email@email.com",
                role
        );
    }

    @Test
    void getEmployeePage_ShouldReturnEmployeePage_WhenValidPaginationParameters() throws Exception {
        Pageable validPageable = PageRequest.of(0, 15);
        Page<Employee> resultPage = new PageImpl<>(Collections.singletonList(employee), validPageable, 1);

        when(employeeService.getPage(validPageable)).thenReturn(resultPage);

        ResultActions result = mockMvc.perform(
                get("/api/v1/employees")
                        .param("page", "0")
                        .param("size", "15")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        result.andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.page").isArray())
                .andExpect(jsonPath("$.page[0].id").value(employee.getId()));

    }

    @Test
    void getEmployeePage_ShouldThrowPageOutOfBoundException_WhenPageIsPageOutOfBoundException() throws Exception {
        Pageable outOfBoundPageable = PageRequest.of(200, 15);


        when(employeeService.getPage(outOfBoundPageable)).thenThrow(new PageOutOfBoundException("Page number is greater than the total number of pages"));

        ResultActions result = mockMvc.perform(
                get("/api/v1/employees")
                        .param("page", "200")
                        .param("size", "15")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        result.andDo(print()).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Page number is greater than the total number of pages"));

    }

    @Test
    void getEmployeeById_ShouldReturnEmployee_WhenValidEmployeeId() throws Exception {
        when(employeeService.getById(employeeId)).thenReturn(employee);

        ResultActions result = mockMvc.perform(get("/api/v1/employees/" + employeeId));

        result.andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(employee.getId()));
    }

    @Test
    void getEmployeeById_ShouldThrowEmployeeNotFoundException_WhenEmployeeDoesNotExist() throws Exception {
        when(employeeService.getById("fake_id")).thenThrow(new EmployeeNotFoundException());

        ResultActions result = mockMvc.perform(get("/api/v1/employees/" + "fake_id"));

        result.andDo(print()).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Employee Not Found"));
    }

    @Test
    void deleteEmployeeById_ShouldDeleteEmployee_WhenValidEmployeeId() throws Exception {
        doNothing().when(employeeService).delete(employeeId);

        ResultActions result = mockMvc.perform(delete("/api/v1/employees/" + employeeId));

        result.andDo(print()).andExpect(status().isNoContent());
    }

    @Test
    void deleteEmployeeById_ShouldThrowEmployeeNotFoundException_WhenEmployeeDoesNotExist() throws Exception {
        doThrow(new EmployeeNotFoundException()).when(employeeService).delete("fake_id");

        ResultActions result = mockMvc.perform(delete("/api/v1/employees/" + "fake_id"));

        result.andDo(print()).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Employee Not Found"));
    }


    @Test
    void addEmployee_ShouldAddEmployee_WhenValidRequestBody() throws Exception {
        when(employeeService.save(requestBody)).thenReturn(employee);

        ResultActions result = mockMvc.perform(
                post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
        );

        result.andDo(print()).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(employee.getId()));
    }

    @Test
    void updateEmployee_ShouldThrowEmployeeNotFoundException_WhenEmployeeDoesNotExist() throws Exception {
        when(employeeService.update(employeeId, requestBody)).thenThrow(new EmployeeNotFoundException());

        ResultActions result = mockMvc.perform(
                put("/api/v1/employees/" + employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
        );

        result.andDo(print()).andExpect(status().isNotFound());
    }

    @Test
    void updateEmployee_ShouldReturnUpdatedEmployee_WhenEmployeeExists() throws Exception {
        Employee updatedEmployee = modelMapper.map(requestBody, Employee.class);
        when(employeeService.update(employeeId, requestBody)).thenReturn(updatedEmployee);

        ResultActions result = mockMvc.perform(
                put("/api/v1/employees/" + employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody))
        );

        result.andDo(print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedEmployee.getId()))
                .andExpect(jsonPath("$.address").value(updatedEmployee.getAddress()))
                .andExpect(jsonPath("$.email").value(updatedEmployee.getEmail()))
                .andExpect(jsonPath("$.fullName").value(updatedEmployee.getFullName()));
    }
}