package com.example.expensetracker.services;

import com.example.expensetracker.dtos.ExpenseDto;
import com.example.expensetracker.mapper.ExpenseMapper;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExpenseService {
    @Autowired
    private ExpenseMapper expenseMapper;

    @Autowired
    private ExpenseRepository expenseRepository;

    public ExpenseDto getExpense(Long id) {
        Expense expense = expenseRepository.findById(id).orElse(null);
        return expenseMapper.toDto(expense);
    }

    public void saveExpense(Expense expense) {
        System.out.println(expense.getPaymentMethod());
        expenseRepository.save(expense);
    }
}