package com.erms.back.service;



import com.erms.back.Exception.EmployeeNotFoundException;
import com.erms.back.Exception.PageOutOfBoundException;
import com.erms.back.dto.EmployeeDto;
import com.erms.back.model.Employee;
import com.erms.back.model.enums.Role;
import com.erms.back.repository.EmployeeRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import org.modelmapper.record.RecordModule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Spy
    private ModelMapper mapper = new ModelMapper().registerModule(new RecordModule());

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee;
    private EmployeeDto employeeDto;
    private String employeeId;
    private Role role;

    @BeforeEach
    void setUp() {

        employeeId = "UUID";
        role = Role.NO_ROLE;
        employee = Employee.builder()
                .id(employeeId)
                .address("Address")
                .email("email")
                .department("Department")
                .employmentStatus("EmploymentStatus")
                .contactInformation("ContactInformation")
                .hireDate(LocalDate.of(1995,9,13))
                .fullName("jordan teller carter")
                .password("password")
                .role(role)
                .build();

        employeeDto = new EmployeeDto(
                "John Doe",
                "Software Engineer",
                "Technology",
                LocalDate.of(2023, 1, 15),
                "Full-Time",
                "john.doe@example.com",
                "123 Elm Street, Springfield, USA",
                role
        );
    }

    @Test
    void getById_ShouldReturnEmployee_WhenEmployeeExists() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        Employee result = employeeService.getById(employeeId);

        assertNotNull(result);
        assertEquals("jordan teller carter", result.getFullName());
        verify(employeeRepository, times(1)).findById(employeeId);
    }

    @Test
    void getById_ShouldThrowException_WhenEmployeeNotFound() {
        when(employeeRepository.findById("fake id")).thenThrow(EmployeeNotFoundException.class);

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.getById("fake id"));
    }



    @Test
    void update_ShouldUpdateEmployee_WhenEmployeeExists() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(employee)).thenReturn(employee);

        Employee result = employeeService.update(employeeId, employeeDto);

        assertNotNull(result);
        assertEquals("John Doe", result.getFullName());
        verify(employeeRepository, times(1)).findById(employeeId);
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    void update_ShouldThrowException_WhenEmployeeNotFound() {
        when(employeeRepository.findById("fake_id")).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.update("fake_id", employeeDto));
        verify(employeeRepository, times(1)).findById("fake_id");
    }

    @Test
    void getPage_shouldReturnPageEmptyPage_WhenNoEmployeeInDb(){
        when(employeeRepository.findAll(PageRequest.of(0,1))).thenReturn(
                Page.empty()
        );

        Page<Employee> result = employeeService.getPage(PageRequest.of(0,1));
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());

    }

    @Test
    void getPage_shouldReturnPageWithEmployee_WhenEmployeeInDb(){
        Page<Employee> employeePage = new PageImpl<>(Collections.singletonList(employee),
                PageRequest.of(0, 1),
                1);

        when(employeeRepository.findAll(PageRequest.of(0, 1))).thenReturn(employeePage);

        Page<Employee> result = employeeService.getPage(PageRequest.of(0, 1));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("jordan teller carter", result.getContent().get(0).getFullName());
    }

    @Test
    void getPage_shouldThrowException_WhenPageOutOfBounds() {

        Pageable pageable = PageRequest.of(2, 1);

        when(employeeRepository.findAll(pageable)).thenReturn(
                new PageImpl<>(Collections.emptyList(), pageable, 1)
        );

        assertThrows(PageOutOfBoundException.class, () -> employeeService.getPage(pageable));
        verify(employeeRepository, times(1)).findAll(pageable);
    }



    @Test
    void save_ShouldAddEmployee_WhenValidDtoProvided() {
        when(mapper.map(employeeDto, Employee.class)).thenReturn(
                Employee.builder()
                        .id(employeeId)
                        .address("Address")
                        .email("email")
                        .department("Department")
                        .employmentStatus("EmploymentStatus")
                        .contactInformation("ContactInformation")
                        .hireDate(LocalDate.of(1995,9,13))
                        .fullName("jordan teller carter")
                        .password("password")
                        .role(role)
                        .build()
        );
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Employee result = employeeService.save(employeeDto);

        assertNotNull(result);
        assertEquals("jordan teller carter", result.getFullName());
        assertEquals("email", result.getEmail());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void delete_ShouldDeleteEmployee_WhenEmployeeExists() {
        doNothing().when(employeeRepository).deleteById("id");

        employeeService.delete("id");

        verify(employeeRepository, times(1)).deleteById("id");
    }

}
