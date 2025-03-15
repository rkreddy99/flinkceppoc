package com.example.flinkceppoc.config;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlinkConfig {
    
    @Bean
    public StreamExecutionEnvironment streamExecutionEnvironment() {
        // For the POC, we'll use a local environment
        return StreamExecutionEnvironment.getExecutionEnvironment();
    }
}