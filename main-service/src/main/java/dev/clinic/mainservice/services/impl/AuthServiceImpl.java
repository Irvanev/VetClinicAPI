package dev.clinic.mainservice.services.impl;

import dev.clinic.mainservice.dtos.auth.*;
import dev.clinic.mainservice.mapping.UserMapping;
import dev.clinic.mainservice.models.entities.Client;
import dev.clinic.mainservice.models.entities.Role;
import dev.clinic.mainservice.models.entities.User;
import dev.clinic.mainservice.models.enums.RoleEnum;
import dev.clinic.mainservice.repositories.RoleRepository;
import dev.clinic.mainservice.repositories.UserRepository;
import dev.clinic.mainservice.security.JwtTokenProvider;
import dev.clinic.mainservice.services.AuthService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Value("${jwt.access-token-validity}")
    private int accessTokenValidityInMillis;

    @Value("${jwt.refresh-token-validity}")
    private int refreshTokenValidityInMillis;

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
        log.info("START register: email={}", signUpRequest.getEmail());
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            log.warn("Registration failed: user already exists, email={}", signUpRequest.getEmail());
            throw new IllegalArgumentException("User already exists");
        }
        if (!signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword())) {
            log.warn("Registration failed: passwords do not match for email={}", signUpRequest.getEmail());
            throw new IllegalArgumentException("Passwords do not match");
        }
        User client = UserMapping.registerClient(signUpRequest);
        client.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        client.setEnabled(false);

        String code = generateSixDigitCode();
        client.setVerificationCode(code);
        client.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        log.debug("Generated verification code for email={}: code={}, expiresAt={}",
                signUpRequest.getEmail(), code, client.getVerificationCodeExpiresAt());

        Role role = roleRepository.findByName(RoleEnum.User)
                .orElseThrow(() -> {
                    log.error("Default role not found during registration");
                    return new IllegalStateException("Default role not found");
                });
        client.setRole(role);
        userRepository.save(client);
        emailService.sendVerificationEmail(client.getEmail(), code);
        log.info("END register: user saved and verification email sent to={}", client.getEmail());
    }

    /**
     * Подтверждение почты.
     */
    @Override
    public void verify(VerifyRequest verifyRequest) {
        log.info("START verify: email={}, code={}", verifyRequest.getEmail(), verifyRequest.getVerificationCode());
        VerifyRequest dto = modelMapper.map(verifyRequest, VerifyRequest.class);

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> {
                    log.warn("Verification failed: user not found, email={}", dto.getEmail());
                    return new IllegalArgumentException("User not found");
                });
        if (user.isEnabled()) {
            log.warn("Verification skipped: user already verified, email={}", user.getEmail());
            throw new IllegalStateException("User already verified");
        }
        if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("Verification failed: code expired for email={}", user.getEmail());
            throw new IllegalStateException("Verification code expired");
        }
        if (!user.getVerificationCode().equals(dto.getVerificationCode())) {
            log.warn("Verification failed: invalid code for email={}", user.getEmail());
            throw new IllegalArgumentException("Invalid verification code");
        }
        user.setEnabled(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);
        userRepository.save(user);
        log.info("END verify: user enabled with email={}", user.getEmail());
    }

    /**
     * Аутентификация пользователя и генерация access и refresh токенов.
     */
    @Override
    public AuthResponse login(SignInRequest signInRequest) {
        log.info("START login: email={}", signInRequest.getEmail());
        String email = signInRequest.getEmail();
        String password = signInRequest.getPassword();

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);
        var authentication = authenticationManager.authenticate(authToken);

        var userDetails = (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Login failed: user not found, email={}", email);
                    return new IllegalArgumentException("User not found");
                });

        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        log.debug("User authenticated: email={}, role={}", email, role);

        String accessToken = tokenProvider.generateAccessToken(email, role);
        String refreshToken = tokenProvider.generateRefreshToken(email, role);
        log.info("END login: email={} issued tokens [accessExpiresIn={}ms, refreshExpiresIn={}ms]",
                email, accessTokenValidityInMillis, refreshTokenValidityInMillis);
        emailService.sendDeviceTokenAndUserId(user.getId(), signInRequest.getTokenDevice());
        return new AuthResponse(user.getId(), accessToken, refreshToken, accessTokenValidityInMillis, refreshTokenValidityInMillis);
    }

    /**
     * Обновление access токена по refresh токену.
     */
    @Override
    public AuthResponse refresh(RefreshRequest refreshRequest) {
        log.info("START refresh token");
        String providedRefreshToken = refreshRequest.getRefreshToken();

        if (!tokenProvider.validateToken(providedRefreshToken)) {
            log.warn("Refresh failed: invalid token");
            throw new IllegalArgumentException("Invalid refresh token");
        }
        if (!"refresh".equals(tokenProvider.getTokenType(providedRefreshToken))) {
            log.warn("Refresh failed: token is not a refresh token");
            throw new IllegalArgumentException("Provided token is not a refresh token");
        }

        String email = tokenProvider.getUsernameFromJWT(providedRefreshToken);
        String role = tokenProvider.getUserRoleFromJWT(providedRefreshToken);
        log.debug("Refreshing tokens for email={}, role={}", email, role);

        String newAccessToken = tokenProvider.generateAccessToken(email, role);
        String newRefreshToken = tokenProvider.generateRefreshToken(email, role);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Refresh failed: user not found, email={}", email);
                    return new IllegalArgumentException("User not found");
                });
        log.info("END refresh: email={} issued new tokens", email);

        return new AuthResponse(user.getId(), newAccessToken, newRefreshToken, accessTokenValidityInMillis, refreshTokenValidityInMillis);
    }
}
