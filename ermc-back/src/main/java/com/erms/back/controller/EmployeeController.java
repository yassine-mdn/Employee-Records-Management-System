package com.erms.back.controller;


import com.erms.back.dto.EmployeeDto;
import com.erms.back.model.Employee;
import com.erms.back.service.EmployeeService;
import com.erms.back.util.ErrorHandling.ApiError;
import com.erms.back.util.PageWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
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

    @Operation(summary = "Return a page of Employees")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "found page of Employees"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid page parameters",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = "{ \"status\": \"BAD_REQUEST\", \"timestamp\": \"15-01-2025 06:12:30\", \"message\": \"Page number is greater than the total number of pages\", \"debugMessage\": null, \"subErrors\": null }")),

                    }
            )

    })
    @GetMapping()
    public ResponseEntity<PageWrapper<Employee>> getPage(
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "15") Integer size
    ) {
        return ResponseEntity.ok(new PageWrapper<>(employeeService.getPage(PageRequest.of(page, size))));
    }


    @Operation(summary = "Get a Employee by his Id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "found the corresponding employee"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "invalid employee id",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = "{   \"status\": \"NOT_FOUND\",   \"timestamp\": \"15-01-2025 06:28:33\",   \"message\": \"Employee Not Found\",   \"debugMessage\": null,   \"subErrors\": null }")),

                    }
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getById(@PathVariable(name = "id") String id) {
        return ResponseEntity.ok(employeeService.getById(id));
    }

    @Operation(summary = "Save new employee to db")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Employee created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Request body invalid",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = "{ \"status\": \"BAD_REQUEST\", \"timestamp\": \"15-01-2025 09:14:11\", \"message\": \"Validation failed for argument [0] in public org.springframework.http.ResponseEntity<com.erms.back.model.Employee> com.erms.back.controller.EmployeeController.save(com.erms.back.dto.EmployeeDto) with 2 errors: [Field error in object 'employeeDto' on field 'email': rejected value [string]; codes [Email.employeeDto.email,Email.email,Email.java.lang.String,Email]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [employeeDto.email,email]; arguments []; default message [email],[Ljakarta.validation.constraints.Pattern$Flag;@26d78daa,.*]; default message [must be a well-formed email address]] [Field error in object 'employeeDto' on field 'address': rejected value []; codes [NotBlank.employeeDto.address,NotBlank.address,NotBlank.java.lang.String,NotBlank]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [employeeDto.address,address]; arguments []; default message [address]]; default message [must not be blank]] \", \"debugMessage\": \"problemDetail.title.org.springframework.web.bind.MethodArgumentNotValidException\", \"subErrors\": [ { \"object\": \"employeeDto\", \"field\": \"email\", \"rejectedValue\": \"string\", \"message\": \"must be a well-formed email address\" }, { \"object\": \"employeeDto\", \"field\": \"address\", \"rejectedValue\": \"\", \"message\": \"must not be blank\" } ] }")),

                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Transaction Error",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = "{ \"status\": \"NOT_FOUND\", \"timestamp\": \"15-01-2025 09:13:05\", \"message\": \"Could not commit JPA transaction\", \"debugMessage\": \"TransactionSystemException\", \"subErrors\": null }")),

                    }

            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflicting Fields",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = "{ \"status\": \"NOT_FOUND\", \"timestamp\": \"15-01-2025 08:53:55\", \"message\": \"could not execute statement [ERROR: duplicate key value violates unique constraint 'employee_full_name_key' Detail: Key (full_name)=(string) already exists.]\", \"debugMessage\": \"DataIntegrityViolationException\", \"subErrors\": null }")),

                    }
            )
    })
    @PostMapping()
    public ResponseEntity<Employee> save(@Valid @RequestBody EmployeeDto body) {
        //TODO : check if admin to set Role else Role is NO_ROLE or throw exception not sure for now
        return new ResponseEntity<>(employeeService.save(body), HttpStatus.CREATED);
    }


    @Operation(summary = "Update Pre existing Employee")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Employee updated Successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Request body invalid",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = "{ \"status\": \"BAD_REQUEST\", \"timestamp\": \"15-01-2025 09:14:11\", \"message\": \"Validation failed for argument [0] in public org.springframework.http.ResponseEntity<com.erms.back.model.Employee> com.erms.back.controller.EmployeeController.save(com.erms.back.dto.EmployeeDto) with 2 errors: [Field error in object 'employeeDto' on field 'email': rejected value [string]; codes [Email.employeeDto.email,Email.email,Email.java.lang.String,Email]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [employeeDto.email,email]; arguments []; default message [email],[Ljakarta.validation.constraints.Pattern$Flag;@26d78daa,.*]; default message [must be a well-formed email address]] [Field error in object 'employeeDto' on field 'address': rejected value []; codes [NotBlank.employeeDto.address,NotBlank.address,NotBlank.java.lang.String,NotBlank]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [employeeDto.address,address]; arguments []; default message [address]]; default message [must not be blank]] \", \"debugMessage\": \"problemDetail.title.org.springframework.web.bind.MethodArgumentNotValidException\", \"subErrors\": [ { \"object\": \"employeeDto\", \"field\": \"email\", \"rejectedValue\": \"string\", \"message\": \"must be a well-formed email address\" }, { \"object\": \"employeeDto\", \"field\": \"address\", \"rejectedValue\": \"\", \"message\": \"must not be blank\" } ] }")),

                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "invalid employee id",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = "{   \"status\": \"NOT_FOUND\",   \"timestamp\": \"15-01-2025 06:28:33\",   \"message\": \"Employee Not Found\",   \"debugMessage\": null,   \"subErrors\": null }")),

                    }
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflicting Fields",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = "{ \"status\": \"NOT_FOUND\", \"timestamp\": \"15-01-2025 08:53:55\", \"message\": \"could not execute statement [ERROR: duplicate key value violates unique constraint \"employee_full_name_key\"  Detail: Key (full_name)=(string) already exists.]\", \"debugMessage\": \"DataIntegrityViolationException\", \"subErrors\": null }")),

                    }
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Employee> update(@PathVariable(name = "id") String id, @RequestBody EmployeeDto body) {
        return new ResponseEntity<>(employeeService.update(id, body), HttpStatus.OK);
    }

    @Operation(summary = "Delete a Employee by his Id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "deleted the corresponding employee"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "invalid employee id",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = "{   \"status\": \"NOT_FOUND\",   \"timestamp\": \"15-01-2025 06:28:33\",   \"message\": \"Employee Not Found\",   \"debugMessage\": null,   \"subErrors\": null }")),

                    }
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Employee> delete(@PathVariable(name = "id") String id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
