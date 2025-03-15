package com.example.flinkceppoc.model;

import lombok.Data;

@Data
public class StrategyReadyEvent {
    private String id;
    private String strategy;
    private String date;
    private String exchange;
    private String hash;
}