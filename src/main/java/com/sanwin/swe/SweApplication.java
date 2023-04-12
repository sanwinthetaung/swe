package com.sanwin.swe;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Employees", version = "1.0", description = "Employees Information"))
public class SweApplication {

    public static void main(String[] args) {
        SpringApplication.run(SweApplication.class, args);
    }

}
