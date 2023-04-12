package com.sanwin.swe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanwin.swe.controller.EmployeeController;
import com.sanwin.swe.model.Employee;
import com.sanwin.swe.services.EmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService csvService;

    @Test
    public void testUploadFile() throws Exception {
        Resource file = new ClassPathResource("file/MOCK_DATA.csv");

        MockMultipartFile multipartFile = new MockMultipartFile(
                "file", file.getFilename(),
                "text/csv",
                file.getInputStream()
        );

        mockMvc.perform(multipart("/api/upload")
                .file(multipartFile))
            .andExpect(status().isOk());
    }

    @Test
    public void findAllEmployeesReturn200Ok() throws Exception {
        Employee employee1 = new Employee("e5466", "Ingrid", "Ferrierio", 5867.39);
        Employee employee2 = new Employee("e5467", "Zoro", "Zoro", 5867.39);

        Double minSalary = 0.0;
        Double maxSalary = 7000.0;
        int offset = 0;
        int limit = 30;

        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.ASC, "name"));
        Pageable pageable = PageRequest.of(offset, limit, Sort.by(orders));

        Page<Employee> pageResponse = new PageImpl<>(Arrays.asList(employee1, employee2));

        if (minSalary > 0.0 && maxSalary == 0.0) {
            Mockito.when(csvService.findBySalaryGreaterThanEqual(minSalary, pageable)).thenReturn(pageResponse);
        } else if (minSalary == 0.0 && maxSalary > 0.0) {
            Mockito.when(csvService.findBySalaryLessThanEqual(maxSalary, pageable)).thenReturn(pageResponse);
        } else if (minSalary > 0.0 && maxSalary > 0.0) {
            Mockito.when(csvService.findBySalaryBetween(minSalary, maxSalary, pageable)).thenReturn(pageResponse);
        } else {
            Mockito.when(csvService.getAllEmployees(pageable)).thenReturn(pageResponse);
        }

        mockMvc.perform(get("/api/users")
                .param("minSalary", "0.0")
                .param("maxSalary", "7000.0")
                .param("offset", "0")
                .param("limit", "30")
                .param("sort", "+name"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.[0].login").value("Ingrid"))
                .andExpect(jsonPath("$.data.[1].login").value("Zoro"))
                .andDo(print());
    }

    @Test
    public void findEmployeeByIdTest200Ok() throws Exception {
        Employee employee = new Employee("e5466", "Ingrid", "Ferrierio", 5867.39);

        Mockito.when(csvService.findById("e5466")).thenReturn(Optional.of(employee));

        mockMvc.perform(get("/api/users/{id}", "e5466")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(employee)))
                .andDo(print());
    }

    @Test
    public void findEmployeeByIdTest404NotFound() throws Exception {
        Mockito.when(csvService.findById("e5466")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/{id}", "e5466")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void createEmployeeTestReturn201Created() throws Exception {
        Employee employee = new Employee("e5466", "Ingrid", "Ferrierio", 5867.39);

        Mockito.when(csvService.save(Mockito.any(Employee.class))).thenReturn(employee);

        mockMvc.perform(post("/api/user")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(employee)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    @Test
    public void updateEmployeeTestReturn200Ok() throws Exception {
        Employee employee = new Employee("e5466", "Ingrid", "Ferrierio", 5867.39);

        Mockito.when(csvService.findById("e5466")).thenReturn(Optional.of(employee));
        Mockito.when(csvService.save(Mockito.any(Employee.class))).thenReturn(employee);

        mockMvc.perform(patch("/api/users/{id}", "e5466")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(employee)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.login").value("Ingrid"))
                .andDo(print());
    }

    @Test
    public void updateEmployeeTestReturn404NotFound() throws Exception {
        Employee employee = new Employee("e5466", "Ingrid", "Ferrierio", 5867.39);

        Mockito.when(csvService.findById("e5466")).thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/users/{id}", "e5466")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(employee)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

}
