package com.example.flinkceppoc.controller;

import com.example.flinkceppoc.model.BaseEvent;
import com.example.flinkceppoc.model.EventA;
import com.example.flinkceppoc.model.EventB;
import com.example.flinkceppoc.model.EventC;
import com.example.flinkceppoc.service.PatternService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final PatternService patternService;

    @Autowired
    public EventController(PatternService patternService) {
        this.patternService = patternService;
    }

    @PostMapping
    public ResponseEntity<String> processEvent(@RequestBody Map<String, Object> eventData) {
        try {
            // Extract event type
            String eventType = (String) eventData.get("eventType");
            if (eventType == null) {
                return ResponseEntity.badRequest().body("Event type is required");
            }

            // Create the appropriate event object
            BaseEvent event = createEvent(eventType, eventData);
            
            // Generate an ID if not provided
            if (event.getId() == null) {
                event.setId(UUID.randomUUID().toString());
            }

            // In a real implementation, we would send this event to Flink
            // For now, we'll just log it
            return ResponseEntity.ok("Event processed: " + event);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing event: " + e.getMessage());
        }
    }

    @PostMapping("/test-pattern")
    public ResponseEntity<String> testPattern() {
        try {
            // Run our simple pattern detection
            patternService.createSimplePattern();
            return ResponseEntity.ok("Pattern test initiated");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error testing pattern: " + e.getMessage());
        }
    }

    private BaseEvent createEvent(String eventType, Map<String, Object> data) {
        return switch (eventType) {
            case "A" -> {
                EventA event = new EventA();
                event.setStrategy((String) data.get("strategy"));
                event.setDate((String) data.get("date"));
                yield event;
            }
            case "B" -> {
                EventB event = new EventB();
                event.setStrategy((String) data.get("strategy"));
                event.setDate((String) data.get("date"));
                event.setExchange((String) data.get("exchange"));
                yield event;
            }
            case "C" -> {
                EventC event = new EventC();
                event.setStrategy((String) data.get("strategy"));
                event.setHash((String) data.get("hash"));
                yield event;
            }
            default -> throw new IllegalArgumentException("Unknown event type: " + eventType);
        };
    }
}