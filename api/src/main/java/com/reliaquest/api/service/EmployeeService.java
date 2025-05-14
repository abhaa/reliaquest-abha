package com.reliaquest.api.service;

import com.reliaquest.api.entity.CreateEmployeeRequest;
import com.reliaquest.api.entity.Employee;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface EmployeeService {

    ResponseEntity<List<Employee>> getAllEmployees();

    ResponseEntity<Employee> getEmployeeById(@PathVariable String id);

    ResponseEntity<Employee> createEmployee(@RequestBody CreateEmployeeRequest employeeInput);

    ResponseEntity<Boolean> deleteEmployeeByName(@PathVariable String name);
}
