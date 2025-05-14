package com.reliaquest.api.controller;

import com.reliaquest.api.entity.CreateEmployeeRequest;
import com.reliaquest.api.entity.Employee;
import com.reliaquest.api.service.EmployeeService;
import java.util.Comparator;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/employees")
@Tag(name = "Employees", description = "ReliaQuest Employee Management API")
public class EmployeeController implements IEmployeeController<Employee, CreateEmployeeRequest> {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    @Operation(summary = "Get all employees", description = "Returns a list of all employees")
    @ApiResponse(responseCode = "200", description = "List of employees retrieved successfully")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @Override
    @Operation(summary = "Returns employee with the given ID", description = "Returns an employee with the given ID")
    @ApiResponse(responseCode = "200", description = "Employee with the given ID")
    public ResponseEntity<Employee> getEmployeeById(String id) {
        return employeeService.getEmployeeById(id);
    }

    @Override
    @Operation(summary = "Returns all employees having or containing the given name", description = "Returns a list of all employees whose name contains or matches the string input provided")
    @ApiResponse(responseCode = "200", description = "List of employees matching or containing the given name retrieved successfully")
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        ResponseEntity<List<Employee>> response = employeeService.getAllEmployees();

        if (response.getStatusCode() == HttpStatus.OK) {
            List<Employee> employees = response.getBody();
            if (employees == null) {
                logEmployeesNotFound();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            List<Employee> byName = employees.stream()
                    .filter(it -> it.getName().contains(searchString))
                    .toList();
            if (byName.isEmpty()) {
                logEmployeeNotFound(searchString);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            logSuccess();
            return ResponseEntity.ok(byName);
        } else {
            logWithStatusCode(response.getStatusCode());
            return ResponseEntity.status(response.getStatusCode()).body(null);
        }
    }

    @Override
    @Operation(summary = "Returns highest salary amount", description = "Salary Value of the highest salary")
    @ApiResponse(responseCode = "200", description = "Highest Salary Amount")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        ResponseEntity<List<Employee>> response = employeeService.getAllEmployees();

        if (response.getStatusCode() == HttpStatus.OK) {
            List<Employee> employees = response.getBody();

            if (employees == null) {
                logEmployeesNotFound();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0);
            }
            Integer max = (int)
                    employees.stream().mapToDouble(Employee::getSalary).max().orElse(0);

            logSuccess();
            return ResponseEntity.ok(max);
        } else {
            logWithStatusCode(response.getStatusCode());
            return ResponseEntity.status(response.getStatusCode()).body(null);
        }
    }

    @Override
    @Operation(summary = "Returns names of top 10 employees with the highest salary", description = "Returns names of top 10 employees with the highest salary")
    @ApiResponse(responseCode = "200", description = "Returns names of top 10 employees with the highest salary")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        ResponseEntity<List<Employee>> response = employeeService.getAllEmployees();

        if (response.getStatusCode() == HttpStatus.OK) {
            List<Employee> employees = response.getBody();
            if (employees == null) {
                logEmployeesNotFound();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            List<String> topTenNames = employees.stream()
                    .sorted(Comparator.comparingDouble(Employee::getSalary).reversed())
                    .limit(10)
                    .map(Employee::getName)
                    .toList();

            if (topTenNames.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            logSuccess();
            return ResponseEntity.ok(topTenNames);
        } else {
            logWithStatusCode(response.getStatusCode());
            return ResponseEntity.status(response.getStatusCode()).body(null);
        }
    }

    @Override
    @Operation(summary = "Create a new employee", description = "Adds a new employee to the system")
    @ApiResponse(responseCode = "200", description = "Creates a new Employee")
    @RequestBody(description = "Details of employee to be created with json keys - name, salary, age, title")
    public ResponseEntity<Employee> createEmployee(CreateEmployeeRequest employeeInput) {
        CreateEmployeeRequest employeeRequest = new CreateEmployeeRequest(
                employeeInput.getEmployee_name(),
                employeeInput.getEmployee_salary(),
                employeeInput.getEmployee_age(),
                employeeInput.getEmployee_title());
        return employeeService.createEmployee(employeeRequest);
    }

    @Override
    @Operation(summary = "Deletes employee with given employee_id", description = "Deletes employee with given employee_id")
    @ApiResponse(responseCode = "200", description = "Deletes employee with the given employee id")
    public ResponseEntity<String> deleteEmployeeById(String id) {
        ResponseEntity<Employee> response = employeeService.getEmployeeById(id);
        if (response.getStatusCode() == HttpStatus.OK) {
            Employee employee = response.getBody();
            if (employee == null) {
                logEmployeesNotFound();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            ResponseEntity<Boolean> responseEntity = employeeService.deleteEmployeeByName(employee.getName());

            if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() == Boolean.TRUE) {
                logSuccess();
                return ResponseEntity.ok(employee.getName());
            } else {
                logWithStatusCode(responseEntity.getStatusCode());
                return ResponseEntity.status(responseEntity.getStatusCode()).body(null);
            }
        } else {
            logWithStatusCode(response.getStatusCode());
            return ResponseEntity.status(response.getStatusCode()).body(null);
        }
    }

    private void logEmployeesNotFound() {
        log.error("Employees not found");
    }

    private void logEmployeeNotFound(String input) {
        log.error("Employee not found with name : {}", input);
    }

    private void logSuccess() {
        log.info("Success");
    }

    private void logWithStatusCode(HttpStatusCode statusCode) {
        log.error("Error processing your request, failed with status code {} ", statusCode);
    }
}
