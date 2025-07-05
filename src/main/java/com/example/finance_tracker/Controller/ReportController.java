package com.example.finance_tracker.Controller;


import com.example.finance_tracker.Entity.Report;
import com.example.finance_tracker.Entity.Transaction;
import com.example.finance_tracker.Service.ReportService;
import com.example.finance_tracker.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    @GetMapping
    public ResponseEntity<?> getReport(
            @RequestParam String userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) List<Transaction.TransactionCategory> categories)
             {
                 // Log the request parameters
                 logger.info("Requested User ID: " + userId);
                 logger.info("Start Date: " + startDate);
                 logger.info("End Date: " + endDate);
                 logger.info("Categories: " + categories);

        // Check if the authenticated user is authorized to access the report
        if (!userService.isAuthUser(userId)) {
            logger.warn("Access denied for user ID: " + userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this report.");
        }

        // Generate the report
        Report report = reportService.generateReport(userId, startDate, endDate, categories, null);
        return ResponseEntity.ok(report);
    }

    @GetMapping(value = "/download", produces = "text/csv")
    public ResponseEntity<byte[]> downloadReport(
            @RequestParam String userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) List<Transaction.TransactionCategory> categories) {

        // Check if the authenticated user is authorized to access the report
        if (!userService.isAuthUser(userId)) {
            logger.warn("Access denied for user ID: {}", userId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        // Generate the report
        Report report = reportService.generateReport(userId, startDate, endDate, categories, null);

        // Convert the report to CSV
        byte[] csvBytes = reportService.generateCsvReport(report);

        // Set headers for file download
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "financial_report.csv");

        return ResponseEntity.ok()
                .headers(headers)
                .body(csvBytes);
    }
}
