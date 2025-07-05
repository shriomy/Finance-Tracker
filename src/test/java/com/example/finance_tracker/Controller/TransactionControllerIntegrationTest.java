package com.example.finance_tracker.Controller;

import com.example.finance_tracker.Entity.Transaction;
import com.example.finance_tracker.Entity.User;
import com.example.finance_tracker.Service.TransactionService;
import com.example.finance_tracker.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.testcontainers.shaded.org.bouncycastle.cms.RecipientId.password;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private UserService userService;

    private Transaction transaction;
    private User user;

    @BeforeEach
    public void setUp() {
        // Create a sample transaction
        transaction = new Transaction();
        transaction.setTransactionId("12345");
        transaction.setUserId("user123");
        transaction.setTransactionAmount(new BigDecimal("100.00"));
        transaction.setTransactionDate(LocalDateTime.now());

        // Create a sample user
        User user = new User("user@example.com", "John", "password123", "Doe", "user123", "USER", "USD");
        user.setRole("USER");

        // Mock the behavior of UserService
        when(userService.isAuthUser("user123")).thenReturn(true);
        when(userService.getUserByUsername("user123")).thenReturn(new User("admin@example.com", "admin123","password123", "Doe", "adminDoe",  "ADMIN", "USD"));
        // Mock the authenticated user
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user123"); // Authenticated user is "user123"
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void testCreateTransaction() throws Exception {
        // Mock the behavior of UserService
        when(userService.isAuthUser("user123")).thenReturn(true); // Ensure this returns true
        // Mock the behavior of TransactionService
        when(transactionService.createTransaction(any(Transaction.class))).thenReturn(transaction);

        // Perform the POST request
        mockMvc.perform(MockMvcRequestBuilders.post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"user123\",\"transactionAmount\":100.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("12345"))
                .andExpect(jsonPath("$.userId").value("user123"))
                .andExpect(jsonPath("$.transactionAmount").value(100.00));
    }

    @Test
    public void testGetAllTransactions_Admin() throws Exception {
        // Mock the behavior of TransactionService
        List<Transaction> transactions = Arrays.asList(transaction);
        when(transactionService.getAllTransactions()).thenReturn(transactions);

        // Mock the behavior of UserService
        when(userService.isAuthUser("user123")).thenReturn(true);

        // Mock the authenticated user as ADMIN
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("admin");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Perform the GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transactionId").value("12345"))
                .andExpect(jsonPath("$[0].userId").value("user123"))
                .andExpect(jsonPath("$[0].transactionAmount").value(100.00));
    }

    @Test
    public void testGetAllTransactions_NonAdmin() throws Exception {
        // Mock the authenticated user as non-ADMIN
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user123");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Mock the behavior of UserService
        User nonAdminUser = new User("user@example.com", "John", "password123", "Doe", "user123", "USER", "USD");
        when(userService.getUserByUsername("user123")).thenReturn(nonAdminUser); // Return a non-admin user

        // Perform the GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().string("You are not authorized to access all transactions."));
    }

    @Test
    public void testGetTransactionsByUserId() throws Exception {
        // Mock the behavior of TransactionService
        List<Transaction> transactions = Arrays.asList(transaction);
        when(transactionService.getTransactionsByUserId("user123")).thenReturn(transactions);

        // Mock the behavior of UserService
        when(userService.isAuthUser("user123")).thenReturn(true);

        // Perform the GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/api/transactions/user/user123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transactionId").value("12345"))
                .andExpect(jsonPath("$[0].userId").value("user123"))
                .andExpect(jsonPath("$[0].transactionAmount").value(100.00));
    }

    @Test
    public void testGetTransactionById() throws Exception {
        // Mock the behavior of TransactionService
        when(transactionService.getTransactionById("12345")).thenReturn(transaction);

        // Mock the behavior of UserService
        when(userService.isAuthUser("user123")).thenReturn(true);

        // Perform the GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/api/transactions/12345")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("12345"))
                .andExpect(jsonPath("$.userId").value("user123"))
                .andExpect(jsonPath("$.transactionAmount").value(100.00));
    }

    @Test
    public void testGetTransactionById_NotFound() throws Exception {
        // Mock the behavior of TransactionService
        when(transactionService.getTransactionById("99999")).thenThrow(new RuntimeException("Transaction not found with ID: 99999"));

        // Mock the behavior of UserService
        when(userService.isAuthUser("user123")).thenReturn(true);

        // Perform the GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/api/transactions/99999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Transaction not found with ID: 99999"));
    }

    @Test
    public void testUpdateTransaction() throws Exception {
        // Mock the behavior of TransactionService
        when(transactionService.getTransactionById("12345")).thenReturn(transaction);
        when(transactionService.updateTransaction(eq("12345"), any(Transaction.class))).thenReturn(transaction);

        // Mock the behavior of UserService
        when(userService.isAuthUser("user123")).thenReturn(true);

        // Perform the PUT request
        mockMvc.perform(MockMvcRequestBuilders.put("/api/transactions/12345")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"user123\",\"transactionAmount\":200.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("12345"))
                .andExpect(jsonPath("$.userId").value("user123"))
                .andExpect(jsonPath("$.transactionAmount").value(100.00));
    }

    @Test
    public void testDeleteTransaction() throws Exception {
        // Mock the behavior of TransactionService
        when(transactionService.getTransactionById("12345")).thenReturn(transaction);

        // Mock the behavior of UserService
        when(userService.isAuthUser("user123")).thenReturn(true);

        // Perform the DELETE request
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/transactions/12345")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
