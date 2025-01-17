package com.erms.back.service;


import com.erms.back.Exception.EmployeeNotFoundException;
import com.erms.back.Exception.PageOutOfBoundException;
import com.erms.back.dto.EmployeeDto;
import com.erms.back.model.Employee;
import com.erms.back.model.enums.Department;
import com.erms.back.repository.EmployeeRepository;
import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ModelMapper mapper;
    private final FilterParser filterParser;
    private final FilterSpecificationConverter filterSpecificationConverter;


    public Employee getById(@NotNull String id) {
        log.info("Attempting to get employee by id: {}", id);
        return employeeRepository.findById(id).orElseThrow(EmployeeNotFoundException::new);
    }

    public Page<Employee> getPage(@NotNull Pageable pageable) {
        log.info("Attempting to get employee page of size {} and offset {}", pageable.getPageSize(), pageable.getOffset());
        Page<Employee> result = employeeRepository.findAll(pageable);

        if (pageable.getPageNumber() > result.getTotalPages())
            throw new PageOutOfBoundException("Page number is greater than the total number of pages");
        return result;
    }

    public Page<Employee> getPage(@NotNull Pageable pageable, @NotNull String filter) {
        FilterNode node = filterParser.parse(filter);
        FilterSpecification<Employee> specification = filterSpecificationConverter.convert(node);
        return employeeRepository.findAll(specification, pageable);
    }

    public Page<Employee> gePageFromDepartment(@NotNull Department department, @NotNull Pageable pageable, String filter) {
        String fullFilter = "department : " + "'" + department + "'";
        if (filter != null && !filter.trim().isEmpty()) {
            fullFilter = fullFilter + " and " + filter;
        }
        FilterNode node = filterParser.parse(fullFilter);
        FilterSpecification<Employee> specification = filterSpecificationConverter.convert(node);
        return employeeRepository.findAll(specification, pageable);
    }

    public List<Employee> getLastMonthsEmployees() {
        LocalDate lastMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        return employeeRepository.findAllByHireDateAfter(lastMonth);
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
        if (!employeeRepository.existsById(id))
            throw new EmployeeNotFoundException(id);
        employeeRepository.deleteById(id);
    }

}
