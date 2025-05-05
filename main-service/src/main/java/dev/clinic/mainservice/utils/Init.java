package dev.clinic.mainservice.utils;

import dev.clinic.mainservice.models.entities.Role;
import dev.clinic.mainservice.models.entities.User;
import dev.clinic.mainservice.models.enums.RoleEnum;
import dev.clinic.mainservice.repositories.RoleRepository;
import dev.clinic.mainservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Init implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public Init(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        initRoles();
    }

    private void initRoles() {
        if (roleRepository.count() == 0) {
            roleRepository.findByName(RoleEnum.User).orElseGet(() -> {
                Role userRole = new Role();
                userRole.setName(RoleEnum.User);
                return roleRepository.save(userRole);
            });

            roleRepository.findByName(RoleEnum.Doctor).orElseGet(() -> {
                Role adminRole = new Role();
                adminRole.setName(RoleEnum.Doctor);
                return roleRepository.save(adminRole);
            });

            roleRepository.findByName(RoleEnum.Admin).orElseGet(() -> {
                Role adminRole = new Role();
                adminRole.setName(RoleEnum.Admin);
                return roleRepository.save(adminRole);
            });
        }
    }
}