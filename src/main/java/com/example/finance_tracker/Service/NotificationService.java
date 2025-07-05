package com.example.finance_tracker.Service;

import com.example.finance_tracker.Entity.User;
import com.example.finance_tracker.Repository.UserRepository;
import lombok.Getter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class NotificationService {

    private final SendGridEmailService sendGridEmailService;
    private final UserRepository userRepository;

    @Autowired
    public NotificationService(SendGridEmailService sendGridEmailService, UserRepository userRepository) {
        this.sendGridEmailService = sendGridEmailService;
        this.userRepository = userRepository;
    }

    // Method to send email notifications
    public void sendEmailNotification(String userId, String subject, String message) {
        // Fetch the user's email from the database
        String userEmail = getUserEmail(userId);

        if (userEmail != null) {
            sendGridEmailService.sendEmail(userEmail, subject, message);
        } else {
            System.err.println("User email not found for userId: " + userId);
        }
    }

    // Method to fetch user email from the database
    private String getUserEmail(String userId) {
        User user = userRepository.findByUserId(userId); // Fetch user by userId
        if (user != null) {
            return user.getEmail(); // Return the user's email
        }
        return null; // Return null if user not found
    }
}