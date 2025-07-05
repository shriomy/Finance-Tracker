package com.example.finance_tracker.Controller;
import com.example.finance_tracker.Service.UserService;
import com.example.finance_tracker.Utility.JwtUtil;
import com.example.finance_tracker.Entity.User;
import com.example.finance_tracker.Repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "APIs for user authentication and registration")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

//    @Autowired
//    public AuthController(UserService userService ) {
//        this.userService = userService;
//    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            //check username
            if(userRepository.findByUsername(authRequest.getUsername()).isEmpty()){
                logger.warn("Login failed: Incorrect username - {}", authRequest.getUsername());
                return ResponseEntity.status(401).body("Incorrect username");
            }
            // Retrieve user from DB
            User user = userRepository.findByUsername(authRequest.getUsername())
                    .orElseThrow(()-> new RuntimeException("User not found"));

            // Compare raw password with hashed password
            if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
                logger.warn("Login failed: Invalid password for user - {}", authRequest.getUsername());
                return ResponseEntity.status(401).body("Invalid Password");
            }

            // Authenticate the user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

             //Generate JWT token
            String token = jwtUtil.generateToken(authRequest.getUsername(),
                    Collections.singletonList(user.getRole()));


            return ResponseEntity.ok(new AuthResponse(token));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }


    @PostMapping("/signup")
    public User signup(@RequestBody User user) {
        return userService.createUser(user);
    }
}

// DTO classes for the authentication request and response

class AuthRequest {
    private String username;
    private String password;

    public AuthRequest() {}

    public AuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

class AuthResponse {
    private String token;

    public AuthResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
