package com.example.finance_tracker.Service;

import com.example.finance_tracker.Entity.Transaction;
import com.example.finance_tracker.Entity.Budget;
import com.example.finance_tracker.Entity.Goal.Goal;
import com.example.finance_tracker.Entity.Goal.GoalStatus;
import com.example.finance_tracker.Repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BudgetService budgetService;

    @Mock
    private GoalService goalService;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    void testCreateTransaction() {
        // Arrange
        Transaction transaction = new Transaction();
        transaction.setUserId("user123");
        transaction.setTransactionType(Transaction.TransactionType.EXPENSE);
        transaction.setTransactionCategory(Transaction.TransactionCategory.FOOD);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setTransactionAmount(BigDecimal.valueOf(50.00));
        transaction.setTransactionDescription("Lunch");

        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // Act
        Transaction createdTransaction = transactionService.createTransaction(transaction);

        // Assert
        assertNotNull(createdTransaction);
        assertEquals("user123", createdTransaction.getUserId());
        assertEquals(Transaction.TransactionType.EXPENSE, createdTransaction.getTransactionType());
        verify(transactionRepository, times(1)).save(transaction);
        verify(budgetService, times(1)).getBudgetsByUserId("user123");
    }

    @Test
    void testGetAllTransactions() {
        // Arrange
        Transaction transaction1 = new Transaction();
        transaction1.setUserId("user123");
        Transaction transaction2 = new Transaction();
        transaction2.setUserId("user456");

        when(transactionRepository.findAll()).thenReturn(Arrays.asList(transaction1, transaction2));

        // Act
        List<Transaction> transactions = transactionService.getAllTransactions();

        // Assert
        assertEquals(2, transactions.size());
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    void testGetTransactionById() {
        // Arrange
        String transactionId = "12345";
        Transaction transaction = new Transaction();
        transaction.setTransactionId(transactionId);
        transaction.setUserId("user123");

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        // Act
        Transaction foundTransaction = transactionService.getTransactionById(transactionId);

        // Assert
        assertNotNull(foundTransaction);
        assertEquals(transactionId, foundTransaction.getTransactionId());
        verify(transactionRepository, times(1)).findById(transactionId);
    }

    @Test
    void testGetTransactionById_NotFound() {
        // Arrange
        String transactionId = "12345";
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> transactionService.getTransactionById(transactionId));
        verify(transactionRepository, times(1)).findById(transactionId);
    }

    @Test
    void testGetTransactionsByUserId() {
        // Arrange
        String userId = "user123";
        Transaction transaction1 = new Transaction();
        transaction1.setUserId(userId);
        Transaction transaction2 = new Transaction();
        transaction2.setUserId(userId);

        when(transactionRepository.findByUserId(userId)).thenReturn(Arrays.asList(transaction1, transaction2));

        // Act
        List<Transaction> transactions = transactionService.getTransactionsByUserId(userId);

        // Assert
        assertEquals(2, transactions.size());
        verify(transactionRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testGetTransactionsByUserIdAndCategory() {
        // Arrange
        String userId = "user123";
        Transaction.TransactionCategory category = Transaction.TransactionCategory.FOOD;
        Transaction transaction1 = new Transaction();
        transaction1.setUserId(userId);
        transaction1.setTransactionCategory(category);
        Transaction transaction2 = new Transaction();
        transaction2.setUserId(userId);
        transaction2.setTransactionCategory(category);

        when(transactionRepository.findByUserIdAndTransactionCategory(userId, category))
                .thenReturn(Arrays.asList(transaction1, transaction2));

        // Act
        List<Transaction> transactions = transactionService.getTransactionsByUserIdAndCategory(userId, category);

        // Assert
        assertEquals(2, transactions.size());
        verify(transactionRepository, times(1)).findByUserIdAndTransactionCategory(userId, category);
    }

    @Test
    void testUpdateTransaction() {
        // Arrange
        String transactionId = "12345";
        Transaction existingTransaction = new Transaction();
        existingTransaction.setTransactionId(transactionId);
        existingTransaction.setUserId("user123");

        Transaction updatedTransaction = new Transaction();
        updatedTransaction.setUserId("user456");
        updatedTransaction.setTransactionType(Transaction.TransactionType.INCOME);
        updatedTransaction.setTransactionCategory(Transaction.TransactionCategory.SALARY);
        updatedTransaction.setTransactionDate(LocalDateTime.now());
        updatedTransaction.setTransactionAmount(BigDecimal.valueOf(1000.00));
        updatedTransaction.setTransactionDescription("Salary");

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(existingTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(updatedTransaction);

        // Act
        Transaction result = transactionService.updateTransaction(transactionId, updatedTransaction);

        // Assert
        assertNotNull(result);
        assertEquals("user456", result.getUserId());
        assertEquals(Transaction.TransactionType.INCOME, result.getTransactionType());
        verify(transactionRepository, times(1)).findById(transactionId);
        verify(transactionRepository, times(1)).save(existingTransaction);
    }

    @Test
    void testDeleteTransaction() {
        // Arrange
        String transactionId = "12345";
        when(transactionRepository.existsById(transactionId)).thenReturn(true);

        // Act
        transactionService.deleteTransaction(transactionId);

        // Assert
        verify(transactionRepository, times(1)).deleteById(transactionId);
    }

    @Test
    void testDeleteTransaction_NotFound() {
        // Arrange
        String transactionId = "12345";
        when(transactionRepository.existsById(transactionId)).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> transactionService.deleteTransaction(transactionId));
        verify(transactionRepository, times(1)).existsById(transactionId);
    }

    /*
    @Test
    void testValidateTransaction() {
        // Arrange
        Transaction transaction = new Transaction();
        transaction.setUserId("user123");
        transaction.setTransactionType(Transaction.TransactionType.EXPENSE);
        transaction.setTransactionCategory(Transaction.TransactionCategory.FOOD);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setTransactionAmount(BigDecimal.valueOf(50.00));
        transaction.setTransactionDescription("Lunch");

        // Act & Assert (No exception should be thrown)
        assertDoesNotThrow(() -> transactionService.createTransaction(transaction));
    }

     */

}

    /*
    @Test
    void testValidateTransaction_NullTransaction() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> transactionService.createTransaction(null));
    }

    @Test
    void testValidateTransaction_MissingUserId() {
        // Arrange
        Transaction transaction = new Transaction();
        transaction.setTransactionType(Transaction.TransactionType.EXPENSE);
        transaction.setTransactionCategory(Transaction.TransactionCategory.FOOD);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setTransactionAmount(BigDecimal.valueOf(50.00));
        transaction.setTransactionDescription("Lunch");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> transactionService.createTransaction(transaction));
    }
    */