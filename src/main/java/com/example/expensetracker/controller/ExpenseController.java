package com.example.expensetracker.controller;


import com.example.expensetracker.dtos.ExpenseDto;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.MetodoPagamento;
import com.example.expensetracker.repositories.ExpenseRepository;
import com.example.expensetracker.services.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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


    @PostMapping("/create")
    public ResponseEntity<String> addExpense(@RequestBody ExpenseDto expenseDto) {
        try {
            Expense expense = Expense.fromDto(expenseDto);
            expenseService.saveExpense(expense);
            System.out.println(expense);
            return ResponseEntity.status(HttpStatus.CREATED).body("Expense added successfully." + expense.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding expense: " + e.getMessage());
        }
    }


    @GetMapping("/all")
    public ResponseEntity<? extends List<?>> getAllExpenses() {
        List<?> expenses = expenseService.getAllExpenses();

        return Optional.of(expenses)
                .filter(list -> !list.isEmpty())
                .map(list -> ResponseEntity.status(HttpStatus.OK).body(list))
                .orElse(ResponseEntity.status(HttpStatus.NO_CONTENT).body(null));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ExpenseDto> getExpenseById(@PathVariable Long id, @RequestBody ExpenseDto expenseDto) {
        ExpenseDto result = expenseService.getExpense(id);

        result.setCategory(expenseDto.getCategory());
        result.setDescription(expenseDto.getDescription());
        result.setPrice(expenseDto.getPrice());
        result.setDate(expenseDto.getDate());
        result.setObservation(expenseDto.getObservation());
        return ResponseEntity.status(HttpStatus.OK).body(result);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExpenseById(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.status(HttpStatus.OK).body("Expense deleted successfully." + id);
    }

    @GetMapping("/between")
    public ResponseEntity<List<Expense>> getExpensesBetween(@RequestParam Date startDate, @RequestParam Date endDate) {
        List<Expense> expenses = expenseService.getExpenseForDateRange(startDate, endDate);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/date")
    public ResponseEntity<List<Expense>> getExpensesByDate(@RequestParam Date date) {
        List<Expense> expenses = expenseService.getExpenseForDate(date);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/last-three-months")
    public ResponseEntity<List<Expense>> getExpensesLastThreeMonths() {
        List<Expense> expenses = expenseService.getExpensesLastThreeMonths();
        return ResponseEntity.ok(expenses);
    }

}
