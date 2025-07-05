package com.example.finance_tracker.Entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "transactions")
public class Transaction {

    @Id
    @NotNull(message = "User ID is required")
    private String transactionId;

    @Field(name = "user_id")
    private String userId;

    @Field(name = "transaction_type")
    @NotNull(message = "Transaction type is required")
    private TransactionType transactionType;

    @Field(name = "transaction_category")
    private TransactionCategory transactionCategory;

    @Field(name = "transaction_date")
    private LocalDateTime transactionDate;

    private List<String> tags;

    private Boolean recurring; // Optional, so using Boolean instead of boolean

    @Field(name = "recurrence_pattern")
    private RecurrencePattern recurrencePattern;

    @Field(name = "transaction_amount")
    @NotNull(message = "Transaction amount is required") // new change
    @Min(value = 0, message = "Transaction amount must be greater than or equal to 0") // new change
    private BigDecimal transactionAmount;

    @Field(name = "transaction_description")
    private String transactionDescription;

    @CreatedDate
    @Field(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field(name = "updated_at")
    private LocalDateTime updatedAt;

    public Transaction() {
    }

    //constructor
    public Transaction(String userId, TransactionType transactionType, TransactionCategory transactionCategory,
                       LocalDateTime transactionDate, BigDecimal transactionAmount, String transactionDescription) {
        this.userId = userId;
        this.transactionType = transactionType;
        this.transactionCategory = transactionCategory;
        this.transactionDate = transactionDate;
        this.transactionAmount = transactionAmount;
        this.transactionDescription = transactionDescription;
    }

    // Getters and Setters
    public String getTransactionId() {
        return transactionId;
    }
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }
    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public TransactionCategory getTransactionCategory() {
        return transactionCategory;
    }
    public void setTransactionCategory(TransactionCategory transactionCategory) {
        this.transactionCategory = transactionCategory;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }
    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public List<String> getTags() {
        return tags;
    }
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Boolean getRecurring() {
        return recurring;
    }
    public void setRecurring(Boolean recurring) {
        this.recurring = recurring;
    }

    public RecurrencePattern getRecurrencePattern() {
        return recurrencePattern;
    }
    public void setRecurrencePattern(RecurrencePattern recurrencePattern) {
        this.recurrencePattern = recurrencePattern;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }
    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getTransactionDescription() {
        return transactionDescription;
    }
    public void setTransactionDescription(String transactionDescription) {
        this.transactionDescription = transactionDescription;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Enums
    public enum TransactionType {
        INCOME, EXPENSE
    }

    public enum TransactionCategory {
        FOOD, TRANSPORT, ENTERTAINMENT, SALARY
    }

    public enum RecurrencePattern {
        DAILY, WEEKLY, MONTHLY
    }

    //Override the toString() method for better debugging
    @Override
    public String toString() { //new change this method
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", userId='" + userId + '\'' +
                ", transactionType=" + transactionType +
                ", transactionCategory=" + transactionCategory +
                ", transactionDate=" + transactionDate +
                ", transactionAmount=" + transactionAmount +
                ", transactionDescription='" + transactionDescription + '\'' +
                '}';
    }
}
