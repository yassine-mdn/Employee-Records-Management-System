package com.erms.back.Exception;

public class EmployeeNotFoundException extends BaseAppException {
    public EmployeeNotFoundException() {
        super("Employee Not Found");
    }

    public EmployeeNotFoundException(String id) {
        super("Employee Not Found with id: " + id);
    }
}
