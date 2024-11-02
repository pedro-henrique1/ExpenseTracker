package com.example.expensetracker.mapper;

import com.example.expensetracker.dtos.ExpenseDto;
import com.example.expensetracker.model.Expense;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ExpenseMapper {
    ExpenseDto toDto(Expense expense);
    Expense fromDto(ExpenseDto expenseDto);
}
