package com.example.expensetracker.dtos;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserDto {

    private String email;

    @Size(min = 8, max = 50, message = "A senha deve ter entre 8 e 50 caracteres")
    private String password;

}
