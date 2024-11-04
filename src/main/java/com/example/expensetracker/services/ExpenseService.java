package com.example.expensetracker.services;

import com.example.expensetracker.dtos.ExpenseDto;
import com.example.expensetracker.mapper.ExpenseMapper;
import com.example.expensetracker.model.Categoria;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;
import com.example.expensetracker.repositories.ExpenseRepository;
import com.example.expensetracker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseService {
    @Autowired
    private ExpenseMapper expenseMapper;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    public ExpenseDto getExpense(Long id, User user) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Despesa não encontrada"));

        if (!expense.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Acesso negado: a despesa não pertence ao usuário.");
        }

        return Expense.toDto(expense);
    }

    public Expense saveExpense(User user, Expense expense) {
        expense.setUser(user);
        return expenseRepository.save(expense);
    }

    public List<?> getAllExpenses(User user) {
        List<Expense> expenses = expenseRepository.findByUser(user);
        return expenses.stream()
                .map(expenseMapper::toDto)
                .collect(Collectors.toList());
    }

    public ExpenseDto updateExpense(Long id, User user, ExpenseDto expenseDto) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not allowed to delete this expense");
        }

        // Atualiza os campos da despesa
        expense.setCategory(expenseDto.getCategory());
        expense.setDescription(expenseDto.getDescription());
        expense.setPrice(expenseDto.getPrice());
        expense.setDate(expenseDto.getDate());
        expense.setObservation(expenseDto.getObservation());

        // Salva e retorna a despesa atualizada
        Expense updatedExpense = expenseRepository.save(expense);

        // Converte a despesa atualizada em DTO
        return expenseMapper.toDto(updatedExpense);
    }



    public void deleteExpense(User user, Long id) {
        Expense expense = expenseRepository.findById(id).orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not allowed to delete this expense");
        }

        expenseRepository.delete(expense);
    }

    public List<ExpenseDto> getExpenseForDateRange(Date startDate, Date endDate, User user) {
        List<Expense> expenses =  expenseRepository.findAllByDateBetween(startDate, endDate, user);
        return expenses.stream().map(Expense::toDto).collect(Collectors.toList());
    }

    public List<ExpenseDto> getExpenseForDate(java.sql.Date date, User user) {
        List<Expense> expenses =  expenseRepository.findByDateAndUser(date, user);
        return expenses.stream().map(Expense::toDto).collect(Collectors.toList());
    }

    public List<Expense> getExpensesLastThreeMonths(User user) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -3);
        java.sql.Date threeMonthsAgo = new java.sql.Date(calendar.getTimeInMillis());
        return expenseRepository.findByDateAfterAndUser(threeMonthsAgo, user);
    }


    public List<ExpenseDto> getExpenseFilterCategory(String category, User user) {
        Categoria categoria = Categoria.valueOf(category.toUpperCase());
        List<Expense> expenses = expenseRepository.findFilterByCategory(categoria, user);
        return expenses.stream().map(Expense::toDto).collect(Collectors.toList());
    }
}