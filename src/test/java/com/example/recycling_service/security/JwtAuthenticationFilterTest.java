package com.example.recycling_service.security;

import io.jsonwebtoken.Jwt;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    UserDetailsService userDetailsService;

    @InjectMocks
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    FilterChain filterChain;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Валидный Bearer токен - SecurityContext заполнен - цепочка продолжается")
    void validToken_setAuthentication_andContinuesChain() throws Exception{
        UserDetails fakeUser = new org.springframework.security.core.userdetails.User(
                "test_login", "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        request.addHeader("Authorization", "Bearer valid.jwt.token");

        when(jwtTokenProvider.validateToken("valid.jwt.token"))
                .thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken("valid.jwt.token"))
                .thenReturn("test_login");
        when(userDetailsService.loadUserByUsername("test_login"))
                .thenReturn(fakeUser);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getName()).isEqualTo("test_login");

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Без заголовка Authorization - цепочка продолжается, контекст пустой")
    void noToken_continuesChain_contextEmpty() throws Exception{
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Невалидный токен - 401, цепочка не продолжается")
    void invalidToken_return401_chainNotCalled() throws Exception{
        request.addHeader("Authorization", "Bearer bad.token");
        when(jwtTokenProvider.validateToken("bad.token"))
                .thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain, never()).doFilter(request, response);
    }
}