package com.erms.back.controller;


import com.erms.back.Exception.NonAuthorizedException;
import com.erms.back.dto.EmployeeDto;
import com.erms.back.model.Employee;
import com.erms.back.model.enums.Role;
import com.erms.back.service.AuthenticatedDetailsService;
import com.erms.back.service.EmployeeService;
import com.erms.back.util.ErrorHandling.ApiError;
import com.erms.back.util.HasAuthorization;
import com.erms.back.util.PageWrapper;
import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/employees")
@Tag(name = "Employee Controller", description = "Handles all operations related to managing employees")
public class EmployeeController {

    private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);
    private final EmployeeService employeeService;
    private final HasAuthorization authorization;

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
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','HR')")
    public ResponseEntity<PageWrapper<Employee>> getPage(
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "15") Integer size,
            @Parameter(
                    in = ParameterIn.QUERY,
                    name = "filter",
                    description = "Filter query for more detail on syntax visit : https://github.com/turkraft/springfilter?tab=readme-ov-file",
                    schema = @Schema(type = "string"),
                    required = false,
                    example = "department in ['HR','IT'] and employmentStatus : 'ACTIVE'"
            ) @Nullable @Filter Specification<Employee> specification
    ) {
        if (specification != null)
            return ResponseEntity.ok(new PageWrapper<>(employeeService.getPage(PageRequest.of(page, size), specification)));
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
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','HR')")
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
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<Employee> save(@Valid @RequestBody EmployeeDto body) {
        authorization.canEditRole(body);
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
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','HR')")
    public ResponseEntity<Employee> update(@PathVariable(name = "id") String id, @RequestBody EmployeeDto body) {
        authorization.canEdit(body);
        authorization.canEditRole(body);
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
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<Employee> delete(@PathVariable(name = "id") String id) {

        authorization.canDelete(employeeService.getById(id));
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }




}
