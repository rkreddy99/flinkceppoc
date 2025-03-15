package com.example.flinkceppoc.model;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public abstract class BaseEvent implements Serializable {
    private String id;
    private String eventType;
    private LocalDateTime timestamp;
    
    // Default constructor for serialization
    public BaseEvent() {
        this.timestamp = LocalDateTime.now();
    }
}