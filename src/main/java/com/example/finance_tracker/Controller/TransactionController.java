package com.example.finance_tracker.Controller;

import com.example.finance_tracker.Entity.Transaction;
import com.example.finance_tracker.Entity.Report;
import com.example.finance_tracker.Entity.User;
import com.example.finance_tracker.Service.TransactionService;
import com.example.finance_tracker.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    //Constructors
    @Autowired
    public TransactionController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    //Create a new Transaction
    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody Transaction transaction) {

        try {
            // Ensure the authenticated user is creating a transaction for themselves
            boolean isAuthUser = userService.isAuthUser(transaction.getUserId());
            if (!isAuthUser) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to create a transaction for another user.");
            }

            // Proceed with transaction creation
            Transaction createdTransaction = transactionService.createTransaction(transaction);
            return ResponseEntity.ok(createdTransaction);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Returns validation error

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error occurred.");
        }
    }


    //Get all transactions (ADMIN only)
    @GetMapping
    public ResponseEntity<?> getAllTransactions() {

        // Get the authenticated user's details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUsername = authentication.getName();

        // Fetch the user from the database
        User user = userService.getUserByUsername(authenticatedUsername);

        // Check if the user is an ADMIN
        if (!"ADMIN".equals(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access all transactions.");
        }
        // Fetch and return all transactions
        List<Transaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    //Retrieve all transactions for a specific user.
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getTransactionsByUserId(@PathVariable String userId) {

        // Check if the authenticated user matches the requested user
        boolean isAuthUser = userService.isAuthUser(userId);
        if (!isAuthUser) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to view these transactions.");
        }

        List<Transaction> transactions = transactionService.getTransactionsByUserId(userId);
        return ResponseEntity.ok(transactions);
    }


    //Retrieve a transaction by ID.
    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable String id) {

        try {
            // Retrieve the transaction by ID
            Transaction transaction = transactionService.getTransactionById(id);

            // Check if the authenticated user owns the transaction
            boolean isAuthUser = userService.isAuthUser(transaction.getUserId());
            if (!isAuthUser) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to view this transaction.");
            }

            return ResponseEntity.ok(transaction);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage()); // Returns "Transaction not found" message
        }
    }


    // Retrieve all transactions for a specific user, filtered by category
    @GetMapping("/user/{userId}/category/{category}")
    public ResponseEntity<?> getTransactionsByUserIdAndCategory(
            @PathVariable String userId,
            @PathVariable Transaction.TransactionCategory category) {

        // Check if the authenticated user matches the requested user
        boolean isAuthUser = userService.isAuthUser(userId);
        if (!isAuthUser) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to view these transactions.");
        }
        // Fetch transactions by user ID and category
        List<Transaction> transactions = transactionService.getTransactionsByUserIdAndCategory(userId, category);
        return ResponseEntity.ok(transactions);
    }

    //Update an existing transaction.
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(@PathVariable String id, @RequestBody Transaction updatedTransaction) {

        try {
            // Get the existing transaction by ID
            Transaction storedTransaction = transactionService.getTransactionById(id);
            // Check if the authenticated user is the owner of the transaction
            boolean isAuthUser = userService.isAuthUser(storedTransaction.getUserId());
            if (!isAuthUser || !updatedTransaction.getUserId().equals(storedTransaction.getUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to update this transaction.");
            }

            // Ensure the transaction remains linked to the original user
            updatedTransaction.setUserId(storedTransaction.getUserId());

            // Proceed with the update
            Transaction transaction = transactionService.updateTransaction(id, updatedTransaction);
            return ResponseEntity.ok(transaction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Validation error
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // Transaction not found
        }
    }


    //Delete a transaction by ID.
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable String id) {

        try {
            // Retrieve the transaction by ID
            Transaction transaction = transactionService.getTransactionById(id);

            // Check if the authenticated user owns the transaction
            boolean isAuthUser = userService.isAuthUser(transaction.getUserId());

            if (!isAuthUser) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete this transaction.");
            }

            // Proceed with deletion
            transactionService.deleteTransaction(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage()); // Transaction not found
        }
    }

}
