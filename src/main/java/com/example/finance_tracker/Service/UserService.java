package com.example.finance_tracker.Service;

import com.example.finance_tracker.Entity.Transaction;
import com.example.finance_tracker.Entity.User;
import com.example.finance_tracker.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ✅ create user with error handling
    public User createUser(User user) {

        //email validation
        if(userRepository.findByEmail(user.getEmail()) != null) throw new IllegalArgumentException("Email is already in use");

        //username validation
        if(userRepository.findByUsername(user.getUsername()).isPresent()) throw new IllegalArgumentException("Username is already in use");

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    // ✅ get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ✅ get user by it's id
    public User getUserById(String userId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return existingUser;
    };

    // ✅ get user by it's username
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    // ✅ update a user
    public User updateUser(String userId, User userData) {

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        existingUser.setUsername(userData.getUsername());
        existingUser.setEmail(userData.getEmail());
        existingUser.setFirstName(userData.getFirstName());
        existingUser.setLastName(userData.getLastName());
        existingUser.setRole(userData.getRole());
        existingUser.setCurrency(userData.getCurrency());

        return userRepository.save(existingUser);
    };

    // ✅ delete a user
    public ResponseEntity<String> deleteUser(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User with ID " + userId + " not found.");
        }

        userRepository.deleteById(userId);
        return ResponseEntity.ok("User with ID " + userId + " deleted successfully.");
    }

    // ✅ My method to check if the current user's username is the same as username of the request body
    public boolean isAuthUser(String userid){
        User user = getUserById(userid);

        //get token's username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUsername = authentication.getName(); // Extracts username from token

        // Get authenticated user details
        User authenticatedUser = getUserByUsername(authenticatedUsername);

        // Allow access if the user is an ADMIN or if they own the resource
        return (authenticatedUser.getRole().equals("ADMIN")) || user.getUsername().equals(authenticatedUsername);
    }
}
