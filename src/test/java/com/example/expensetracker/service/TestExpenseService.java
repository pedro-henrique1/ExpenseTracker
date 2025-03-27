package com.example.expensetracker.service;

import com.example.expensetracker.dtos.ExpenseDto;
import com.example.expensetracker.dtos.RegisterUserDto;
import com.example.expensetracker.enums.Category;
import com.example.expensetracker.enums.PaymentMethod;
import com.example.expensetracker.exceptions.MissingRequiredFieldException;
import com.example.expensetracker.mapper.ExpenseMapper;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;
import com.example.expensetracker.repositories.ExpenseRepository;
import com.example.expensetracker.services.ExpenseService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
@DisplayName("Teste de validação das despesas")
public class TestExpenseService {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private ExpenseMapper expenseMapper;

    @InjectMocks
    private ExpenseService expenseService;

    private User testUser;
    private User testUser2;

    private Expense testExpense;
    private Expense testExpense2;


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        RegisterUserDto registerUserDto = new RegisterUserDto();
        registerUserDto.setEmail("testuser1@test.com");
        registerUserDto.setPassword("password");

        testUser = new User();
        testUser.setEmail(registerUserDto.getEmail());
        testUser.setId(1);
        testUser.setPassword(registerUserDto.getPassword());

        RegisterUserDto registerUserDto2 = new RegisterUserDto();
        registerUserDto2.setEmail("testuser2@test.com");
        registerUserDto2.setPassword("password2");

        testUser2 = new User();
        testUser2.setEmail(registerUserDto.getEmail());
        testUser2.setId(2);
        testUser2.setPassword(registerUserDto.getPassword());

        testExpense = new Expense();
        testExpense.setUser(testUser);
        testExpense.setId(1);
        testExpense.setDescription("Almoço no restaurante");
        testExpense.setPrice(new BigDecimal("35.50"));
        testExpense.setCategory(Category.ALIMENTACAO);
        testExpense.setPaymentMethod(PaymentMethod.CARTAOCREDITO);
        testExpense.setDate(Date.valueOf(LocalDate.now()));
        testExpense.setObservation("Alimentação para o trabalho");

        testExpense2 = new Expense();
        testExpense2.setUser(testUser);
        testExpense2.setId(2);
        testExpense2.setDescription("Compra no supermercado");
        testExpense2.setPrice(new BigDecimal("120.75"));
        testExpense2.setCategory(Category.ALIMENTACAO);
        testExpense2.setPaymentMethod(PaymentMethod.DINHEIRO);
        testExpense2.setDate(Date.valueOf(LocalDate.now().minusDays(3)));
        testExpense2.setObservation("Compra mensal");

        ExpenseDto expenseDto = new ExpenseDto();
        expenseDto.setId(1L);
        expenseDto.setDescription(testExpense.getDescription());
        expenseDto.setPrice(testExpense.getPrice());
        expenseDto.setCategory(testExpense.getCategory());
        expenseDto.setPaymentMethod(testExpense.getPaymentMethod());
        expenseDto.setDate(testExpense.getDate());
        expenseDto.setObservation(testExpense.getObservation());

        ExpenseDto expenseDto2 = new ExpenseDto();
        expenseDto2.setId(2L);
        expenseDto2.setDescription(testExpense2.getDescription());
        expenseDto2.setPrice(testExpense2.getPrice());
        expenseDto2.setCategory(testExpense2.getCategory());
        expenseDto2.setPaymentMethod(testExpense2.getPaymentMethod());
        expenseDto2.setDate(testExpense2.getDate());
        expenseDto2.setObservation(testExpense2.getObservation());

