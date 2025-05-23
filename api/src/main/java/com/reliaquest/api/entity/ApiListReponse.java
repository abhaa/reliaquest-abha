package com.reliaquest.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiListReponse {

    private List<Employee> data;

    @JsonProperty("status")
    private String status;
}
