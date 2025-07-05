package com.example.finance_tracker.Service;

import com.example.finance_tracker.Entity.Budget;
import com.example.finance_tracker.Entity.Goal.Goal;
import com.example.finance_tracker.Entity.Goal.GoalStatus;
import com.example.finance_tracker.Repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
public class GoalService {
    private final GoalRepository goalRepository;

    //constructor
    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    // ✅ create a gaol
    public Goal createGoal(Goal goal) {
        goal.setSavedAmount(0);
        goal.setStatus(GoalStatus.NOT_STARTED);
        return goalRepository.save(goal);
    }

    // ✅ Get all goals by admin
    public List<Goal> getAllGoals() {
        return goalRepository.findAll();
    }

    // ✅ Get all budgets of a user
    public List<Goal> getUserGoals(String userId) {
        return goalRepository.findByUserId(userId);
    }

    // ✅ Get budget by ID
    public Goal getGoalById(String id) {
        return goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found"));
    }

    // ✅ Update a budget
    public Goal updateGoal(String goalId, Goal updatedGoal) {
        Goal goal = getGoalById(goalId);

        goal.setGoalName(updatedGoal.getGoalName());
        goal.setStatus(updatedGoal.getStatus());
        goal.setTargetAmount(updatedGoal.getTargetAmount());
        goal.setSavedAmount(updatedGoal.getSavedAmount());
        goal.setStartDate(updatedGoal.getStartDate());
        goal.setEndDate(updatedGoal.getEndDate());

        return goalRepository.save(goal);
    }

    // ✅ delete goal
    public void deleteGoal(String goalId) {
        goalRepository.deleteById(goalId);
    }

    // ✅ Check completion percentage of a goal
    public String goalCompletionPercentage(String goalId) {
        Optional<Goal> optionalGoal = goalRepository.findById(goalId);

        if (optionalGoal.isEmpty()) {
            return "Goal not found";
        }

        Goal goal = optionalGoal.get();
        double savedAmount = goal.getSavedAmount();
        double targetAmount = goal.getTargetAmount();

        if (savedAmount >= targetAmount) {
            double extra = savedAmount - targetAmount;
            return String.format("100%% (Extra saved: %.2f)", extra);
        } else {
            double remaining = targetAmount - savedAmount;
            double percentage = (savedAmount / targetAmount) * 100;
            return String.format("%.0f%% (Remaining: %.2f)", percentage, remaining);
        }
    }

    // ✅ add money to savings of a goal
    public Goal addMoneyToGoal(Goal request) {
        Optional<Goal> optionalGoal = goalRepository.findById(request.getId());

        if (optionalGoal.isEmpty()) {
            return null;  // You may handle this case in the controller instead of throwing an error
        }

        Goal goal = optionalGoal.get();
        double savedAmount = goal.getSavedAmount();
        double targetAmount = goal.getTargetAmount();
        double newAmount = request.getSavedAmount();

        savedAmount += newAmount;
        goal.setSavedAmount(savedAmount);

        if (savedAmount >= targetAmount) {
            goal.setStatus(GoalStatus.COMPLETED);
        } else {
            goal.setStatus(GoalStatus.IN_PROGRESS);
        }

        return goalRepository.save(goal);
    }
}
