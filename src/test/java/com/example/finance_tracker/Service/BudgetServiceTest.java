package com.example.finance_tracker.Service;

import com.example.finance_tracker.Entity.Budget;
import com.example.finance_tracker.Repository.BudgetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @InjectMocks
    private BudgetService budgetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    void testCreateOrUpdateBudget() {
        // Arrange
        Budget budget = new Budget();
        budget.setUserId("user123");
        budget.setBudgetCategory("FOOD");
        budget.setAmount(BigDecimal.valueOf(500.00));
        budget.setCurrentSpentAmount(BigDecimal.ZERO);

        when(budgetRepository.save(any(Budget.class))).thenReturn(budget);

        // Act
        Budget createdBudget = budgetService.createOrUpdateBudget(budget);

        // Assert
        assertNotNull(createdBudget);
        assertEquals("user123", createdBudget.getUserId());
        assertEquals("FOOD", createdBudget.getBudgetCategory());
        assertEquals(BigDecimal.valueOf(500.00), createdBudget.getAmount());
        verify(budgetRepository, times(1)).save(budget);
    }

    @Test
    void testGetAllBudgets() {
        // Arrange
        Budget budget1 = new Budget();
        budget1.setUserId("user123");
        Budget budget2 = new Budget();
        budget2.setUserId("user456");

        when(budgetRepository.findAll()).thenReturn(Arrays.asList(budget1, budget2));

        // Act
        List<Budget> budgets = budgetService.getAllBudgets();

        // Assert
        assertEquals(2, budgets.size());
        verify(budgetRepository, times(1)).findAll();
    }

    @Test
    void testGetBudgetsByUserId() {
        // Arrange
        String userId = "user123";
        Budget budget1 = new Budget();
        budget1.setUserId(userId);
        Budget budget2 = new Budget();
        budget2.setUserId(userId);

        when(budgetRepository.findByUserId(userId)).thenReturn(Arrays.asList(budget1, budget2));

        // Act
        List<Budget> budgets = budgetService.getBudgetsByUserId(userId);

        // Assert
        assertEquals(2, budgets.size());
        verify(budgetRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testGetBudgetById() {
        // Arrange
        String budgetId = "12345";
        Budget budget = new Budget();
        budget.setId(budgetId);
        budget.setUserId("user123");

        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(budget));

        // Act
        Budget foundBudget = budgetService.getBudgetById(budgetId);

        // Assert
        assertNotNull(foundBudget);
        assertEquals(budgetId, foundBudget.getId());
        verify(budgetRepository, times(1)).findById(budgetId);
    }

    @Test
    void testGetBudgetById_NotFound() {
        // Arrange
        String budgetId = "12345";
        when(budgetRepository.findById(budgetId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> budgetService.getBudgetById(budgetId));
        verify(budgetRepository, times(1)).findById(budgetId);
    }

    @Test
    void testUpdateBudget() {
        // Arrange
        String budgetId = "12345";
        Budget existingBudget = new Budget();
        existingBudget.setId(budgetId);
        existingBudget.setUserId("user123");

        Budget updatedBudget = new Budget();
        updatedBudget.setBudgetCategory("TRANSPORT");
        updatedBudget.setAmount(BigDecimal.valueOf(300.00));
        updatedBudget.setStartDate(LocalDate.parse("2023-01-01"));
        updatedBudget.setEndDate(LocalDate.parse("2023-12-31"));
        updatedBudget.setCurrentSpentAmount(BigDecimal.valueOf(100.00));

        when(budgetRepository.findById(budgetId)).thenReturn(Optional.of(existingBudget));
        when(budgetRepository.save(any(Budget.class))).thenReturn(updatedBudget);

        // Act
        Budget result = budgetService.updateBudget(budgetId, updatedBudget);

        // Assert
        assertNotNull(result);
        assertEquals("TRANSPORT", result.getBudgetCategory());
        assertEquals(BigDecimal.valueOf(300.00), result.getAmount());
        verify(budgetRepository, times(1)).findById(budgetId);
        verify(budgetRepository, times(1)).save(existingBudget);
    }

    @Test
    void testDeleteBudget() {
        // Arrange
        String budgetId = "12345";
        when(budgetRepository.existsById(budgetId)).thenReturn(true);

        // Act
        budgetService.deleteBudget(budgetId);

        // Assert
        verify(budgetRepository, times(1)).deleteById(budgetId);
    }

    /*
    @Test
    void testDeleteBudget_NotFound() {
        // Arrange
        String budgetId = "12345";
        when(budgetRepository.existsById(budgetId)).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> budgetService.deleteBudget(budgetId));
        verify(budgetRepository, times(1)).existsById(budgetId);
    }

     */

    @Test
    void testIsExceedingBudget_True() {
        // Arrange
        Budget budget = new Budget();
        budget.setAmount(BigDecimal.valueOf(500.00));
        budget.setCurrentSpentAmount(BigDecimal.valueOf(450.00));

        BigDecimal newExpense = BigDecimal.valueOf(100.00);

        // Act
        boolean isExceeding = budgetService.isExceedingBudget(budget, newExpense);

        // Assert
        assertTrue(isExceeding);
    }

    @Test
    void testIsExceedingBudget_False() {
        // Arrange
        Budget budget = new Budget();
        budget.setAmount(BigDecimal.valueOf(500.00));
        budget.setCurrentSpentAmount(BigDecimal.valueOf(400.00));

        BigDecimal newExpense = BigDecimal.valueOf(50.00);

        // Act
        boolean isExceeding = budgetService.isExceedingBudget(budget, newExpense);

        // Assert
        assertFalse(isExceeding);
    }
}