package com.example.flinkceppoc.controller;

import com.example.flinkceppoc.model.*;
import com.example.flinkceppoc.service.PatternService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final PatternService patternService;

    @Autowired
    public TestController(PatternService patternService) {
        this.patternService = patternService;
    }

    @PostMapping("/simple-pattern")
    public ResponseEntity<Map<String, String>> testSimplePattern() {
        try {
            // Run our simple pattern detection
            patternService.createSimplePattern();
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Pattern detection job submitted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Error testing pattern: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/event-sequence")
    public ResponseEntity<Map<String, String>> testEventSequence() {
        try {
            // Create a test sequence with correlated values
            String strategy = "MACD_" + UUID.randomUUID().toString().substring(0, 8);
            String date = "2023-03-15";
            String exchange = "NASDAQ";
            String hash = UUID.randomUUID().toString();
            
            // Run the pattern detection with these values
            patternService.testWithEventSequence(strategy, date, exchange, hash);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Event sequence pattern detection submitted");
            response.put("strategy", strategy);
            response.put("date", date);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Error testing event sequence: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}