package com.reliaquest.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEmployeeRequest {

    @JsonProperty("name")
    private String employee_name; // Employee's full name

    @JsonProperty("salary")
    private int employee_salary; // Employee's salary

    @JsonProperty("age")
    private int employee_age; // Employee's age

    @JsonProperty("title")
    private String employee_title; // Job title of the employee
}
