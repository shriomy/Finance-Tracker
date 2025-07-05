package com.example.finance_tracker.Controller;

import com.example.finance_tracker.Entity.Budget;
import com.example.finance_tracker.Entity.Goal.Goal;
import com.example.finance_tracker.Entity.User;
import com.example.finance_tracker.Service.GoalService;
import com.example.finance_tracker.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    @Autowired
    private GoalService goalService;
    private UserService userService;

    public GoalController(GoalService goalService, UserService userService) {
        this.goalService = goalService;
        this.userService = userService;
    }

    //Create a Goal
    @PostMapping
    public ResponseEntity<?> createGoal(@RequestBody Goal goal) {

        // Check if the authenticated user is allowed to create the goal
        if (!userService.isAuthUser(goal.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to create this goal.");
        }
        return ResponseEntity.ok(goalService.createGoal(goal));
    }

    // Get all goals by ADMIN only
    @GetMapping
    public ResponseEntity<?> getAllGoals() {

        // Get the authenticated user's details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUsername = authentication.getName();

        // Fetch the user from the database
        User user = userService.getUserByUsername(authenticatedUsername);

        // Check if the user is an ADMIN
        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access all goals.");
        }

        // Fetch and return all goals
        return ResponseEntity.ok(goalService.getAllGoals());
    }

    //Get all Goals of a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserGoals(@PathVariable String userId) {

        // Check if the authenticated user is allowed to access this user's goals
        if (!userService.isAuthUser(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to view these goals.");
        }
        return ResponseEntity.ok(goalService.getUserGoals(userId));
    }

    //get a goal by goal ID
    @GetMapping("/{goalId}")
    public ResponseEntity<?> getGoalById(@PathVariable String goalId) {

        try {
            // Retrieve the goal by ID
            Goal goal = goalService.getGoalById(goalId);

            // Check if the authenticated user owns the goal
            boolean isAuthUser = userService.isAuthUser(goal.getUserId());
            if (!isAuthUser) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to view this budget.");
            }

            return ResponseEntity.ok(goal);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage()); // Returns "Goal not found" message
        }
    }


    //update a goal
    @PutMapping("/{goalId}")
    public ResponseEntity<?> updateGoal(@PathVariable String goalId, @RequestBody Goal goal) {

        try {
            // Retrieve the existing budget by ID
            Goal storedGoal = goalService.getGoalById(goalId);

            // Check if the authenticated user owns the budget
            boolean isAuthUser = userService.isAuthUser(storedGoal.getUserId());
            if (!isAuthUser) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to update this goal.");
            }

            // Proceed with the update
            Goal updatedGoal = goalService.updateGoal(goalId,goal);
            return ResponseEntity.ok(updatedGoal);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage()); // Budget not found
        }
    }


    //delete a goal
    @DeleteMapping("/{goalId}")
    public ResponseEntity<?> deleteGoal(@PathVariable String goalId) {

        try {
            // Retrieve the existing budget by ID
            Goal storedGoal = goalService.getGoalById(goalId);

            // Check if the authenticated user owns the budget
            boolean isAuthUser = userService.isAuthUser(storedGoal.getUserId());
            if (!isAuthUser) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete this Goal.");
            }

            // Proceed with the delete operation
            goalService.deleteGoal(goalId);
            return ResponseEntity.noContent().build(); // Return HTTP 204 No Content on successful deletion
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // Budget not found
        }
    }


    //return how much of percentage completed
    @GetMapping("/{goalId}/completion")
    public ResponseEntity<String> get(@PathVariable String goalId) {

        // Check if the current user is authorized to access the goal's completion data
        Goal goal = goalService.getGoalById(goalId); // Fetch the goal
        if (goal == null || !userService.isAuthUser(goal.getUserId())) {
            return ResponseEntity.status(403).body("You are not authorized to view this goal's completion.");
        }
        // If authorized, return the completion percentage
        return ResponseEntity.ok(goalService.goalCompletionPercentage(goalId));
    }

    // save money in the saved Amount of the goal
    @PostMapping("/addMoney")
    public ResponseEntity<?> addMoneyToGoal(@RequestBody Goal request) {

        Goal goalOfRequest = goalService.getGoalById(request.getId());

        // Check if the current user is authorized to modify the goal (either as the owner or as ADMIN)
        if (!userService.isAuthUser(goalOfRequest.getUserId())) {
            return ResponseEntity.status(403).body("You are not authorized to update this goal.");
        }

        // Proceed to add money to the goal if the user is authorized
        Goal updatedGoal = goalService.addMoneyToGoal(request);

        if (updatedGoal == null) {
            return ResponseEntity.status(404).body("Goal not found.");
        }
        return ResponseEntity.ok(updatedGoal);
    }
}
