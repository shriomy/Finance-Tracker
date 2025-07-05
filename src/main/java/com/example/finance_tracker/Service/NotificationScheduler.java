package com.example.finance_tracker.Service;

import com.example.finance_tracker.Entity.Transaction;
import com.example.finance_tracker.Entity.Goal.Goal;
import com.example.finance_tracker.Entity.Goal.GoalStatus;
import com.example.finance_tracker.Repository.TransactionRepository;
import com.example.finance_tracker.Repository.GoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class NotificationScheduler {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private NotificationService notificationService;

    // Run this task every day at 8 AM
    @Scheduled(cron = "0 0 8 * * ?")
    public void checkForNotifications() {
        checkUnusualSpending();
        checkUpcomingDeadlines();
    }

    // Check for unusual spending patterns
    private void checkUnusualSpending() {
        List<Transaction> transactions = transactionRepository.findAll();
        for (Transaction transaction : transactions) {
            double averageSpending = calculateAverageSpending(transaction.getUserId(), transaction.getTransactionCategory());
            if (transaction.getTransactionAmount().doubleValue() > 1.5 * averageSpending) {
                String message = "Unusual spending detected in category: " + transaction.getTransactionCategory() +
                        ". You spent " + transaction.getTransactionAmount() + ", which is higher than your average spending.";
                notificationService.sendEmailNotification(transaction.getUserId(), "Unusual Spending Alert", message);
            }
        }
    }

    // Check for upcoming deadlines (e.g., financial goals)
    private void checkUpcomingDeadlines() {
        List<Goal> goals = goalRepository.findAll();
        LocalDate today = LocalDate.now();
        for (Goal goal : goals) {
            if (goal.getEndDate().isBefore(today.plusDays(7)) && !goal.getStatus().equals(GoalStatus.COMPLETED)) {
                String message = "Your financial goal '" + goal.getGoalName() + "' is due in 7 days. You have saved " +
                        goal.getSavedAmount() + " out of " + goal.getTargetAmount() + ".";
                notificationService.sendEmailNotification(goal.getUserId(), "Upcoming Goal Deadline", message);
            }
        }
    }

    // Helper method to calculate average spending for a category
    private double calculateAverageSpending(String userId, Transaction.TransactionCategory category) {
        List<Transaction> transactions = transactionRepository.findByUserIdAndTransactionCategory(userId, category);
        if (transactions.isEmpty()) {
            return 0.0; // Return 0 if no transactions exist for the category
        }
        double totalSpending = transactions.stream()
                .mapToDouble(t -> t.getTransactionAmount().doubleValue())
                .sum();
        return totalSpending / transactions.size();
    }
}