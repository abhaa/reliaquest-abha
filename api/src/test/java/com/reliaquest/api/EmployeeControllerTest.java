package com.reliaquest.api;

import com.reliaquest.api.controller.EmployeeController;
import com.reliaquest.api.entity.CreateEmployeeRequest;
import com.reliaquest.api.entity.Employee;
import com.reliaquest.api.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private List<Employee> mockEmployees;

    @BeforeEach
    void setUp() {
        mockEmployees = List.of(
                new Employee("1", "Alice", 50000, 30, "Engineer", "alice@xyz.com-fake"),
                new Employee("2", "Bob", 70000, 35, "Manager", "bob@xyz.com-fake"),
                new Employee("3", "Charlie", 40000, 28, "Analyst", "charlie@xyz.com-fake")
        );
    }

    @Test
    void testGetAllEmployees_Success() {
        when(employeeService.getAllEmployees()).thenReturn(ResponseEntity.ok(mockEmployees));

        ResponseEntity<List<Employee>> response = employeeController.getAllEmployees();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    void testGetEmployeeById_Found() {
        Employee mock = mockEmployees.get(0);
        when(employeeService.getEmployeeById("1")).thenReturn(ResponseEntity.ok(mock));

        ResponseEntity<Employee> response = employeeController.getEmployeeById("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Alice", Objects.requireNonNull(response.getBody()).getName());
    }

    @Test
    void testGetEmployeesByNameSearch_Found() {
        when(employeeService.getAllEmployees()).thenReturn(ResponseEntity.ok(mockEmployees));

        ResponseEntity<List<Employee>> response = employeeController.getEmployeesByNameSearch("li");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size()); // Alice, Charlie
    }

    @Test
    void testGetEmployeesByNameSearch_NotFound() {
        when(employeeService.getAllEmployees()).thenReturn(ResponseEntity.ok(mockEmployees));

        ResponseEntity<List<Employee>> response = employeeController.getEmployeesByNameSearch("ZZZ");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetHighestSalaryOfEmployees() {
        when(employeeService.getAllEmployees()).thenReturn(ResponseEntity.ok(mockEmployees));

        ResponseEntity<Integer> response = employeeController.getHighestSalaryOfEmployees();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(70000, response.getBody());
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNames() {
        when(employeeService.getAllEmployees()).thenReturn(ResponseEntity.ok(mockEmployees));

        ResponseEntity<List<String>> response = employeeController.getTopTenHighestEarningEmployeeNames();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of("Bob", "Alice", "Charlie"), response.getBody());
    }

    @Test
    void testCreateEmployee() {
        CreateEmployeeRequest request = new CreateEmployeeRequest("David", 55000, 32, "Developer");
        Employee created = new Employee("4", "David", 55000, 32, "Developer", "david@xyz.com-fake");

        when(employeeService.createEmployee(any())).thenReturn(ResponseEntity.ok(created));

        ResponseEntity<Employee> response = employeeController.createEmployee(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("David", Objects.requireNonNull(response.getBody()).getName());
    }

    @Test
    void testDeleteEmployeeById_Success() {
        Employee employee = mockEmployees.get(1);

        when(employeeService.getEmployeeById("2")).thenReturn(ResponseEntity.ok(employee));
        when(employeeService.deleteEmployeeByName(employee.getName()))
                .thenReturn(ResponseEntity.ok(true));

        ResponseEntity<String> response = employeeController.deleteEmployeeById("2");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Bob", response.getBody());
    }

    @Test
    void testDeleteEmployeeById_EmployeeNotFound() {
        when(employeeService.getEmployeeById("99")).thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));

        ResponseEntity<String> response = employeeController.deleteEmployeeById("99");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
