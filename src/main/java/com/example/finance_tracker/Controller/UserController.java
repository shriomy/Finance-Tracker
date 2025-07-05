package com.example.finance_tracker.Controller;

import com.example.finance_tracker.Entity.User;
import com.example.finance_tracker.Repository.UserRepository;
import com.example.finance_tracker.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

 //creating user will handle from Auth controller

    //get all users
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    //update user by id
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable String userId,@RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(userId,user));
    }

    //delete user by id
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        return ResponseEntity.ok(userService.deleteUser(userId));
    }
}
