package com.example.finance_tracker.Service;

import com.example.finance_tracker.Entity.Transaction;
import com.example.finance_tracker.Repository.TransactionRepository;
import com.example.finance_tracker.Entity.Report;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.example.finance_tracker.Entity.Budget;
import com.example.finance_tracker.Entity.Goal.Goal;
import com.example.finance_tracker.Entity.Goal.GoalStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BudgetService budgetService;
    private final GoalService goalService;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, BudgetService budgetService, GoalService goalService) {
        this.transactionRepository = transactionRepository;
        this.budgetService = budgetService;
        this.goalService = goalService;
    }

    // ✅ logging to track important events
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class); // new change

    // ✅ Create a new transaction with error handling
    // ✅ Ensures required fields are present.
    public Transaction createTransaction(Transaction transaction) {
        logger.info("Creating transaction for user: {}", transaction.getUserId()); // new change
        validateTransaction(transaction);  // Validate transaction fields

        if (transaction.getRecurring() == null) {
            transaction.setRecurring(false); // Default to false if not provided
        }
        // Save the transaction
        Transaction createdTransaction = transactionRepository.save(transaction);

        // If the transaction is an expense, update the corresponding budget
        if (transaction.getTransactionType() == Transaction.TransactionType.EXPENSE) {
            updateBudgetForExpense(createdTransaction);
        }
        // If the transaction is an income, update the user's goals
        if (transaction.getTransactionType() == Transaction.TransactionType.INCOME) {
            updateGoalsForIncome(createdTransaction);
        }
        return createdTransaction;
    }

    // Helper method to update the budget when an expense is created
    private void updateBudgetForExpense(Transaction expenseTransaction) {
        // Find the budget for the user and category
        List<Budget> budgets = budgetService.getBudgetsByUserId(expenseTransaction.getUserId());

        // Find the budget that matches the transaction category
        Optional<Budget> matchingBudget = budgets.stream()
                .filter(budget -> budget.getBudgetCategory().equals(expenseTransaction.getTransactionCategory().toString()))
                .findFirst();

        if (matchingBudget.isPresent()) {
            Budget budget = matchingBudget.get();
            // Update the currentSpentAmount by adding the expense amount
            BigDecimal newSpentAmount = budget.getCurrentSpentAmount().add(expenseTransaction.getTransactionAmount());
            budget.setCurrentSpentAmount(newSpentAmount);

            // Save the updated budget
            budgetService.createOrUpdateBudget(budget);
        }
    }

    // Helper method to update the goals when an income is created
    private void updateGoalsForIncome(Transaction incomeTransaction) {
        // Get all goals for the user
        List<Goal> userGoals = goalService.getUserGoals(incomeTransaction.getUserId());

        // Add the income amount to the savedAmount of each goal
        for (Goal goal : userGoals) {
            double newSavedAmount = goal.getSavedAmount() + incomeTransaction.getTransactionAmount().doubleValue();
            goal.setSavedAmount(newSavedAmount);

            // Update the goal status if necessary
            if (newSavedAmount >= goal.getTargetAmount()) {
                goal.setStatus(GoalStatus.COMPLETED);
            } else {
                goal.setStatus(GoalStatus.IN_PROGRESS);
            }

            // Save the updated goal
            goalService.updateGoal(goal.getId(), goal);
        }
    }

    // ✅ Get all transactions by admin
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // ✅ Get transaction by ID
    public Transaction getTransactionById(String transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionId));
    }

    // ✅ Get transaction of a user by ID
    public List<Transaction> getTransactionsByUserId(String userId) {
        if (!StringUtils.hasText(userId)) {
            throw new IllegalArgumentException("User ID is required.");
        }
        return transactionRepository.findByUserId(userId);
    }

    // ✅ Get transactions of a user by ID and category
    public List<Transaction> getTransactionsByUserIdAndCategory(String userId, Transaction.TransactionCategory category) {
        if (!StringUtils.hasText(userId)) {
            throw new IllegalArgumentException("User ID is required.");
        }
        if (category == null) {
            throw new IllegalArgumentException("Category is required.");
        }

        return transactionRepository.findByUserIdAndTransactionCategory(userId, category);
    }

    // ✅ update a transaction
    public Transaction updateTransaction(String transactionId, Transaction updatedTransaction) {
        validateTransaction(updatedTransaction);  // Validate input

        return transactionRepository.findById(transactionId)
                .map(existingTransaction -> {
                    existingTransaction.setUserId(updatedTransaction.getUserId());
                    existingTransaction.setTransactionType(updatedTransaction.getTransactionType());
                    existingTransaction.setTransactionCategory(updatedTransaction.getTransactionCategory());
                    existingTransaction.setTransactionDate(updatedTransaction.getTransactionDate());
                    existingTransaction.setTags(updatedTransaction.getTags());
                    existingTransaction.setRecurring(updatedTransaction.getRecurring() != null ? updatedTransaction.getRecurring() : false);
                    existingTransaction.setRecurrencePattern(updatedTransaction.getRecurrencePattern());
                    existingTransaction.setTransactionAmount(updatedTransaction.getTransactionAmount());
                    existingTransaction.setTransactionDescription(updatedTransaction.getTransactionDescription());
                    return transactionRepository.save(existingTransaction);
                })
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionId));
    }

    // ✅ delete a transaction
    public void deleteTransaction(String transactionId) {
        if (!transactionRepository.existsById(transactionId)) {
            throw new RuntimeException("Transaction not found with ID: " + transactionId);
        }
        transactionRepository.deleteById(transactionId);
    }

    // ✅ validate a transaction with error handling
    private void validateTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null.");
        }
        if (!StringUtils.hasText(transaction.getUserId())) {
            throw new IllegalArgumentException("User ID is required.");
        }
        if (transaction.getTransactionType() == null) {
            throw new IllegalArgumentException("Transaction Type is required.");
        }
        if (transaction.getTransactionCategory() == null) {
            throw new IllegalArgumentException("Transaction Category is required.");
        }
        if (transaction.getTransactionDate() == null) {
            throw new IllegalArgumentException("Transaction Date is required.");
        }
        if (transaction.getTransactionAmount() == null || transaction.getTransactionAmount().doubleValue() <= 0) {
            throw new IllegalArgumentException("Transaction Amount must be greater than zero.");
        }
        if (!StringUtils.hasText(transaction.getTransactionDescription())) {
            throw new IllegalArgumentException("Transaction Description is required.");
        }
        if (Boolean.TRUE.equals(transaction.getRecurring()) && transaction.getRecurrencePattern() == null) {
            throw new IllegalArgumentException("Recurrence Pattern is required for recurring transactions.");
        }
    }

}
