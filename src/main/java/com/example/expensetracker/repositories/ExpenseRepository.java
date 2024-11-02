package com.example.expensetracker.repositories;

import com.example.expensetracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findAllByDateBetween(Date startDate,Date endDate);

    List<Expense> findAllByDate(Date date);

    @Query("SELECT e FROM Expense e WHERE e.date >= :threeMonthsAgo")
    List<Expense> findAllExpensesLastThreeMonths(@Param("threeMonthsAgo") Date threeMonthsAgo);

}
