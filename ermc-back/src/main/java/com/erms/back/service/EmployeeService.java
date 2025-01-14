package com.erms.back.service;


import com.erms.back.Exception.EmployeeNotFoundException;
import com.erms.back.dto.EmployeeDto;
import com.erms.back.model.Employee;
import com.erms.back.repository.EmployeeRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ModelMapper mapper;

    public Employee getById(@NotNull String id) {
        log.info("Attempting to get employee by id: {}", id);
        return employeeRepository.findById(id).orElseThrow(EmployeeNotFoundException::new);
    }

    public Page<Employee> getPage(@NotNull Pageable pageable) {
        log.info("Attempting to get employee page of size {} and offset {}", pageable.getPageSize(), pageable.getOffset());
        return employeeRepository.findAll(pageable);
    }

    public Employee save(@NotNull EmployeeDto employeeDto) {
        log.info("Adding new employee to db");
        Employee employee = mapper.map(employeeDto, Employee.class);
        return employeeRepository.save(employee);
    }

    public Employee update(@NotNull String id, @NotNull EmployeeDto employeeDto) {
        log.info("Attempting to update employee with id: {}", id);
        Employee employee = employeeRepository.findById(id).map(oldEmployeeData -> {
            mapper.map(employeeDto, oldEmployeeData);
            return oldEmployeeData;
        }).orElseThrow(EmployeeNotFoundException::new);
        return employeeRepository.save(employee);
    }

    public void delete(@NotNull String id) {
        log.info("Deleting employee with id: {}", id);
        employeeRepository.deleteById(id);
    }

}
