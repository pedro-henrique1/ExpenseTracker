package com.example.expensetracker.controller;


import com.example.expensetracker.dtos.ExpenseDto;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.repositories.ExpenseRepository;
import com.example.expensetracker.services.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/expanse")
@PreAuthorize("hasAnyRole('expanse')")
public class ExpenseController {


    @Autowired
    private ExpenseService expenseService;


    @GetMapping("/test")
    public String expense() {
        return "expense success";
    }


    @PostMapping("/create")
    public ResponseEntity<String> addExpense(@RequestBody ExpenseDto expenseDto) {
        try {
            // Você pode adicionar validações adicionais aqui
            if (expenseDto == null) {
                return ResponseEntity.badRequest().body("Expense data is missing.");
            }

            // Converte ExpenseDto em Expense e salva no banco
            expenseService.saveExpense(expenseDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Expense added successfully.");
        } catch (Exception e) {
            // Tratar exceções e retornar um erro
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding expense: " + e.getMessage());
        }

    }
}
