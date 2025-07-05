package com.example.finance_tracker.Service;

import com.example.finance_tracker.Controller.ReportController;
import com.example.finance_tracker.Entity.Report;
import com.example.finance_tracker.Entity.Transaction;
import com.example.finance_tracker.Repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private TransactionRepository transactionRepository;

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    public Report generateReport(String userId, LocalDateTime startDate, LocalDateTime endDate, List<Transaction.TransactionCategory> categories, List<String> tags) {
        // Fetch transactions based on filters

        // If categories is null, fetch all categories
        if (categories == null) {
            categories = List.of(Transaction.TransactionCategory.values());
        }
        // Convert enum values to strings
        List<String> categoryStrings = categories.stream()
                .map(Enum::name) // Convert enum to string
                .collect(Collectors.toList());
        // If tags is null, use an empty list
        if (tags == null) {
            tags = List.of();
        }

        List<Transaction> transactions = transactionRepository.findByUserIdAndTransactionDateBetweenAndTransactionCategoryInAndTagsIn(
                userId, startDate, endDate, categoryStrings, tags);

        // Calculate total income and expenses
        BigDecimal totalIncome = transactions.stream()
                .filter(t -> Transaction.TransactionType.INCOME.equals(t.getTransactionType())) // Compare to enum value
                .map(t -> new BigDecimal(String.valueOf(t.getTransactionAmount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = transactions.stream()
                .filter(t -> Transaction.TransactionType.EXPENSE.equals(t.getTransactionType())) // Compare to enum value
                .map(t -> new BigDecimal(String.valueOf(t.getTransactionAmount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Group transactions by category
        Map<Transaction.TransactionCategory, BigDecimal> categoryTotals = transactions.stream()
                .filter(t -> Transaction.TransactionType.EXPENSE.equals(t.getTransactionType())) // Compare to enum value
                .collect(Collectors.groupingBy(
                        Transaction::getTransactionCategory,
                        Collectors.reducing(BigDecimal.ZERO, t -> new BigDecimal(String.valueOf(t.getTransactionAmount())), BigDecimal::add)
                ));

        // Build the report
        Report report = new Report();
        report.setTotalIncome(totalIncome);
        report.setTotalExpenses(totalExpenses);
        report.setCategoryTotals(categoryTotals);

        return report;
    }

    public byte[] generateCsvReport(Report report) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(outputStream)) {

            // Write CSV header
            writer.println("Total Income,Total Expenses,Category,Amount");

            // Write total income and expenses
            writer.println(report.getTotalIncome() + "," + report.getTotalExpenses() + ",,");

            // Write category-wise totals
            for (Map.Entry<Transaction.TransactionCategory, BigDecimal> entry : report.getCategoryTotals().entrySet()) {
                writer.println(",," + entry.getKey() + "," + entry.getValue());
            }

            writer.flush();
            return outputStream.toByteArray();

        } catch (IOException e) {
            logger.error("Error generating CSV report", e);
            throw new RuntimeException("Error generating CSV report", e);
        }
    }
}
