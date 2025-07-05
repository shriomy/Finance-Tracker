package com.example.finance_tracker.Repository;

import com.example.finance_tracker.Entity.Budget;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface BudgetRepository extends MongoRepository<Budget, String> {
    List<Budget> findByUserId(String userId);
}


