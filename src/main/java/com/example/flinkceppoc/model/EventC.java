package com.example.flinkceppoc.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EventC extends BaseEvent {
    private String strategy;
    private String hash;
    
    public EventC() {
        super();
        setEventType("C");
    }
}