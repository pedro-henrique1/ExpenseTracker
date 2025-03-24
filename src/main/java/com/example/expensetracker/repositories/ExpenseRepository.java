package com.example.expensetracker.repositories;

import com.example.expensetracker.enums.Category;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUser(User user);


    @Query("SELECT e FROM Expense e WHERE e.date >= :date AND e.user = :user")
    List<Expense> findByDateAfterAndUser(@Param("date") Date date, @Param("user") User user);

    @Query("SELECT e FROM Expense e WHERE e.date = :date AND e.user = :user")
    List<Expense> findByDateAndUser(@Param("date") Date date, @Param("user") User user);

    @Query("SELECT e FROM Expense e WHERE e.date BETWEEN :startDate AND :endDate AND e.user = :user")
    List<Expense> findAllByDateBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("user") User user);

    @Query("SELECT e FROM Expense e WHERE e.category = :category AND e.user = :user")
    List<Expense> findFilterByCategory(Category category, User user);
}
