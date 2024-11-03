package com.example.expensetracker.repositories;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUser(User user);

//    List<Expense> findAllByDateBetween(Date startDate, Date endDate, User user);

    List<Expense> findAllByDateAndUser(Date date, User user);

    @Query("SELECT e FROM Expense e WHERE e.date >= :threeMonthsAgo AND e.user = :user")
    List<Expense> findAllExpensesLastThreeMonths(@Param("threeMonthsAgo") Date threeMonthsAgo, @Param("user") User user);

    @Query("SELECT e FROM Expense e WHERE e.date = :date AND e.user = :user")
    List<Expense> findByDateAndUser(@Param("date") java.sql.Date date, @Param("user") User user);

    List<Expense> findAllByDateBetween(Date startDate, Date endDate);
}
