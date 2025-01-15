package com.erms.back.util.ErrorHandling;

import com.erms.back.Exception.BaseAppException;
import com.erms.back.Exception.EmployeeNotFoundException;
import com.erms.back.Exception.PageOutOfBoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllException(Exception ex, WebRequest request) {
        ApiError apiError = new ApiError(NOT_FOUND);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(BaseAppException.class)
    public ResponseEntity<Object> handleBaseException(BaseAppException baseAppException, HttpStatus status, WebRequest request, HttpHeaders headers ) {
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, baseAppException.getMessage(), baseAppException));
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(
            EmployeeNotFoundException ex) {
        ApiError apiError = new ApiError(NOT_FOUND);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(PageOutOfBoundException.class)
    protected ResponseEntity<Object> handlePageOutOfBoundException(
            PageOutOfBoundException ex
    ){
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

}
