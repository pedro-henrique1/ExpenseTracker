package com.example.expensetracker.services;

import com.example.expensetracker.dtos.ExpenseDto;
import com.example.expensetracker.enums.Category;
import com.example.expensetracker.exceptions.MissingRequiredFieldException;
import com.example.expensetracker.mapper.ExpenseMapper;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;
import com.example.expensetracker.repositories.ExpenseRepository;
import com.example.expensetracker.repositories.UserRepository;
import com.example.expensetracker.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import org.hibernate.AssertionFailure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.sql.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ch.qos.logback.core.util.OptionHelper.isNullOrEmpty;


@Service
public class ExpenseService {
    private static final Logger log = LoggerFactory.getLogger(ExpenseService.class);
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
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("O usuario nao pode ser invalido");
        }
        if (expense.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O preço não pode ser negativo");
        }

        boolean hasNullField = expense.getCategory() == null ||
                expense.getDescription() == null ||
                expense.getDate() == null ||
                expense.getPaymentMethod() == null;


        if (hasNullField){
            throw new MissingRequiredFieldException("Os campos devem ser preenchidos corretamente");
        }

        expense.setUser(user);
        return expenseRepository.save(expense);
    }

    public List<ExpenseDto> getAllExpenses(User user) {
        List<Expense> expenses = expenseRepository.findByUser(user);
        return expenses.stream()
                .map(expenseMapper::toDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public ExpenseDto updateExpense(Long id, User user, ExpenseDto expenseDto) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Despesa não encontrada"));

        if (!expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not allowed to delete this expense");
        }

        expense.setCategory(expenseDto.getCategory());
        expense.setDescription(expenseDto.getDescription());
        expense.setPrice(expenseDto.getPrice());
        expense.setDate(expenseDto.getDate());
        expense.setObservation(expenseDto.getObservation());

        Expense updatedExpense = expenseRepository.save(expense);
        return expenseMapper.toDto(updatedExpense);
    }


    public void deleteExpense(User user, Long id) {
        Expense expense = expenseRepository.findById(id).orElseThrow(() -> new RuntimeException("Despesa não encontrada"));

        if (!expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Acesso negado: a despesa não pertence ao usuário.");
        }

        expenseRepository.delete(expense);
    }

    public List<ExpenseDto> getExpenseForDateRange(Date startDate, Date endDate, User user) {
        List<Expense> expenses = expenseRepository.findAllByDateBetween(startDate, endDate, user);
        return expenses.stream().map(Expense::toDto).collect(Collectors.toList());
    }

    public List<ExpenseDto> getExpenseForDate(java.sql.Date date, User user) {
        List<Expense> expenses = expenseRepository.findByDateAndUser(date, user);
        return expenses.stream().map(Expense::toDto).collect(Collectors.toList());
    }

    public List<Expense> getExpensesLastThreeMonths(User user) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -3);
        java.sql.Date threeMonthsAgo = new java.sql.Date(calendar.getTimeInMillis());
        return expenseRepository.findByDateAfterAndUser(threeMonthsAgo, user);
    }


    public List<ExpenseDto> getExpenseFilterCategory(String category, User user) {
        Category categoria = Category.valueOf(category.toUpperCase());
        List<Expense> expenses = expenseRepository.findFilterByCategory(categoria, user);
        return expenses.stream().map(Expense::toDto).collect(Collectors.toList());
    }
}