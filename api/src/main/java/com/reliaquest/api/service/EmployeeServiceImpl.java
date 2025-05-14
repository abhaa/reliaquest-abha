package com.reliaquest.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.entity.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.reliaquest.api.utils.RetryUtility;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final RetryUtility retryUtility;

    public EmployeeServiceImpl(RetryUtility httpClientRetryUtil) {
        this.retryUtility = httpClientRetryUtil;
    }

    private final ObjectMapper mapper = new ObjectMapper();
    String url = "http://localhost:8112/api/v1/employee";

    @Override
    @Cacheable("employees")
    public ResponseEntity<List<Employee>> getAllEmployees() {

        HttpRequest request =
                HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        try {
            HttpResponse<String> response = retryUtility.sendRequestWithRetry(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ApiListReponse apiResponse = mapper.readValue(response.body(), ApiListReponse.class);
                return ResponseEntity.ok(apiResponse.getData());
            } else {
                return ResponseEntity.status(HttpStatus.valueOf(response.statusCode()))
                        .body(null);
            }
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Override
    @Cacheable(value = "employeeById", key = "#id")
    public ResponseEntity<Employee> getEmployeeById(String id) {
        HttpRequest request =
                HttpRequest.newBuilder().uri(URI.create(url + "/" + id)).GET().build();
        try {
            return getEmployeeResponseEntity(request);
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Override
    @CacheEvict(value = "employees", allEntries = true)
    public ResponseEntity<Employee> createEmployee(CreateEmployeeRequest employeeInput) {

        try {
            String json = mapper.writeValueAsString(employeeInput);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            return getEmployeeResponseEntity(request);
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @NotNull
    private ResponseEntity<Employee> getEmployeeResponseEntity(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = retryUtility.sendRequestWithRetry(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == HttpStatus.OK.value()) {
            ApiResponse apiResponse = mapper.readValue(response.body(), ApiResponse.class);
            return ResponseEntity.ok(apiResponse.getData());
        } else {
            return ResponseEntity.status(HttpStatus.valueOf(response.statusCode()))
                    .body(null);
        }
    }

    @Override
    @CacheEvict(value = "employees", allEntries = true)
    public ResponseEntity<Boolean> deleteEmployeeByName(String name) {
        DeleteEmployeeApiRequestInput input = new DeleteEmployeeApiRequestInput(name);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(input);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .method("DELETE", HttpRequest.BodyPublishers.ofString(requestBody))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = retryUtility.sendRequestWithRetry(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpStatus.OK.value()) {
                return new ResponseEntity<>(Boolean.TRUE, HttpStatus.valueOf(response.statusCode()));
            } else {
                return new ResponseEntity<>(Boolean.FALSE, HttpStatus.valueOf(response.statusCode()));
            }
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
