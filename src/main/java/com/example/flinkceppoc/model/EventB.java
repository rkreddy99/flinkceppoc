package com.example.flinkceppoc.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EventB extends BaseEvent {
    private String strategy;
    private String date;
    private String exchange;
    
    public EventB() {
        super();
        setEventType("B");
    }
}