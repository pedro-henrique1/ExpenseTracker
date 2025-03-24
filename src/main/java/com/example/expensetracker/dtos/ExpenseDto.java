package com.example.expensetracker.dtos;

import com.example.expensetracker.enums.Category;
import com.example.expensetracker.enums.PaymentMethod;
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
    private Long id;
    private String description;
    private BigDecimal price;
    private Date date;
    private PaymentMethod paymentMethod;
    private Category category;
    private String observation;
}
