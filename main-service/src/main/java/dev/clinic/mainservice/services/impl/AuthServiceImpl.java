package dev.clinic.mainservice.services.impl;

import dev.clinic.mainservice.dtos.auth.*;
import dev.clinic.mainservice.models.entities.Client;
import dev.clinic.mainservice.models.entities.Role;
import dev.clinic.mainservice.models.entities.User;
import dev.clinic.mainservice.models.enums.RoleEnum;
import dev.clinic.mainservice.repositories.RoleRepository;
import dev.clinic.mainservice.repositories.UserRepository;
import dev.clinic.mainservice.security.JwtTokenProvider;
import dev.clinic.mainservice.services.AuthService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailMessageProducer emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final ModelMapper modelMapper;

    @Autowired
    public AuthServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            EmailMessageProducer emailService,
            AuthenticationManager authenticationManager,
            JwtTokenProvider tokenProvider,
            ModelMapper modelMapper
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.modelMapper = modelMapper;
    }

    /**
     * Метод для генерации кода.
     */
    private static final SecureRandom secureRandom = new SecureRandom();
    private String generateSixDigitCode() {
        int number = secureRandom.nextInt(1_000_000);
        return String.format("%06d", number);
    }


    /**
     * Регистрация нового пользователя с отправкой кода на почту.
     */
    @Override
    public void register(SignUpRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new IllegalArgumentException("User already exists");
        }
        if (!signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        Client client = modelMapper.map(signUpRequest, Client.class);
        client.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        client.setEnabled(false);

        String code = generateSixDigitCode();
        client.setVerificationCode(code);
        client.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));

        Role role = roleRepository.findByName(RoleEnum.User)
                .orElseThrow(() -> new IllegalStateException("Default role not found"));
        client.setRole(role);

        userRepository.save(client);
        emailService.sendVerificationEmail(client.getEmail(), code);
    }

    /**
     * Подтверждение почты.
     */
    @Override
    public void verify(VerifyRequest verifyRequest) {
        VerifyRequest dto = modelMapper.map(verifyRequest, VerifyRequest.class);

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user.isEnabled()) {
            throw new IllegalStateException("User already verified");
        }
        if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Verification code expired");
        }
        if (!user.getVerificationCode().equals(dto.getVerificationCode())) {
            throw new IllegalArgumentException("Invalid verification code");
        }
        user.setEnabled(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);
        userRepository.save(user);
    }

    /**
     * Аутентификация пользователя и генерация access и refresh токенов.
     */
    @Override
    public AuthResponse login(SignInRequest signInRequest) {
        String email = signInRequest.getEmail();
        String password = signInRequest.getPassword();

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);
        var authentication = authenticationManager.authenticate(authToken);

        var userDetails = (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();

        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        String accessToken = tokenProvider.generateAccessToken(email, role);
        String refreshToken = tokenProvider.generateRefreshToken(email, role);

        return new AuthResponse(accessToken, refreshToken);
    }

    /**
     * Обновление access токена по refresh токену.
     */
    @Override
    public AuthResponse refresh(RefreshRequest refreshRequest) {
        String providedRefreshToken = refreshRequest.getRefreshToken();

        if (!tokenProvider.validateToken(providedRefreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        if (!"refresh".equals(tokenProvider.getTokenType(providedRefreshToken))) {
            throw new IllegalArgumentException("Provided token is not a refresh token");
        }

        String email = tokenProvider.getUsernameFromJWT(providedRefreshToken);
        String role = tokenProvider.getUserRoleFromJWT(providedRefreshToken);

        String newAccessToken = tokenProvider.generateAccessToken(email, role);
        String newRefreshToken = tokenProvider.generateRefreshToken(email, role);

        return new AuthResponse(newAccessToken, newRefreshToken);
    }
}
