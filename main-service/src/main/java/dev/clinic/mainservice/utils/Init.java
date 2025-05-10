package dev.clinic.mainservice.utils;

import dev.clinic.mainservice.models.entities.*;
import dev.clinic.mainservice.models.enums.AppointmentStatus;
import dev.clinic.mainservice.models.enums.AppointmentType;
import dev.clinic.mainservice.models.enums.RoleEnum;
import dev.clinic.mainservice.repositories.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.Set;

@Component
public class Init implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClientRepository clientRepository;
    private final BranchesRepository branchesRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    public Init(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ClientRepository clientRepository,
            PetRepository petRepository,
            BranchesRepository branchesRepository,
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.clientRepository = clientRepository;
        this.petRepository = petRepository;
        this.branchesRepository = branchesRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        initRoles();
        initAdmin();
        initUser();
        initPets();
        initBraches();
        initDoctors();
        initAppointments();
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

    private void initBraches() {

        GeometryFactory geometryFactory = new GeometryFactory();
        double latitude = 55.7558;
        double longitude = 37.6176;

        Coordinate coordinate = new Coordinate(longitude, latitude);
        Point point = geometryFactory.createPoint(new CoordinateArraySequence(new Coordinate[]{coordinate}));

        Set<AppointmentType> services1 = Set.of(
                AppointmentType.CONSULTATION,
                AppointmentType.VACCINATION,
                AppointmentType.XRAY
        );

        Set<AppointmentType> services2 = Set.of(
                AppointmentType.SURGERY,
                AppointmentType.INSPECTION,
                AppointmentType.CHIPPING
        );

        if (branchesRepository.findByEmail("branch1@mail.ru").isEmpty()) {
            Branches branch1 = new Branches();
            branch1.setName("Территориальное ветеринарное управление № 5, Ленинская ветеринарная станция");
            branch1.setShortName("Территориальное ветеринарное управление № 5");
            branch1.setAddress("Гагаринский пер., 29, Москва");
            branch1.setPhone("79999999999");
            branch1.setEmail("branch1@mail.ru");
            branch1.setCoordinates(point);
            branch1.setServices(services1);
            branchesRepository.save(branch1);
        }

        if (branchesRepository.findByEmail("branch2@mail.ru").isEmpty()) {
            Branches branch2 = new Branches();
            branch2.setName("Территориальное ветеринарное управление № 75, Боровицкая ветеринарная станция");
            branch2.setShortName("Территориальное ветеринарное управление № 75");
            branch2.setAddress("Хрущевский пер., 16, Москва, 185747");
            branch2.setPhone("79999999999");
            branch2.setEmail("branch2@mail.ru");
            branch2.setCoordinates(point);
            branch2.setServices(services2);
            branchesRepository.save(branch2);
        }
    }

    private void initDoctors() {

        Branches branches1 = branchesRepository.findByEmail("branch1@mail.ru")
                .orElseThrow(() -> new IllegalStateException("Branch 1 not found"));

        Branches branches2 = branchesRepository.findByEmail("branch2@mail.ru")
                .orElseThrow(() -> new IllegalStateException("Branch 2 not found"));

        Role doctorRole = roleRepository.findByName(RoleEnum.Doctor)
                .orElseThrow(() -> new IllegalStateException("Role 'Doctor' not found"));

        if (doctorRepository.findByEmail("vitro003@mail.ru").isEmpty()) {
            Doctor doctor1 = new Doctor();
            doctor1.setEmail("vitro003@mail.ru");
            doctor1.setAddress("Кирпичная улица, 2Д, Москва");
            doctor1.setEnabled(true);
            doctor1.setEducation("МГУ");
            doctor1.setSpecialization("Хиурург");
            doctor1.setFirstName("Михаил");
            doctor1.setLastName("Михайлов");
            doctor1.setPatronymic("Михайлович");
            doctor1.setPassword(passwordEncoder.encode("123456789"));
            doctor1.setDateOfBirth(LocalDate.of(1977, 5, 5));
            doctor1.setNumberPhone("79999999999");
            doctor1.setBranch(branches1);
            doctor1.setRole(doctorRole);
            doctorRepository.save(doctor1);
        }

        if (doctorRepository.findByEmail("memas03@mail.ru").isEmpty()) {
            Doctor doctor2 = new Doctor();
            doctor2.setEmail("memas03@mail.ru");
            doctor2.setAddress("Кирпичная улица, 2Д, Москва");
            doctor2.setEnabled(true);
            doctor2.setEducation("МГУ");
            doctor2.setSpecialization("Хиурург");
            doctor2.setFirstName("Никита");
            doctor2.setLastName("Алексеев");
            doctor2.setPatronymic("Алексеевич");
            doctor2.setPassword(passwordEncoder.encode("123456789"));
            doctor2.setDateOfBirth(LocalDate.of(1980, 5, 5));
            doctor2.setNumberPhone("79999999999");
            doctor2.setBranch(branches2);
            doctor2.setRole(doctorRole);
            doctorRepository.save(doctor2);
        }
    }

    private void initAppointments() {
        Client client = clientRepository.findByEmail("vadim.lushina@gmail.com")
                .orElseThrow(() -> new IllegalStateException("Client not found: vadim.lushina@gmail.com"));

        Pet pet1 = petRepository.findByName("Барсик")
                .orElseThrow(() -> new IllegalStateException("Pet not found: Барсик"));
        Pet pet2 = petRepository.findByName("Рекс")
                .orElseThrow(() -> new IllegalStateException("Pet not found: Рекс"));

        Doctor doctor1 = doctorRepository.findByEmail("vitro003@mail.ru")
                .orElseThrow(() -> new IllegalStateException("Doctor not found: vitro003@mail.ru"));
        Doctor doctor2 = doctorRepository.findByEmail("memas03@mail.ru")
                .orElseThrow(() -> new IllegalStateException("Doctor not found: memas03@mail.ru"));

        if (appointmentRepository.count() == 0) {
            Appointment appointment1 = new Appointment();
            appointment1.setAppointmentType(AppointmentType.CONSULTATION);
            appointment1.setAppointmentDate(LocalDate.of(2025, 5, 10));
            appointment1.setAppointmentStartTime(LocalTime.of(11, 30));
            appointment1.setAppointmentEndTime(LocalTime.of(12, 0));
            appointment1.setStatus(AppointmentStatus.SCHEDULED);
            appointment1.setComments("Первый прием");
            appointment1.setDoctor(doctor1);
            appointment1.setPet(pet1);
            appointment1.setClient(client);
            appointmentRepository.save(appointment1);

            Appointment appointment2 = new Appointment();
            appointment2.setAppointmentType(AppointmentType.CHIPPING);
            appointment2.setAppointmentDate(LocalDate.of(2025, 5, 15));
            appointment2.setAppointmentStartTime(LocalTime.of(12, 30));
            appointment2.setAppointmentEndTime(LocalTime.of(13, 0));
            appointment2.setStatus(AppointmentStatus.COMPLETED);
            appointment2.setComments("Первый прием");
            appointment2.setDoctor(doctor2);
            appointment2.setPet(pet2);
            appointment2.setClient(client);
            appointmentRepository.save(appointment2);
        }
    }
}