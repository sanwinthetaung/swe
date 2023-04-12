package com.sanwin.swe.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "employee")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class Employee {

    @Id
    @Column(name = "employee_id", unique = true)
    private String employeeId;

    @Column(name = "employee_login", unique = true)
    private String login;

    @Column(name = "employee_name")
    private String name;

    @Column(name = "employee_salary", precision = 5, scale = 2)
    private double salary;
}
