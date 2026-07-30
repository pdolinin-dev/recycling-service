package com.example.recycling_service.service;

import com.example.recycling_service.dto.Request.LoginRequest;
import com.example.recycling_service.dto.Request.RegisterRequest;
import com.example.recycling_service.dto.Response.JwtResponse;
import com.example.recycling_service.exception.ConflictException;
import com.example.recycling_service.model.Enum.Role;
import com.example.recycling_service.model.User;
import com.example.recycling_service.repository.UserRepository;
import com.example.recycling_service.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    AuthenticationManager authenticationManager;

    @InjectMocks
    AuthService authService;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setLogin("test_login_123");
        loginRequest.setPassword("test_password_123");

        registerRequest = new RegisterRequest();
        registerRequest.setLogin("test_login_123");
        registerRequest.setPassword("test_password_123");
        registerRequest.setEmail("test_email_123");
        registerRequest.setName("test_name_123");
    }

    @Test
    @DisplayName("Авторизация прошла успешно: вернулся логин, роль, jwt-токен")
    void login_success() {
        // Объект Authentication — то, что Spring Security вернёт после проверки логина/пароля.
        // Мы его создаём руками, потому что реальной базы данных нет.
        Authentication fakeAuthentication = new UsernamePasswordAuthenticationToken(
                "test_login_123",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        // Говорим Mockito: когда authenticationManager.authenticate() вызовут с ЛЮБЫМ аргументом,
        // вместо реальной проверки верни наш fakeAuthentication.
        when(authenticationManager.authenticate(any())).thenReturn(fakeAuthentication);

        // Говорим Mockito: когда jwtTokenProvider.generateToken() вызовут с этим логином,
        // верни фейковый токен (нам не нужен реальный JWT, нас интересует лишь то, что он попадёт в ответ).
        when(jwtTokenProvider.generateToken("test_login_123")).thenReturn("fake.jwt.token");

        // --- Act (действие) ---
        JwtResponse response = authService.login(loginRequest);

        // --- Assert (проверка) ---
        // Проверяем каждое поле ответа — именно то, что сервис должен вернуть при успешном логине.
        assertThat(response.getLogin()).isEqualTo("test_login_123");
        assertThat(response.getRole()).isEqualTo("ROLE_USER");
        assertThat(response.getJwt()).isEqualTo("fake.jwt.token");
    }

    @Test
    @DisplayName("Введены неверные данные: вызвалось исключение BadCredentials")
    void login_badCredentials_throwsException() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad Credentials"));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    @DisplayName("Регистрация прошла успешно: вернулся логин, роль, jwt-токен")
    void register_success() {
        User fakeUser = new User();
        fakeUser.setLogin("test_login_123");
        fakeUser.setPassword("test_password_123");
        fakeUser.setRole(Role.USER);
        fakeUser.setEmail("test_email_123");

        when(passwordEncoder.encode(any())).thenReturn("encode_password");
        when(userRepository.existsByLogin(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(fakeUser);
        when(jwtTokenProvider.generateToken("test_login_123")).thenReturn("fake.jwt.token");

        JwtResponse jwtResponse = authService.register(registerRequest);

        assertThat(jwtResponse.getJwt()).isEqualTo("fake.jwt.token");
        assertThat(jwtResponse.getRole()).isEqualTo(Role.USER.name());
        assertThat(jwtResponse.getLogin()).isEqualTo(registerRequest.getLogin());
    }

    @Test
    @DisplayName("Введенный login уже существует: вызвался ConflictException")
    void register_conflictException_login() {
        when(userRepository.existsByLogin(any()))
                .thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    @DisplayName("Введенный email уже существует: вызвался ConflictException")
    void register_conflictException_email() {
        when(userRepository.existsByEmail(any()))
                .thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(ConflictException.class);
    }
}