        lenient().when(expenseRepository.findByUser(eq(testUser))).thenReturn(List.of(testExpense));
        lenient().when(expenseMapper.toDto(any(Expense.class))).thenAnswer(invocation -> {
            Expense expense = invocation.getArgument(0);
            log.info("Convertendo Expense para ExpenseDto: {}", expense);

            ExpenseDto dto = new ExpenseDto();
            dto.setId(Long.valueOf(expense.getId()));
            dto.setDescription(expense.getDescription());
            dto.setPrice(expense.getPrice());
            dto.setCategory(expense.getCategory());
            dto.setPaymentMethod(expense.getPaymentMethod());
            dto.setDate(expense.getDate());
            dto.setObservation(expense.getObservation());

            return dto;
        });
        lenient().when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(expenseRepository.findById(eq(1L))).thenReturn(Optional.of(testExpense));  // Garantir que a despesa com ID 1L seja encontrada

    }

    @Test
    @DisplayName("Buscar despesas do usuário")
    public void buscarDadosDespesas() {
        List<ExpenseDto> expenses = expenseService.getAllExpenses(testUser);
        verify(expenseRepository).findByUser(testUser);
        log.info("Despesas retornadas: {}", expenses);
        assertNotNull(expenses);
        assertEquals(1, expenses.size());
        assertEquals("Almoço no restaurante", expenses.getFirst().getDescription());
    }

    @Test
    @DisplayName("Buscar uma despesa específica do usuário")
    public void buscarDadosDespesasUsuario() {
        ExpenseDto result = expenseService.getExpense(Long.valueOf(testUser.getId()), testUser);
        verify(expenseRepository).findById(eq(Long.valueOf(testUser.getId())));
        log.info("Despesa retornada: {}", result);


        assertNotNull(result);
        assertEquals("Almoço no restaurante", result.getDescription());
    }

    @Test
    @DisplayName("Erro ao acessar despesa de outro usuário")
    public void erroDeAcessoDespesas() {
        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> expenseService.getExpense(Long.valueOf(testUser.getId()), testUser2));

        assertEquals("Acesso negado: a despesa não pertence ao usuário.", exception.getMessage());
    }

    @Test
    @DisplayName("Erro ao buscar despesa inexistente")
    public void despesaNaoEncontrada() {
        when(expenseRepository.findById(40L)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> expenseService.getExpense(40L, testUser));
        assertEquals("Despesa não encontrada", exception.getMessage());
    }

    @Test
    @DisplayName("Criar despesa com sucesso")
    public void criarDespesaComSucesso() {
        Expense newExpense = new Expense();
        newExpense.setUser(testUser);
        newExpense.setDescription("Café da manhã");
        newExpense.setPrice(new BigDecimal("15.00"));
        newExpense.setCategory(Category.ALIMENTACAO);
        newExpense.setPaymentMethod(PaymentMethod.PIX);
        newExpense.setDate(Date.valueOf(LocalDate.now()));
        newExpense.setObservation("Café na padaria");

        when(expenseRepository.save(any(Expense.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        Expense savedExpense = expenseService.saveExpense(testUser, newExpense);


        assertNotNull(savedExpense);
        assertEquals("Café da manhã", savedExpense.getDescription());

        verify(expenseRepository, times(1)).save(any(Expense.class));

    }

    @Test
    @DisplayName("Erro ao criar despesa sem usuário")
    public void criarDespesaSemUsuario() {
        Expense novaDespesa = new Expense();
        novaDespesa.setDescription("Compra de mercado");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> expenseService.saveExpense(null, novaDespesa));

        assertEquals("O usuario nao pode ser invalido", exception.getMessage());
    }

    @Test
    @DisplayName("Erro ao criar despesa com valor negativo")
    public void criarDespesaComValorNegativo() {
        Expense novaDespesa = new Expense();
        novaDespesa.setPrice(new BigDecimal("-150.00"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> expenseService.saveExpense(testUser, novaDespesa));

        assertEquals("O preço não pode ser negativo", exception.getMessage());
    }

    @Test
    @DisplayName("Erro ao criar despesa com campos inválidos")
    public void criarDespesaComValorInvalido() {
        Expense novaDespesa = new Expense();
        novaDespesa.setCategory(null);
        novaDespesa.setDescription("");
        novaDespesa.setDate(null);
        novaDespesa.setPaymentMethod(null);
        novaDespesa.setPrice(new BigDecimal("150.00"));

        MissingRequiredFieldException exception = assertThrows(MissingRequiredFieldException.class,
                () -> expenseService.saveExpense(testUser, novaDespesa));

        assertEquals("Os campos devem ser preenchidos corretamente", exception.getMessage());
    }

    @Test
    @DisplayName("Atualizar despesa existente")
    public void atualizarExpense() {
        testExpense.setDescription("Almoço no restaurante");
        testExpense.setPrice(new BigDecimal("50.00"));
        testExpense.setObservation("Almoço de negócios");

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(testExpense));

        ExpenseDto novaDespesa = new ExpenseDto();
        novaDespesa.setDescription("Compra de mercado");
        novaDespesa.setPrice(new BigDecimal("150.00"));
        novaDespesa.setObservation("Compras do mês");
        expenseService.updateExpense(1L, testUser, novaDespesa);

        assertEquals("Compras do mês", testExpense.getObservation());
        assertEquals("150.00", testExpense.getPrice().toString());
        verify(expenseRepository, times(1)).findById(1L);
    }


    @Test
    @DisplayName("Excluir despesa existente")
    public void deleteExpense() {
        doNothing().when(expenseRepository).delete(any(Expense.class));
        expenseService.deleteExpense(testUser, Long.valueOf(testUser.getId()));
        verify(expenseRepository, times(1)).delete(any(Expense.class));
    }

    @Test
    @DisplayName("Deve lançar uma exception de acesso negado")
    public void deleteAcessoNegado() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> expenseService.deleteExpense(testUser2, 1L));
        assertEquals("Acesso negado: a despesa não pertence ao usuário.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar despesas dentro do intervalo de datas")
    public void testGetExpenseForDateRange() {
        Date startDate = Date.valueOf("2024-01-01");
        Date endDate = Date.valueOf("2024-03-01");

        when(expenseRepository.findAllByDateBetween(startDate, endDate, testUser))
                .thenReturn(Arrays.asList(testExpense, testExpense2));

        List<ExpenseDto> result = expenseService.getExpenseForDateRange(startDate, endDate, testUser);
        log.info("resultado encontrado com intervalo de datas {}" ,result.toString());
        assertEquals(2, result.size());
        verify(expenseRepository, times(1)).findAllByDateBetween(startDate, endDate, testUser);
    }

    @Test
    @DisplayName("Deve retornar despesas de um dia específico")
    public void testGetExpenseForDate() {
        Date date = new Date(Date.valueOf(LocalDate.now()).getTime());

        when(expenseRepository.findByDateAndUser(date, testUser)).thenReturn(Arrays.asList(testExpense));

        List<ExpenseDto> result = expenseService.getExpenseForDate(date, testUser);
        log.info("resultado encontrado com uma data {}" ,result.toString());

        assertEquals(1, result.size());
        verify(expenseRepository, times(1)).findByDateAndUser(date, testUser);
    }

    @Test
    @DisplayName("Deve retornar despesas dos últimos três meses")
    public void testGetExpensesLastThreeMonths() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -3);
        Date threeMonthsAgo = new Date(Date.valueOf(LocalDate.now()).getTime());

        when(expenseRepository.findByDateAfterAndUser(any(Date.class), eq(testUser)))
                .thenReturn(Arrays.asList(testExpense, testExpense2));

        List<Expense> result = expenseService.getExpensesLastThreeMonths(testUser);
        log.info("resultado encontrado nos ultimos 3 meses {}" ,result.toString());

        assertEquals(2, result.size());
        verify(expenseRepository, times(1)).findByDateAfterAndUser(any(Date.class), eq(testUser));
    }

    @Test
    @DisplayName("Deve filtrar despesas por categoria")
    public void testGetExpenseFilterCategory() {
        when(expenseRepository.findFilterByCategory(Category.ALIMENTACAO, testUser)).thenReturn(Arrays.asList(testExpense));

        List<ExpenseDto> result = expenseService.getExpenseFilterCategory("ALIMENTACAO", testUser);
        log.info("resultado encontrado no filtro de alimentação {}" ,result.toString());

        assertEquals(1, result.size());
        verify(expenseRepository, times(1)).findFilterByCategory(Category.ALIMENTACAO, testUser);
    }
}
