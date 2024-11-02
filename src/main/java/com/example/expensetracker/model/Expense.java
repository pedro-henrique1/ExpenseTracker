package com.example.expensetracker.model;


import com.example.expensetracker.dtos.ExpenseDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.util.Date;

@Table(name = "expenses")
@Entity
@Getter
@Setter

public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Date date;

    @Enumerated(EnumType.STRING)
    private MetodoPagamento payment_method;

    @Enumerated(EnumType.STRING)
    private Categoria category;

    private String observation;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    // Método de conversão de DTO para entidade
    public static Expense fromDto(ExpenseDto dto) {
        Expense expense = new Expense();
        expense.setId(dto.getId());
        expense.setDescription(dto.getDescription());
        expense.setPrice(dto.getPrice());
        expense.setDate(dto.getDate());
        expense.setPayment_method(dto.getPayment_method());
        expense.setCategory(dto.getCategory());
        expense.setObservation(dto.getObservation());
        return expense;
    }

    // Método de conversão de entidade para DTO
    public static ExpenseDto toDto(Expense expense) {
        ExpenseDto dto = new ExpenseDto();
        dto.setId(expense.getId());
        dto.setDescription(expense.getDescription());
        dto.setPrice(expense.getPrice());
        dto.setDate(expense.getDate());
        dto.setPayment_method(expense.getPayment_method()); // Corrigido aqui
        dto.setCategory(expense.getCategory());
        dto.setObservation(expense.getObservation());
        return dto;
    }

}

