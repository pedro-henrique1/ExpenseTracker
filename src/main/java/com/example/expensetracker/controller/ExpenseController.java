package com.example.expensetracker.controller;


import com.example.expensetracker.dtos.ExpenseDto;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;
import com.example.expensetracker.services.ExpenseService;
import com.example.expensetracker.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/expense")
@PreAuthorize("hasAnyRole('expense')")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Operation(summary = "Cria uma nova despesa")
    @PostMapping("/create")
    public ResponseEntity<String> addExpense(@RequestBody @Schema(description = "Dados da despesa") ExpenseDto expenseDto) {
        User user = SecurityUtils.getAuthenticatedUser();
        try {
            Expense expense = Expense.fromDto(expenseDto);
            expenseService.saveExpense(user, expense);
            return ResponseEntity.status(HttpStatus.CREATED).body("Expense added successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding expense: " + e.getMessage());
        }
    }

    @Operation(summary = "Bsuca uma despesa")
    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenses(@PathVariable Long id) {
        User user = SecurityUtils.getAuthenticatedUser();
        assert user != null;
        Expense expense = Expense.fromDto(expenseService.getExpense(id, user));
        return ResponseEntity.status(HttpStatus.OK).body(expense);
    }


    @Operation(summary = "Busca todas as despesas")
    @GetMapping("/all")
    public ResponseEntity<? extends List<?>> getAllExpenses() {
        User user = SecurityUtils.getAuthenticatedUser();
        List<?> expenses = expenseService.getAllExpenses(user);
        return Optional.of(expenses).filter(list -> !list.isEmpty()).map(list -> ResponseEntity.status(HttpStatus.OK).body(list)).orElse(ResponseEntity.status(HttpStatus.NO_CONTENT).body(null));
    }

    @Operation(summary = "Atualiza a despesas com base no id")
    @PatchMapping("/{id}")
    public ResponseEntity<ExpenseDto> getExpenseById(@PathVariable Long id, @RequestBody ExpenseDto expenseDto) {
        User user = SecurityUtils.getAuthenticatedUser();
        assert user != null;
        ExpenseDto updateExpense = expenseService.updateExpense(id, user, expenseDto);
        return ResponseEntity.status(HttpStatus.OK).body(updateExpense);
    }

    @Operation(summary = "Deleta a despesa pelo id")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExpenseById(@PathVariable Long id) {
        User user = SecurityUtils.getAuthenticatedUser();
        assert user != null;
        expenseService.deleteExpense(user, id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
