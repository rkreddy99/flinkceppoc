package com.example.flinkceppoc.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EventA extends BaseEvent {
    private String strategy;
    private String date;
    
    public EventA() {
        super();
        setEventType("A");
    }
}