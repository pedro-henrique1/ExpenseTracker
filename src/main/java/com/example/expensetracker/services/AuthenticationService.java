package com.example.expensetracker.services;

import com.example.expensetracker.dtos.LoginUserDto;
import com.example.expensetracker.dtos.RegisterUserDto;
import com.example.expensetracker.exceptions.EmailDuplicateException;
import com.example.expensetracker.exceptions.MissingRequiredFieldException;
import com.example.expensetracker.model.User;
import com.example.expensetracker.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepository userRepository, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(@Valid RegisterUserDto input) {
        validateInput(input);
        try {
            User user = new User();
            user.setEmail(input.getEmail());
            user.setPassword(passwordEncoder.encode(input.getPassword()));
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new EmailDuplicateException("O e-mail já está em uso.");
        }
    }

    public User authenticate(LoginUserDto input) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(input.getEmail(), input.getPassword()));

        return userRepository.findByEmail(input.getEmail()).orElseThrow();
    }


    public void validateInput(RegisterUserDto input) {
        if (input.getPassword() == null || input.getPassword().isEmpty() ||
                input.getEmail() == null || input.getEmail().isEmpty()) {
            throw new MissingRequiredFieldException("Email e/ou senha não pode ser vazio");
        }
        if (!input.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Formato de email inválido");
        }
        if (input.getPassword().length() > 50 || input.getPassword().length() < 8) {
            throw new MissingRequiredFieldException("A senha deve ter entre 8 e 50 caracteres");
        }
    }
}
