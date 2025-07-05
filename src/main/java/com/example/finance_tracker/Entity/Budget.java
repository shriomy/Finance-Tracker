package com.example.finance_tracker.Entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDate;

@Document(collection = "budgets")
public class Budget {
        @Id
        @NotNull(message = "User ID is required")
        private String id;

        @Field(name = "user_id")
        private String userId;

        @Field(name = "budget_category")
        private String budgetCategory;

        @Field(name = "budget_amount")
        @NotNull(message = "budget amount is required")
        @Min(value = 0, message = "budget amount must be greater than or equal to 0")
        private BigDecimal amount;

        @Field(name = "spent_amount")
        private BigDecimal currentSpentAmount;

        @Field(name = "start_date")
        private LocalDate startDate;

        @Field(name = "end_date")
        private LocalDate endDate;

        public Budget(){}

        // Constructor
        public Budget(String userId, String budgetCategory, BigDecimal amount, BigDecimal currentSpentAmount, LocalDate startDate, LocalDate endDate) {
                this.userId = userId;
                this.budgetCategory = budgetCategory;
                this.amount = amount;
                this.currentSpentAmount = currentSpentAmount;
                this.startDate = startDate;
                this.endDate = endDate;
        }

        // Getters & Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getBudgetCategory() { return budgetCategory; }
        public void setBudgetCategory(String budgetCategory) { this.budgetCategory = budgetCategory; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public BigDecimal getCurrentSpentAmount() { return currentSpentAmount; }
        public void setCurrentSpentAmount(BigDecimal spentAmount) { this.currentSpentAmount = spentAmount; }

        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

        public enum BudgetCategory {
                FOOD, TRANSPORT, ENTERTAINMENT, SALARY
        }

        //Override the toString() method for better debugging
        @Override
        public String toString() { //new change this method
                return "Budget{" +
                        "budgetId='" + id + '\'' +
                        ", userId='" + userId + '\'' +
                        ", budgetCategory=" + budgetCategory +
                        ", amount=" + amount +
                        ", currentSpentAmount=" + currentSpentAmount +
                        ", startDate=" + startDate +
                        ", endDate=" + endDate +
                        '}';
        }
}


