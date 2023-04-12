package com.sanwin.swe.services;

import com.sanwin.swe.model.Employee;
import com.sanwin.swe.repository.EmployeeRepository;
import com.sanwin.swe.utils.CSVHelper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EmployeeService {

    private EmployeeRepository employeeRepository;

    public void save(MultipartFile file) {
        try {
            List<Employee> employees = CSVHelper.csvToEmployees(file.getInputStream());

            for (Employee employee : employees) {
                if (employee.getSalary() < 0) {
                    throw new RuntimeException("fail to store csv data: Salary must be >= 0.0");
                }

                Optional<Employee> old = employeeRepository.findByEmployeeId(employee.getEmployeeId());
                if (old.isPresent()) {
                    Employee oldEmployee = old.get();
                    oldEmployee.setLogin(employee.getLogin());
                    oldEmployee.setName(employee.getName());
                    oldEmployee.setSalary(employee.getSalary());
                    employeeRepository.save(oldEmployee);
                } else {
                    employeeRepository.save(employee);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("fail to store csv data: " + e.getMessage());
        }
    }


    public Page<Employee> getAllEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable);
    }

    public  Page<Employee> findBySalaryBetween(Double minSalary, Double maxSalary, Pageable pageable) {
        return employeeRepository.findBySalaryBetween(minSalary, maxSalary, pageable);
    }

    public  Page<Employee> findBySalaryGreaterThanEqual(Double salary, Pageable pageable) {
        return employeeRepository.findBySalaryGreaterThanEqual(salary, pageable);
    }

    public  Page<Employee> findBySalaryLessThanEqual(Double salary, Pageable pageable) {
        return employeeRepository.findBySalaryLessThanEqual(salary, pageable);
    }

    public Optional<Employee> findById(String employeeId) {
        return employeeRepository.findById(employeeId);
    }

    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    public void deleteById(String employeeId) {
        employeeRepository.deleteById(employeeId);
    }
}
