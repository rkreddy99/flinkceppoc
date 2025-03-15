package com.example.flinkceppoc.config;

import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class FlinkConfig {
    
    @Value("${flink.jobmanager.address:localhost}")
    private String jobManagerAddress;
    
    @Value("${flink.jobmanager.port:8081}")
    private int jobManagerPort;
    
    @Bean
    public StreamExecutionEnvironment streamExecutionEnvironment() {
        // For development, you can connect to the remote Flink cluster running in Docker
        Configuration config = new Configuration();
        
        // To connect to a standalone Flink cluster, create the environment with the right configuration
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createRemoteEnvironment(
            jobManagerAddress, // JobManager hostname (from Docker Compose)
            6123, // JobManager RPC port
            config
        );
        
        // Set restart strategy
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, 5000));
        
        // Set parallelism
        env.setParallelism(1);
        
        return env;
    }
}