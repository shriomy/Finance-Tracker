package com.example.finance_tracker.Entity.Goal;

import com.example.finance_tracker.Entity.Budget;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDate;

@Document(collection = "goals")
@Data
@AllArgsConstructor
//@NoArgsConstructor
public class Goal {
    @Id
    private String id;
    private String userId;
    private String goalName;
    private GoalStatus status;
    private double targetAmount;
    private double savedAmount;
    private LocalDate startDate;
    private LocalDate endDate;

    public Goal(){};

    //Constructor
    public Goal(String userId, String goalName, GoalStatus status, double targetAmount, double savedAmount, LocalDate startDate, LocalDate endDate) {
        this.userId = userId;
        this.goalName = goalName;
        this.status = status;
        this.targetAmount = targetAmount;
        this.savedAmount = savedAmount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    //Getters and Setters

    public String getId() {return id;}
    public void setId(String id) {this.id = id;}

    public String getUserId() {return userId;}
    public void setUserId(String userId) {this.userId = userId;}

    public String getGoalName() {return goalName;}
    public void setGoalName(String goalName) {this.goalName = goalName;}

    public GoalStatus getStatus() {return status;}
    public void setStatus(GoalStatus status) {this.status = status;}

    public double getTargetAmount() {return targetAmount;}
    public void setTargetAmount(double targetAmount) {this.targetAmount = targetAmount;}

    public double getSavedAmount() {return savedAmount;}
    public void setSavedAmount(double savedAmount) {this.savedAmount = savedAmount;}

    public LocalDate getStartDate() {return startDate;}
    public void setStartDate(LocalDate startDate) {this.startDate = startDate;}

    public LocalDate getEndDate() {return endDate;}
    public void setEndDate(LocalDate endDate) {this.endDate = endDate;}

}








