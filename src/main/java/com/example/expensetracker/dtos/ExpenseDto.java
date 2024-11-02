package com.example.expensetracker.dtos;

import com.example.expensetracker.model.Categoria;
import com.example.expensetracker.model.MetodoPagamento;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class ExpenseDto {
    private Integer id;
    private String description;
    private BigDecimal price;
    private Date date;
    private MetodoPagamento payment_method;
    private Categoria category;
    private String observation;
}
