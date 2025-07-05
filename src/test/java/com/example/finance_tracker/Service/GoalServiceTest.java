package com.example.finance_tracker.Service;

import com.example.finance_tracker.Entity.Goal.Goal;
import com.example.finance_tracker.Entity.Goal.GoalStatus;
import com.example.finance_tracker.Repository.GoalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private GoalService goalService;

    private Goal testGoal;

    @BeforeEach
    void setup() {
        testGoal = new Goal();
        testGoal.setId("1");
        testGoal.setGoalName("Buy a Car");
        testGoal.setTargetAmount(10000);
        testGoal.setSavedAmount(0);
        testGoal.setStatus(GoalStatus.NOT_STARTED);
    }

    @Test
    void testCreateGoal() {
        when(goalRepository.save(any(Goal.class))).thenReturn(testGoal);

        Goal createdGoal = goalService.createGoal(testGoal);

        assertNotNull(createdGoal);
        assertEquals("Buy a Car", createdGoal.getGoalName());
        assertEquals(GoalStatus.NOT_STARTED, createdGoal.getStatus());
        verify(goalRepository, times(1)).save(testGoal);
    }

    @Test
    void testGetAllGoals() {
        when(goalRepository.findAll()).thenReturn(List.of(testGoal));

        List<Goal> goals = goalService.getAllGoals();

        assertFalse(goals.isEmpty());
        assertEquals(1, goals.size());
        verify(goalRepository, times(1)).findAll();
    }

    @Test
    void testGetGoalById_Found() {
        when(goalRepository.findById("1")).thenReturn(Optional.of(testGoal));

        Goal goal = goalService.getGoalById("1");

        assertNotNull(goal);
        assertEquals("Buy a Car", goal.getGoalName());
        verify(goalRepository, times(1)).findById("1");
    }

    @Test
    void testGetGoalById_NotFound() {
        when(goalRepository.findById("2")).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> goalService.getGoalById("2"));

        assertEquals("Budget not found", exception.getMessage());
        verify(goalRepository, times(1)).findById("2");
    }

    @Test
    void testUpdateGoal() {
        Goal updatedGoal = new Goal();
        updatedGoal.setGoalName("Buy a House");
        updatedGoal.setTargetAmount(50000);
        updatedGoal.setSavedAmount(10000);
        updatedGoal.setStatus(GoalStatus.IN_PROGRESS);

        when(goalRepository.findById("1")).thenReturn(Optional.of(testGoal));
        when(goalRepository.save(any(Goal.class))).thenReturn(updatedGoal);

        Goal result = goalService.updateGoal("1", updatedGoal);

        assertEquals("Buy a House", result.getGoalName());
        assertEquals(50000, result.getTargetAmount());
        assertEquals(10000, result.getSavedAmount());
        assertEquals(GoalStatus.IN_PROGRESS, result.getStatus());
        verify(goalRepository, times(1)).save(any(Goal.class));
    }

    @Test
    void testDeleteGoal() {
        doNothing().when(goalRepository).deleteById("1");

        goalService.deleteGoal("1");

        verify(goalRepository, times(1)).deleteById("1");
    }

    @Test
    void testGoalCompletionPercentage_NotFound() {
        when(goalRepository.findById("2")).thenReturn(Optional.empty());

        String result = goalService.goalCompletionPercentage("2");

        assertEquals("Goal not found", result);
    }

    @Test
    void testGoalCompletionPercentage_NotReached() {
        testGoal.setSavedAmount(2000);
        when(goalRepository.findById("1")).thenReturn(Optional.of(testGoal));

        String result = goalService.goalCompletionPercentage("1");

        assertEquals("20% (Remaining: 8000.00)", result);
    }

    @Test
    void testGoalCompletionPercentage_Completed() {
        testGoal.setSavedAmount(12000);
        when(goalRepository.findById("1")).thenReturn(Optional.of(testGoal));

        String result = goalService.goalCompletionPercentage("1");

        assertEquals("100% (Extra saved: 2000.00)", result);
    }

    @Test
    void testAddMoneyToGoal() {
        Goal goalWithSavings = new Goal();
        goalWithSavings.setId("1");
        goalWithSavings.setSavedAmount(5000);

        when(goalRepository.findById("1")).thenReturn(Optional.of(testGoal));
        when(goalRepository.save(any(Goal.class))).thenReturn(goalWithSavings);

        Goal updatedGoal = goalService.addMoneyToGoal(goalWithSavings);

        assertEquals(5000, updatedGoal.getSavedAmount());
        verify(goalRepository, times(1)).save(any(Goal.class));
    }
}
