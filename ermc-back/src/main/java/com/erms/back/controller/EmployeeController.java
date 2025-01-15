package com.erms.back.controller;


import com.erms.back.dto.EmployeeDto;
import com.erms.back.model.Employee;
import com.erms.back.service.EmployeeService;
import com.erms.back.util.PageWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping()
    public ResponseEntity<PageWrapper<Employee>> getPage(
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "15") Integer size
    ) {
        return ResponseEntity.ok(new PageWrapper<>(employeeService.getPage(PageRequest.of(page, size))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getById(@PathVariable(name = "id") String id) {
        return ResponseEntity.ok(employeeService.getById(id));
    }

    @PostMapping()
    public ResponseEntity<Employee> save(@RequestBody EmployeeDto body) {
        return new ResponseEntity<>(employeeService.save(body), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> update(@PathVariable(name = "id") String id, @RequestBody EmployeeDto body) {
        return new ResponseEntity<>(employeeService.update(id, body), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Employee> delete(@PathVariable(name = "id") String id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
