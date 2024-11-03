package com.example.expensetracker.dtos;

import com.example.expensetracker.model.Categoria;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.MetodoPagamento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseDto {
    private String description;
    private BigDecimal price;
    private Date date;
    private MetodoPagamento paymentMethod;
    private Categoria category;
    private String observation;
}
