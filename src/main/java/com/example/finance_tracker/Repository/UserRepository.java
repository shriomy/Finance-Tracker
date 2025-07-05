package com.example.finance_tracker.Repository;

import com.example.finance_tracker.Entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    User findByEmail(String email);
    Optional<User> findByUsername(String username);
    User findByUserId(String userId);

}
