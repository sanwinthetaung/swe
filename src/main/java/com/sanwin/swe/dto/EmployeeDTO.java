package com.sanwin.swe.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class EmployeeDTO {

    @NotBlank(message = "Employee ID is required.")
    private String employeeId;

    @NotBlank(message = "Login is required.")
    private String login;

    @NotBlank(message = "Name is required.")
    private String name;

    @NotNull(message = "Salary is required.")
    @Positive(message = "Salary must be positive number.")
    private double salary;
}
