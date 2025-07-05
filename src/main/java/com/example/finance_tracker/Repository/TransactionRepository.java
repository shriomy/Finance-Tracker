package com.example.finance_tracker.Repository;

import com.example.finance_tracker.Entity.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {

    // Find all transactions by user ID
    List<Transaction> findByUserId(String userId);

    // Find transactions by type (INCOME or EXPENSE)
    List<Transaction> findByTransactionType(Transaction.TransactionType transactionType);

    // Find transactions by category (FOOD, TRANSPORT, ENTERTAINMENT)
    List<Transaction> findByTransactionCategory(Transaction.TransactionCategory transactionCategory);

    // Find recurring transactions
    List<Transaction> findByRecurringTrue();

    //for filtering transactions by date range, amount range, etc // new change
    List<Transaction> findByUserIdAndTransactionDateBetween(String userId, LocalDateTime startDate, LocalDateTime endDate);

    List<Transaction> findByUserIdAndTransactionAmountBetween(String userId, BigDecimal minAmount, BigDecimal maxAmount);

    // Find transactions by user ID and category
    List<Transaction> findByUserIdAndTransactionCategory(String userId, Transaction.TransactionCategory transactionCategory);

    @Query(value = "{ 'user_id': ?0, " +
            "'transaction_date': { $gte: ?1, $lte: ?2 }, " +
            "$and: [ " +
            "   { $or: [ { 'transaction_category': { $in: ?3 } }, { 'transaction_category': { $exists: false } } ] }, " +
            "   { $or: [ { 'tags': { $in: ?4 } }, { 'tags': { $exists: false } } ] } " +
            "] }")
    List<Transaction> findByUserIdAndTransactionDateBetweenAndTransactionCategoryInAndTagsIn(
            String userId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            List<String> categories,
            List<String> tags);
}

