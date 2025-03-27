package com.example.expensetracker.service;

import com.example.expensetracker.dtos.LoginUserDto;
import com.example.expensetracker.dtos.RegisterUserDto;
import com.example.expensetracker.exceptions.EmailDuplicateException;
import com.example.expensetracker.exceptions.MissingRequiredFieldException;
import com.example.expensetracker.model.User;
import com.example.expensetracker.repositories.UserRepository;
import com.example.expensetracker.services.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import javax.security.auth.login.CredentialNotFoundException;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@Slf4j
@DisplayName("Testes da AuthenticationService")
public class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;


    @BeforeEach
    public void setUp() {
        lenient().when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("encodedPassword");
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken("user", "password");
        lenient().when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("user", "password", new ArrayList<>()));

        reset(userRepository);

    }


    @Test
    @DisplayName("Deve criar um usuário com sucesso")
    public void testCreateUser() {
        RegisterUserDto userDto = new RegisterUserDto();
        userDto.setEmail("50testuser@test.com");
        userDto.setPassword("testpass");

        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setEmail(userDto.getEmail());

        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        User savedUser = userService.signup(userDto);

        assertNotNull(savedUser);
        assertEquals(userDto.getEmail(), savedUser.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testEmailDuplicateConstraint() {
        RegisterUserDto input = new RegisterUserDto();
        input.setEmail("testuser@test.com");
        input.setPassword("validPassword123");

        User user1 = new User();
        user1.setEmail(input.getEmail());
        user1.setPassword(passwordEncoder.encode(input.getPassword()));
        lenient().when(userRepository.save(any(User.class))).thenReturn(user1);

        RegisterUserDto duplicateInput = new RegisterUserDto();
        duplicateInput.setEmail("testuser@test.com");
        duplicateInput.setPassword("anotherPassword123");
        doThrow(new DataIntegrityViolationException("O e-mail já está em uso.")).when(userRepository).save(any(User.class));

        EmailDuplicateException exception = assertThrows(EmailDuplicateException.class,
                () -> userService.signup(duplicateInput));
        assertEquals("O e-mail já está em uso.", exception.getMessage());
        verify(userRepository, times(0)).save(any(User.class));

    }



    @Test
    @DisplayName("Deve lançar exceção quando a senha for nula")
    public void testPasswordNull() {
        RegisterUserDto userDto = new RegisterUserDto();
        userDto.setEmail("50testuser@test.com");
        userDto.setPassword("");

        MissingRequiredFieldException exception = assertThrows(MissingRequiredFieldException.class, () -> userService.signup(userDto));
        assertEquals("Email e/ou senha não pode ser vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o e-mail for nulo")
    public void testEmailNull() {
        RegisterUserDto userDto = new RegisterUserDto();
        userDto.setEmail("");
        userDto.setPassword("testpass");

        MissingRequiredFieldException exception = assertThrows(MissingRequiredFieldException.class, () -> userService.signup(userDto));
        assertEquals("Email e/ou senha não pode ser vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o e-mail for inválido")
    public void testInvalidEmail() {
        RegisterUserDto userDto = new RegisterUserDto();
        userDto.setEmail("testuser-test.com");
        userDto.setPassword("testpass");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.signup(userDto));
        assertEquals("Formato de email inválido", exception.getMessage());
    }

    @Test
    @DisplayName("Deve autenticar usuário com credenciais válidas")
    public void testLogin() {
        String email = "50testuser@test.com";
        String password = "testpass";

        User mockUser = new User();
        mockUser.setEmail(email);
        mockUser.setPassword(password);

        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(mockUser));

        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setEmail(email);
        loginUserDto.setPassword(password);

        User authenticatedUser = userService.authenticate(loginUserDto);
        assertNotNull(authenticatedUser);
        assertEquals(mockUser.getUsername(), authenticatedUser.getUsername());
    }

    @Test
    @DisplayName("Deve lançar exceção para senha incorreta")
    public void testPasswordWrong() {
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setEmail("nonexistent@test.com");
        loginUserDto.setPassword("wrongpassword");


        assertThrows(BadCredentialsException.class, () -> userService.authenticate(loginUserDto));
    }

    @Test
    @DisplayName("Autenticação de usuário inexistente")
    public void testUserNonExistent() {
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setEmail("50testuser@test.com");
        loginUserDto.setPassword("testpass");

        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                () -> userService.authenticate(loginUserDto));
        assertEquals("Usuário não encontrado", exception.getMessage());

    }
}