package com.erms.back.repository;

import com.erms.back.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaSpecificationExecutor<Employee>,JpaRepository<Employee,String> {
}
