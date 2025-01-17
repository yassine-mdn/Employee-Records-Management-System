package com.erms.back.controller;


import com.erms.back.dto.EmployeeDto;
import com.erms.back.model.Employee;
import com.erms.back.service.EmployeeService;
import com.erms.back.util.ErrorHandling.ApiError;
import com.erms.back.util.HasAuthorization;
import com.erms.back.util.OpenApi.UserRoleDescription;
import com.erms.back.util.PageWrapper;
import com.erms.back.util.reporting.PdfGenerator;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/employees")
@Tag(name = "Employee Controller", description = "Handles all operations related to managing employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final HasAuthorization authorization;
    private final PdfGenerator pdfGenerator;

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
    @UserRoleDescription
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
                    example = "department in ['HR','IT'] and employmentStatus : 'ACTIVE'"
            ) @RequestParam(required = false) String filter
    ) {
        if (authorization.isManager())
            return ResponseEntity.ok(new PageWrapper<>(employeeService.gePageFromDepartment(
                    authorization.getEmployee().getDepartment(),
                    PageRequest.of(page, size),
                    filter
            )));
        if (filter != null) {
            return ResponseEntity.ok(new PageWrapper<>(employeeService.getPage(PageRequest.of(page, size), filter)));
        }
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
    @UserRoleDescription
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','HR')")
    public ResponseEntity<Employee> getById(@PathVariable(name = "id") String id) {
        return ResponseEntity.ok(employeeService.getById(id));
    }


    @Operation(summary = "return a pdf report of all the employees that joined in the past month")
    @UserRoleDescription
    @GetMapping("/report")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> test() throws IOException {

        String localDateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMMM-yyyy"));
        String filename = "report-"+localDateString+".pdf";

        ByteArrayOutputStream pdfStream = pdfGenerator.generatePdfOutputStream();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+filename);
        headers.setContentLength(pdfStream.size());
        return new ResponseEntity<>(pdfStream.toByteArray(), headers, HttpStatus.OK);
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
    @UserRoleDescription
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
    @UserRoleDescription
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','HR')")
    public ResponseEntity<Employee> update(@PathVariable(name = "id") String id, @RequestBody EmployeeDto body) {
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
    @UserRoleDescription
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<Employee> delete(@PathVariable(name = "id") String id) {

        authorization.canDelete(employeeService.getById(id));
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }




}
