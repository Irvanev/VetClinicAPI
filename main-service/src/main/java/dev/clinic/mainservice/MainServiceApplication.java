package dev.clinic.mainservice;

import dev.clinic.mainservice.models.entities.Role;
import dev.clinic.mainservice.models.enums.RoleEnum;
import dev.clinic.mainservice.repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MainServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainServiceApplication.class, args);
    }
}
