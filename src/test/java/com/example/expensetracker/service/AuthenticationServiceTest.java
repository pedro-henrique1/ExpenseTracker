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
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;

import javax.security.auth.login.CredentialNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
@DisplayName("Testes da AuthenticationService")
public class AuthenticationServiceTest {

    @Autowired
    private AuthenticationService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }


    @Test
    @DisplayName("Deve criar um usuário com sucesso")
    public void testCreateUser() {
        RegisterUserDto user = new RegisterUserDto();
        user.setEmail("50testuser@test.com");
        user.setPassword("testpass");
        User savedUser = userService.signup(user);
        log.info(savedUser.getUsername());
        assertNotNull(savedUser);
        assertEquals(user.getEmail(), savedUser.getUsername());
        assertTrue(userRepository.existsById(Long.valueOf(savedUser.getId())));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário com e-mail duplicado")
    public void testDuplicateEmail() {
        RegisterUserDto user1 = new RegisterUserDto();
        user1.setEmail("60testuser@test.com");
        user1.setPassword("testpass");
        userService.signup(user1);
        RegisterUserDto user2 = new RegisterUserDto();
        user2.setEmail("60testuser@test.com");
        user2.setPassword("testpass");
        EmailDuplicateException exception = assertThrows(EmailDuplicateException.class, () -> userService.signup(user2));
        assertEquals("O e-mail já está em uso.", exception.getMessage());
        assertNotNull(exception);
    }

    @Test
    @DisplayName("Deve lançar exceção quando a senha for nula")
    public void testPasswordNull() {
        RegisterUserDto user = new RegisterUserDto();
        user.setEmail("50testuser@test.com");
        user.setPassword("");
        MissingRequiredFieldException exception = assertThrows(MissingRequiredFieldException.class, () -> userService.signup(user));
        log.info(exception.getMessage());
        assertEquals("Email e/ou senha não pode ser vazio", exception.getMessage());
        assertNotNull(exception);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o e-mail for nulo")
    public void testEmailNull() {
        RegisterUserDto user = new RegisterUserDto();
        user.setEmail("");
        user.setPassword("testpass");
        MissingRequiredFieldException exception = assertThrows(MissingRequiredFieldException.class, () -> userService.signup(user));
        log.info(exception.getMessage());
        assertNotNull(exception);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o e-mail for inválido")
    public void testInvalidEmail() {
        RegisterUserDto user = new RegisterUserDto();
        user.setEmail("testuser-test.com");
        user.setPassword("testpass");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.signup(user));
        assertEquals("Formato de email inválido", exception.getMessage());
        log.info(exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a senha for muito longa")
    public void testLongPassword() {
        RegisterUserDto user = new RegisterUserDto();
        user.setEmail("longpass@test.com");
        user.setPassword("a".repeat(100));
        MissingRequiredFieldException exception = assertThrows(
                MissingRequiredFieldException.class,
                () -> userService.signup(user)
        );

        assertEquals("A senha deve ter entre 8 e 50 caracteres", exception.getMessage());
    }

    @Test
    @DisplayName("Deve autenticar usuário com credenciais válidas")
    public void  testLogin() {
        RegisterUserDto user = new RegisterUserDto();
        user.setEmail("50testuser@test.com");
        user.setPassword("testpass");
        User savedUser = userService.signup(user);
        assertNotNull(savedUser);

        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setEmail("50testuser@test.com");
        loginUserDto.setPassword("testpass");

        User authenticatedUser = userService.authenticate(loginUserDto);
        assertNotNull(authenticatedUser);
        log.info("Email: {}", loginUserDto.getEmail());
        log.info("Expected Username: {}, Authenticated Username: {}", savedUser.getUsername(), authenticatedUser.getUsername());
        assertEquals(savedUser.getUsername(), authenticatedUser.getUsername());

    }

    @Test
    @DisplayName("Deve retorna um erro de password")
    public void  testPasswordWrong() {
        RegisterUserDto user = new RegisterUserDto();
        user.setEmail("50testuser@test.com");
        user.setPassword("testpass");
        User savedUser = userService.signup(user);
        assertNotNull(savedUser);

        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setEmail("50testuser@test.com");
        loginUserDto.setPassword("test");
        assertThrows(BadCredentialsException.class, () -> userService.authenticate(loginUserDto));
    }

    @Test
    @DisplayName("Verificação de senha criptografada")
    public void  testPassword() {
        RegisterUserDto user = new RegisterUserDto();
        user.setEmail("50testuser@test.com");
        user.setPassword("testpass");
        User savedUser = userService.signup(user);
        assertNotNull(savedUser);

        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setEmail("50testuser@test.com");
        loginUserDto.setPassword("testpass");

        assertNotEquals(savedUser.getPassword(), loginUserDto.getPassword());
    }

    @Test
    @DisplayName("Autenticação de usuário inexistente")
    public void  testUserNonExistent() {
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setEmail("50testuser@test.com");
        loginUserDto.setPassword("testpass");
        assertThrows(BadCredentialsException.class, () -> userService.authenticate(loginUserDto));
    }
}