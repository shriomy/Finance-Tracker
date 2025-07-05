package com.example.finance_tracker.Controller;

import com.example.finance_tracker.Entity.Budget;
import com.example.finance_tracker.Entity.User;
import com.example.finance_tracker.Service.BudgetService;
import com.example.finance_tracker.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {
    private final BudgetService budgetService;
    private final UserService userService;

    public BudgetController(BudgetService budgetService, UserService userService) {
        this.budgetService = budgetService;
        this.userService = userService;
    }

    // Create or update a budget
    @PostMapping
    public ResponseEntity<?> createOrUpdateBudget(@RequestBody Budget budget) {

        // Check if the authenticated user is allowed to create or update the budget
        if (!userService.isAuthUser(budget.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // Forbidden if the user is not authorized
        }
        return ResponseEntity.ok(budgetService.createOrUpdateBudget(budget));
    }

    //get all budgets by ADMIN
    @GetMapping
    public ResponseEntity<?> getAllBudgets() {

        // Get the authenticated user's details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUsername = authentication.getName();

        // Fetch the user from the database
        User user = userService.getUserByUsername(authenticatedUsername);

        // Check if the user is an ADMIN
        if (!"ADMIN".equals(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access all budgets.");
        }

        // Fetch and return all budgets if the user is an ADMIN
        List<Budget> budgets = budgetService.getAllBudgets();
        return ResponseEntity.ok(budgets);
    }


    // Get all budgets for a user
    @GetMapping("user/{userId}")
    public ResponseEntity<List<Budget>> getBudgetsByUserId(@PathVariable String userId) {

        // Check if the authenticated user is allowed to access the budgets
        if (!userService.isAuthUser(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // Forbidden if the user is not authorized
        }

        // Proceed with fetching the budgets
        return ResponseEntity.ok(budgetService.getBudgetsByUserId(userId));
    }

    // Get budget for a user
    @GetMapping("/{id}")
    public ResponseEntity<?> getBudgetById(@PathVariable String id) {

        try {
            // Retrieve the budget by ID
            Budget budget = budgetService.getBudgetById(id);

            // Check if the authenticated user owns the budget
            boolean isAuthUser = userService.isAuthUser(budget.getUserId());
            if (!isAuthUser) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to view this budget.");
            }

            return ResponseEntity.ok(budget);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage()); // Returns "Budget not found" message
        }
    }

    //update budget by that user or ADMIN
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBudget(@PathVariable String id, @RequestBody Budget budget) {

        try {
            // Retrieve the existing budget by ID
            Budget storedBudget = budgetService.getBudgetById(id);

            // Check if the authenticated user owns the budget
            boolean isAuthUser = userService.isAuthUser(storedBudget.getUserId());
            if (!isAuthUser) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to update this budget.");
            }

            // Proceed with the update
            Budget updatedBudget = budgetService.updateBudget(id, budget);
            return ResponseEntity.ok(updatedBudget);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage()); // Budget not found
        }
    }


    // Delete a budget
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBudget(@PathVariable String id) {

        try {
            // Retrieve the existing budget by ID
            Budget storedBudget = budgetService.getBudgetById(id);

            // Check if the authenticated user owns the budget
            boolean isAuthUser = userService.isAuthUser(storedBudget.getUserId());
            if (!isAuthUser) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete this budget.");
            }

            // Proceed with the delete operation
            budgetService.deleteBudget(id);
            return ResponseEntity.noContent().build(); // Return HTTP 204 No Content on successful deletion
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // Budget not found
        }
    }

    //check if the budget is exceeded or not
   @PostMapping("/check/{budgetId}")
   public ResponseEntity<String> checkBudget(@PathVariable String budgetId, @RequestParam BigDecimal newExpense) {

       // Retrieve the budget by ID
       Budget budget = budgetService.getBudgetById(budgetId);

       // Check if the budget exists
       if (budget == null) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Budget not found");
       }

       // Check if the authenticated user is authorized to check this budget
       if (!userService.isAuthUser(budget.getUserId())) {
           return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to check this budget.");
       }

       // Update the spentAmount by adding the new expense
       budget.setCurrentSpentAmount(budget.getCurrentSpentAmount().add(newExpense));

       // Save the updated budget to reflect the change in spentAmount
       budgetService.createOrUpdateBudget(budget);

       // Check if the new expense exceeds the budget
       boolean isOver = budgetService.isExceedingBudget(budget, newExpense);
       return ResponseEntity.ok(isOver ? "You have exceeded your budget!" : "You are within budget.");
   }
}


