package com.example.expensetracker.service;


import com.example.expensetracker.dtos.ExpenseDto;
import com.example.expensetracker.dtos.RegisterUserDto;
import com.example.expensetracker.enums.Category;
import com.example.expensetracker.enums.PaymentMethod;
import com.example.expensetracker.exceptions.MissingRequiredFieldException;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;
import com.example.expensetracker.services.AuthenticationService;
import com.example.expensetracker.services.ExpenseService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.AssertionFailure;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Slf4j
@DisplayName("Teste de validação das despesas")
public class TestExpenseService {

    @Autowired
    private AuthenticationService userService;

    @Autowired
    private ExpenseService expenseService;

    private static User testUser;

    private static User testUser2;

    @BeforeAll
    static void setup(@Autowired AuthenticationService userService,
                      @Autowired ExpenseService expenseService) {

        RegisterUserDto userDto = new RegisterUserDto();
        userDto.setEmail("testuser1@test.com");
        userDto.setPassword("testpass");
        testUser = userService.signup(userDto);
        assertNotNull(testUser, "Usuário de teste não foi criado corretamente.");

        RegisterUserDto userDto2 = new RegisterUserDto();
        userDto2.setEmail("testuser2@test.com");
        userDto2.setPassword("testpass2");
        testUser2 = userService.signup(userDto2);
        assertNotNull(testUser2, "Usuário de teste não foi criado corretamente.");


        Expense expense1 = new Expense();
        expense1.setUser(testUser);
        expense1.setDescription("Almoço no restaurante");
        expense1.setPrice(new BigDecimal("35.50"));
        expense1.setCategory(Category.ALIMENTACAO);
        expense1.setPaymentMethod(PaymentMethod.CARTAOCREDITO);
        expense1.setDate(Date.valueOf(LocalDate.now()));
        expense1.setObservation("alimentação para o trabalho");
        expenseService.saveExpense(testUser, expense1);

        Expense expense2 = new Expense();
        expense2.setUser(testUser);
        expense2.setDescription("Transporte Uber");
        expense2.setPrice(new BigDecimal("25.00"));
        expense2.setCategory(Category.TRANSPORTE);
        expense2.setPaymentMethod(PaymentMethod.PIX);
        expense2.setObservation("Transporte para o trabalho");
        expense2.setDate(Date.valueOf(LocalDate.now()));
        expenseService.saveExpense(testUser, expense2);
    }


    @Test
    @DisplayName("Buscar dados de despesas do usuario")
    public void buscarDadosDespesas() {
        List<?> expenses = expenseService.getAllExpenses(testUser);
        log.info("Lista de despesas: {}", expenses);
        log.info("Usuário de teste: {}", testUser);
        assertNotNull(expenses, "A lista de despesas não deve ser nula.");
        assertTrue(expenses.size() >= 2, "O usuário deve ter pelo menos duas despesas cadastradas.");
    }

    @Test
    @DisplayName("Buscar dados de uma despesa do usuario")
    public void buscarDadosDespesasUsuario() {
        ExpenseDto expense = expenseService.getExpense(1L, testUser);
        log.info("Despesa encontrada: {}", expense.toString());
        assertNotNull(expense, "A despesa não deve ser nula.");
    }


    @Test
    @DisplayName("Deve lançar AccessDeniedException quando a despesa não pertencer ao usuário")
    public void erroDeAcessoDespesas() {
        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> expenseService.getExpense(1L, testUser2));
        assertEquals("Acesso negado: a despesa não pertence ao usuário.", exception.getMessage());
    }


    @Test
    @DisplayName("Deve lançar RuntimeException quando a despesa não foi encontrada")
    public void despesaNaoEncontrada() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> expenseService.getExpense(40L, testUser2));
        assertEquals("Despesa não encontrada", exception.getMessage());
    }

    @Test
    public void criarDespesaComSucesso() {
        Expense novaDespesa = new Expense();
        novaDespesa.setDescription("Compra de mercado");
        novaDespesa.setPrice(new BigDecimal("150.00"));
        novaDespesa.setCategory(Category.ALIMENTACAO);
        novaDespesa.setPaymentMethod(PaymentMethod.PIX);
        novaDespesa.setDate(Date.valueOf(LocalDate.now()));
        novaDespesa.setObservation("Compras do mês");
        Expense expense = expenseService.saveExpense(testUser, novaDespesa);

        assertNotNull(expense, "A despesa criada não deve ser nula.");
        assertNotNull(expense.getId(), "A despesa criada deve ter um ID.");
        assertEquals("Compra de mercado", expense.getDescription());
    }

    @Test
    public void criarDespesaSemUsuario() {
        Expense novaDespesa = new Expense();
        novaDespesa.setDescription("Compra de mercado");
        novaDespesa.setPrice(new BigDecimal("150.00"));
        novaDespesa.setCategory(Category.ALIMENTACAO);
        novaDespesa.setPaymentMethod(PaymentMethod.PIX);
        novaDespesa.setDate(Date.valueOf(LocalDate.now()));
        novaDespesa.setObservation("Compras do mês");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> expenseService.saveExpense(null, novaDespesa));

        assertEquals("O usuario nao pose ser invalido", exception.getMessage());
    }

    @Test
    public void criarDespesaComValorNegativo() {
        Expense novaDespesa = new Expense();
        novaDespesa.setDescription("Compra de mercado");
        novaDespesa.setPrice(new BigDecimal("-150.00"));
        novaDespesa.setCategory(Category.ALIMENTACAO);
        novaDespesa.setPaymentMethod(PaymentMethod.PIX);
        novaDespesa.setDate(Date.valueOf(LocalDate.now()));
        novaDespesa.setObservation("Compras do mês");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> expenseService.saveExpense(testUser, novaDespesa));

        assertEquals("O preço não pode ser negativo", exception.getMessage());
    }

    @Test
    public void criarDespesaComValorInvalido() {
        Expense novaDespesa = new Expense();
        novaDespesa.setDescription("");
        novaDespesa.setPrice(new BigDecimal("1.1"));
        novaDespesa.setCategory(Category.ALIMENTACAO);
        novaDespesa.setPaymentMethod(PaymentMethod.PIX);
        novaDespesa.setDate(Date.valueOf(LocalDate.now()));
        novaDespesa.setObservation("Compras do mês");

        MissingRequiredFieldException exception = assertThrows(MissingRequiredFieldException.class,
                () -> expenseService.saveExpense(testUser, novaDespesa));

        assertEquals("Os campos devem ser preenchidos corretamente", exception.getMessage());
    }

    @Test
    public void atualizarExpense() {
        ExpenseDto novaDespesa = new ExpenseDto();
        novaDespesa.setDescription("Compra de mercado");
        novaDespesa.setPrice(new BigDecimal("150.00"));
        novaDespesa.setCategory(Category.ALIMENTACAO);
        novaDespesa.setPaymentMethod(PaymentMethod.PIX);
        novaDespesa.setDate(Date.valueOf(LocalDate.now()));
        novaDespesa.setObservation("Compra de mercado");
        Expense expense = expenseService.saveExpense(testUser, Expense.fromDto(novaDespesa));
        expense.setPrice(new BigDecimal("1500.00"));
        expense.setObservation("Compra de mercado do mes");
        expenseService.updateExpense(1L, testUser, novaDespesa);

        assertEquals("Compra de mercado do mes", expense.getObservation());
        assertEquals("1500.00", expense.getPrice().toString());


    }








}
