package com.example.flinkceppoc.config;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class PatternConfig {
    private String patternId;
    private String description;
    private String version;
    private boolean enabled;
    
    private boolean enforceEventOrder;
    private String timeWindow;
    private boolean allowEventReuse;
    
    private List<EventConfig> events;
    private Map<String, List<String>> correlationRules;
    
    private OutputEventConfig outputEvent;
    
    @Data
    public static class EventConfig {
        private String id;
        private String type;
    }
    
    @Data
    public static class OutputEventConfig {
        private String type;
        private Map<String, String> mapping;
    }
}