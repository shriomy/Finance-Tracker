package com.example.finance_tracker.Service;

import com.example.finance_tracker.Entity.Budget;
import com.example.finance_tracker.Repository.BudgetRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class BudgetService {
    private final BudgetRepository budgetRepository;

    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    // ✅ Create or Update Budget (Now handles spentAmount)
    public Budget createOrUpdateBudget(Budget budget) {
        if (budget.getCurrentSpentAmount() == null) {
            budget.setCurrentSpentAmount(BigDecimal.ZERO); // Ensure spentAmount is initialized
        }
        return budgetRepository.save(budget);
    }

    // ✅ Get all budgets by admin
    public List<Budget> getAllBudgets() {return budgetRepository.findAll();
    }

    // ✅ Get all budgets for a user
    public List<Budget> getBudgetsByUserId(String userId) {
        return budgetRepository.findByUserId(userId);
    }

    // ✅ Get budget by ID
    public Budget getBudgetById(String id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found"));
    }

    // ✅ Update budget (Now updates spentAmount)
    public Budget updateBudget(String id, Budget budgetDetails) {
        Budget budget = getBudgetById(id);

        budget.setBudgetCategory(budgetDetails.getBudgetCategory());
        budget.setAmount(budgetDetails.getAmount());
        budget.setStartDate(budgetDetails.getStartDate());
        budget.setEndDate(budgetDetails.getEndDate());
        budget.setCurrentSpentAmount(budgetDetails.getCurrentSpentAmount());

        return budgetRepository.save(budget);
    }

    // ✅ Delete a budget
    public void deleteBudget(String id) {
        budgetRepository.deleteById(id);
    }

    // ✅ Check if user is exceeding budget (Now updates spentAmount)
    public boolean isExceedingBudget(Budget budget, BigDecimal newExpense) {
        BigDecimal newTotal = budget.getCurrentSpentAmount().add(newExpense);
        return newTotal.compareTo(budget.getAmount()) > 0;
    }
}


