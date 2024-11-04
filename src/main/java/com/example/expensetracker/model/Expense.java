package com.example.expensetracker.model;


import com.example.expensetracker.dtos.ExpenseDto;
import com.example.expensetracker.enums.Categoria;
import com.example.expensetracker.enums.MetodoPagamento;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.util.Date;

@Table(name = "expenses")
@Entity
@Getter
@Setter
@ToString
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
    private java.sql.Date date;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", columnDefinition = "ENUM('Dinheiro', 'Cartão de Crédito', 'Cartão de Débito', 'Transferência Bancária', 'Pix', 'Boleto', 'Aplicativos de Pagamento', 'Cheque', 'Cartão de Benefícios')")
    private MetodoPagamento paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", columnDefinition = "ENUM ('Alimentação', 'Transporte', 'Lazer', 'Saúde', 'Moradia','Educação', 'Outros')")
    private Categoria category;

    private String observation;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Método de conversão de DTO para entidade
    public static Expense fromDto(ExpenseDto dto) {
        Expense expense = new Expense();
        expense.setDescription(dto.getDescription());
        expense.setPrice(dto.getPrice());
        expense.setDate(dto.getDate());
        expense.setPaymentMethod(dto.getPaymentMethod()); // Converte o ENUM
        expense.setCategory(dto.getCategory());
        expense.setObservation(dto.getObservation());

        return expense;
    }

    // Método de conversão de entidade para DTO
    public static ExpenseDto toDto(Expense expense) {
        ExpenseDto dto = new ExpenseDto();
        dto.setDescription(expense.getDescription());
        dto.setPrice(expense.getPrice());
        dto.setDate(expense.getDate());
        dto.setPaymentMethod(expense.getPaymentMethod()); // Corrigido aqui
        dto.setCategory(expense.getCategory());
        dto.setObservation(expense.getObservation());
        return dto;
    }

}

