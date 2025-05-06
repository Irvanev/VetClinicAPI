package dev.clinic.mainservice.utils;

import dev.clinic.mainservice.models.entities.*;
import dev.clinic.mainservice.models.enums.RoleEnum;
import dev.clinic.mainservice.repositories.ClientRepository;
import dev.clinic.mainservice.repositories.PetRepository;
import dev.clinic.mainservice.repositories.RoleRepository;
import dev.clinic.mainservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;

@Component
public class Init implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClientRepository clientRepository;

    public Init(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ClientRepository clientRepository,
            PetRepository petRepository
    ) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.clientRepository = clientRepository;
        this.petRepository = petRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        initRoles();
        initAdmin();
        initUser();
        initPets();
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

    private void initAdmin() {
        if (userRepository.existsByEmail("irvvanevv@mail.ru")) {
            return;
        }
        Role adminRole = roleRepository.findByName(RoleEnum.Admin)
                .orElseThrow(() -> new IllegalStateException("Role 'Admin' not found"));
        Admin admin = new Admin();
        admin.setEmail("irvvanevv@mail.ru");
        admin.setFirstName("Главный");
        admin.setLastName("Администратор");
        admin.setEnabled(true);
        admin.setPassword(passwordEncoder.encode("CFlW7v7DOT2s"));
        admin.setRole(adminRole);

        userRepository.save(admin);
    }

    private void initUser() {
        if (userRepository.existsByEmail("vadim.lushina@gmail.com")) {
            return;
        }

        Role userRole = roleRepository.findByName(RoleEnum.User)
                .orElseThrow(() -> new IllegalStateException("Role 'User' not found"));

        Client client = new Client();
        client.setFirstName("Вадим");
        client.setLastName("Лушин");
        client.setEmail("vadim.lushina@gmail.com");

        String encodedPassword = passwordEncoder.encode("123456789");
        client.setPassword(encodedPassword);

        client.setEnabled(true);
        client.setRole(userRole);

        LocalDate date = LocalDate.of(2003, 10, 10);
        client.setDateOfBirth(date);

        client.setNumberPhone("79999999999");

        userRepository.save(client);
    }

    private void initPets() {
        String email = "vadim.lushina@gmail.com";
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Client not found: " + email));

        if (petRepository.findByName("Барсик").isEmpty()) {
            Pet pet1 = new Pet();

            pet1.setName("Барсик");
            pet1.setAnimalType("Кот/Кошка");
            pet1.setBreed("Британская вислоухая");
            LocalDate birth1 = LocalDate.of(2020, 10, 10);
            pet1.setBirthDate(birth1);
            pet1.setOwner(client);
            petRepository.save(pet1);
        }
        if (petRepository.findByName("Рекс").isEmpty()) {
            Pet pet2 = new Pet();

            pet2.setName("Рекс");
            pet2.setAnimalType("Собака");
            pet2.setBreed("Немецкая овчарка");
            LocalDate birth2 = LocalDate.of(2018, 5, 5);
            pet2.setBirthDate(birth2);
            pet2.setOwner(client);
            petRepository.save(pet2);
        }
    }
}