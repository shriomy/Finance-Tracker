package com.example.finance_tracker.Repository;

import com.example.finance_tracker.Entity.Goal.Goal;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalRepository  extends MongoRepository<Goal, String> {
    List<Goal> findByUserId(String userId);
}
