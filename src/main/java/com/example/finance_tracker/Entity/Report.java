package com.example.finance_tracker.Entity;

import java.math.BigDecimal;
import java.util.Map;

public class Report {
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private Map<Transaction.TransactionCategory, BigDecimal> categoryTotals; // Category-wise totals

    // Getters and setters
    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public Map<Transaction.TransactionCategory, BigDecimal> getCategoryTotals() {
        return categoryTotals;
    }

    public void setCategoryTotals(Map<Transaction.TransactionCategory, BigDecimal> categoryTotals) {
        this.categoryTotals = categoryTotals;
    }
}