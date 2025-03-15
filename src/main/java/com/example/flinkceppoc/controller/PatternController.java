package com.example.flinkceppoc.controller;

import com.example.flinkceppoc.config.PatternConfig;
import com.example.flinkceppoc.service.PatternService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patterns")
public class PatternController {

    private final PatternService patternService;

    @Autowired
    public PatternController(PatternService patternService) {
        this.patternService = patternService;
    }

    @PostMapping
    public ResponseEntity<String> createPattern(@RequestBody PatternConfig patternConfig) {
        try {
            // Validate pattern configuration
            if (patternConfig.getPatternId() == null || patternConfig.getPatternId().isEmpty()) {
                return ResponseEntity.badRequest().body("Pattern ID is required");
            }
            
            if (!patternConfig.isEnabled()) {
                return ResponseEntity.ok("Pattern created but not enabled: " + patternConfig.getPatternId());
            }

            // In a full implementation, we would convert this to a Flink pattern
            // and deploy it to the Flink runtime
            patternService.createPatternFromConfig(patternConfig);
            
            return ResponseEntity.ok("Pattern created: " + patternConfig.getPatternId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating pattern: " + e.getMessage());
        }
    }
}