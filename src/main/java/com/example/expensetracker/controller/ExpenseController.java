package com.example.expensetracker.controller;


import com.example.expensetracker.dtos.ExpenseDto;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;
import com.example.expensetracker.services.ExpenseService;
import com.example.expensetracker.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/expanse")
@PreAuthorize("hasAnyRole('expanse')")
public class ExpenseController {


    @Autowired
    private ExpenseService expenseService;


    @GetMapping("/test")
    public String expense() {
        return "expense success";
    }

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


    @GetMapping("/all")
    public ResponseEntity<? extends List<?>> getAllExpenses() {
        User user = SecurityUtils.getAuthenticatedUser();
        List<?> expenses = expenseService.getAllExpenses(user);
        return Optional.of(expenses).filter(list -> !list.isEmpty()).map(list -> ResponseEntity.status(HttpStatus.OK).body(list)).orElse(ResponseEntity.status(HttpStatus.NO_CONTENT).body(null));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ExpenseDto> getExpenseById(@PathVariable Long id, @RequestBody ExpenseDto expenseDto) {
        User user = SecurityUtils.getAuthenticatedUser();
        ExpenseDto updateExpense = expenseService.updateExpense(id, user, expenseDto);
        return ResponseEntity.status(HttpStatus.OK).body(updateExpense);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExpenseById(@PathVariable Long id) {
        User user = SecurityUtils.getAuthenticatedUser();
        expenseService.deleteExpense(user, id);
        return ResponseEntity.status(HttpStatus.OK).body("Expense deleted successfully." + id);
    }

    @GetMapping("/between")
    public ResponseEntity<List<ExpenseDto>> getExpensesBetween(@RequestParam String startDate, @RequestParam String endDate) {
        User user = SecurityUtils.getAuthenticatedUser();
        List<ExpenseDto> expenses = expenseService.getExpenseForDateRange(Date.valueOf(startDate), Date.valueOf(endDate), user);
        return expenses.isEmpty() ? ResponseEntity.status(HttpStatus.NO_CONTENT).body(null) : ResponseEntity.ok(expenses);

    }

    @GetMapping("/date")
    public ResponseEntity<List<ExpenseDto>> getExpensesByDate(@RequestParam("date") String date) {
        User user = SecurityUtils.getAuthenticatedUser();
        assert user != null;
        List<ExpenseDto> expenses = expenseService.getExpenseForDate(java.sql.Date.valueOf(date), user);
        return expenses.isEmpty() ? ResponseEntity.status(HttpStatus.NO_CONTENT).body(null) : ResponseEntity.ok(expenses);
    }

    @GetMapping("/last-three-months")
    public ResponseEntity<List<ExpenseDto>> getExpensesLastThreeMonths() {
        User user = SecurityUtils.getAuthenticatedUser();
        List<ExpenseDto> expenses = expenseService.getExpensesLastThreeMonths(user).stream().map(Expense::toDto).collect(Collectors.toList());
        return expenses.isEmpty() ? ResponseEntity.status(HttpStatus.NO_CONTENT).body(null) : ResponseEntity.ok(expenses);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ExpenseDto>> getExpensesByFilterType(@RequestParam("filter") String filter) {
        User user = SecurityUtils.getAuthenticatedUser();
        List<ExpenseDto> expenses = expenseService.getExpenseFilterCategory(filter, user);
        return expenses.isEmpty() ? ResponseEntity.status(HttpStatus.NO_CONTENT).body(null) : ResponseEntity.ok(expenses);
    }

}
