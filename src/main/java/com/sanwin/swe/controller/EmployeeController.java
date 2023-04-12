package com.sanwin.swe.controller;

import com.sanwin.swe.dto.EmployeeDTO;
import com.sanwin.swe.model.Employee;
import com.sanwin.swe.services.EmployeeService;
import com.sanwin.swe.utils.CSVHelper;
import com.sanwin.swe.utils.ResponseMessage;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin("http://localhost:8081")
@RestController
@RequestMapping("/api/")
@AllArgsConstructor
public class EmployeeController {

    private EmployeeService csvService;

    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";

        if (CSVHelper.hasCSVFormat(file)) {
            try {
                csvService.save(file);

                message = "Uploaded the file successfully: " + file.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
            } catch (Exception e) {
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
            }
        }

        message = "Please upload a csv file!";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllEmployees(
            @RequestParam(required = false, defaultValue = "0.0") Double minSalary,
            @RequestParam(required = false, defaultValue = "0.0") Double maxSalary,
            @RequestParam int offset,
            @RequestParam int limit,
            @RequestParam String sort) {

        try {

            String direction = sort.substring(0, 1).equalsIgnoreCase("+") ? "asc" : "desc";
            String field = sort.substring(1);

            List<Sort.Order> orders = new ArrayList<>();
            orders.add(new Sort.Order(getSortDirection(direction), field));
            List<Employee> employees;
            Pageable pagingSort = PageRequest.of(offset, limit, Sort.by(orders));

            Page<Employee> pageTuts;
            if (minSalary > 0.0 && maxSalary == 0.0) {
                pageTuts = csvService.findBySalaryGreaterThanEqual(minSalary, pagingSort);
            } else if (minSalary == 0.0 && maxSalary > 0.0) {
                pageTuts = csvService.findBySalaryLessThanEqual(maxSalary, pagingSort);
            } else if (minSalary > 0.0 && maxSalary > 0.0) {
                pageTuts = csvService.findBySalaryBetween(minSalary, maxSalary, pagingSort);
            } else {
                pageTuts = csvService.getAllEmployees(pagingSort);
            }

            if (pageTuts == null || pageTuts.getContent().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            employees = pageTuts.getContent();

            Map<String, Object> response = new HashMap<>();
            response.put("data", employees);
            response.put("currentPage", pageTuts.getNumber());
            response.put("totalItems", pageTuts.getTotalElements());
            response.put("totalPages", pageTuts.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable("id") String id) {
        Optional<Employee> employee = csvService.findById(id);

        return employee.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @PostMapping("/user")
    public ResponseEntity<Object> createEmployee(@Valid @RequestBody EmployeeDTO dto) {
        try {

            Optional<Employee> old = csvService.findById(dto.getEmployeeId());
            if (old.isPresent()) {
                return new ResponseEntity<>("Employee already existed. Please Update.", HttpStatus.FOUND);
            }

            Employee employee = csvService.save(new Employee(
                    dto.getEmployeeId(),
                    dto.getLogin(),
                    dto.getName(),
                    dto.getSalary()
            ));
            return new ResponseEntity<>(employee, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable("id") String employeeId, @Valid @RequestBody EmployeeDTO body) {
        Optional<Employee> old = csvService.findById(employeeId);

        if (old.isPresent()) {
            Employee employee = old.get();
            employee.setLogin(body.getLogin());
            employee.setName(body.getName());
            employee.setSalary(body.getSalary());

            return new ResponseEntity<>(csvService.save(employee), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<HttpStatus> deleteEmployee(@PathVariable("id") String employeeId) {
        try {
            csvService.deleteById(employeeId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }

        return Sort.Direction.ASC;
    }


}
