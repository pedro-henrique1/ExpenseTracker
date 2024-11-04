package com.example.expensetracker.controller;

import com.example.expensetracker.dtos.ExpenseDto;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;
import com.example.expensetracker.services.ExpenseService;
import com.example.expensetracker.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/v1/expense/filter")
@PreAuthorize("hasAnyRole('expense')")
public class ExpenseFilterController {

    @Autowired
    private ExpenseService expenseService;

    @Operation(summary = "Busca as despesas com base em uma data de início e fim")
    @GetMapping("/between")
    public ResponseEntity<List<ExpenseDto>> getExpensesBetween(@RequestParam String startDate, @RequestParam String endDate) {
        User user = SecurityUtils.getAuthenticatedUser();
        List<ExpenseDto> expenses = expenseService.getExpenseForDateRange(Date.valueOf(startDate), Date.valueOf(endDate), user);
        return expenses.isEmpty() ? ResponseEntity.status(HttpStatus.NO_CONTENT).body(null) : ResponseEntity.ok(expenses);

    }

    @Operation(summary = "Busca as depesas com base em uma data")
    @GetMapping("/date")
    public ResponseEntity<List<ExpenseDto>> getExpensesByDate(@RequestParam("date") String date) {
        User user = SecurityUtils.getAuthenticatedUser();
        assert user != null;
        List<ExpenseDto> expenses = expenseService.getExpenseForDate(java.sql.Date.valueOf(date), user);
        return expenses.isEmpty() ? ResponseEntity.status(HttpStatus.NO_CONTENT).body(null) : ResponseEntity.ok(expenses);
    }

    @Operation(summary = "Busca as despesas dos últimos 3 meses")
    @GetMapping("/last-three-months")
    public ResponseEntity<List<ExpenseDto>> getExpensesLastThreeMonths() {
        User user = SecurityUtils.getAuthenticatedUser();
        List<ExpenseDto> expenses = expenseService.getExpensesLastThreeMonths(user).stream().map(Expense::toDto).collect(Collectors.toList());
        return expenses.isEmpty() ? ResponseEntity.status(HttpStatus.NO_CONTENT).body(null) : ResponseEntity.ok(expenses);
    }

    @Operation(summary = "Busca as depesas com base na categoria")
    @GetMapping("/category")
    public ResponseEntity<List<ExpenseDto>> getExpensesByFilterType(@RequestParam("filter") String filter) {
        User user = SecurityUtils.getAuthenticatedUser();
        List<ExpenseDto> expenses = expenseService.getExpenseFilterCategory(filter, user);
        return expenses.isEmpty() ? ResponseEntity.status(HttpStatus.NO_CONTENT).body(null) : ResponseEntity.ok(expenses);
    }
}
