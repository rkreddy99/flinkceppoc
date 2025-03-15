package com.example.flinkceppoc.service;

import com.example.flinkceppoc.config.PatternConfig;
import com.example.flinkceppoc.model.*;

import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.functions.PatternProcessFunction;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.List;
import java.time.Duration;
import java.util.ArrayList;

@Service
@Slf4j
public class PatternService {

    private final StreamExecutionEnvironment env;
    
    @Autowired
    public PatternService(StreamExecutionEnvironment env) {
        this.env = env;
    }
    
    /**
     * Creates a simple pattern for demonstration - detects A followed by B followed by C
     * This is a simplified version that doesn't use the JSON config yet
     */
    public void createSimplePattern() {
        try {
            // Create sample events for testing
            List<BaseEvent> eventList = new ArrayList<>();
            
            EventA eventA = new EventA();
            eventA.setId("A1");
            eventA.setStrategy("MACD");
            eventA.setDate("2023-01-01");
            eventList.add(eventA);
            
            EventB eventB = new EventB();
            eventB.setId("B1");
            eventB.setStrategy("MACD");
            eventB.setDate("2023-01-01");
            eventB.setExchange("NYSE");
            eventList.add(eventB);
            
            EventC eventC = new EventC();
            eventC.setId("C1");
            eventC.setStrategy("MACD");
            eventC.setHash("abc123");
            eventList.add(eventC);
            
            // Create a stream of BaseEvent objects
            DataStream<BaseEvent> inputStream = env.fromCollection(eventList);
            
            // Define pattern: A -> B -> C
            Pattern<BaseEvent, ?> pattern = Pattern.<BaseEvent>begin("a")
                .where(new SimpleCondition<BaseEvent>() {
                    @Override
                    public boolean filter(BaseEvent event) {
                        return event instanceof EventA;
                    }
                })
                .followedBy("b")
                .where(new SimpleCondition<BaseEvent>() {
                    @Override
                    public boolean filter(BaseEvent event) {
                        return event instanceof EventB;
                    }
                })
                .followedBy("c")
                .where(new SimpleCondition<BaseEvent>() {
                    @Override
                    public boolean filter(BaseEvent event) {
                        return event instanceof EventC;
                    }
                })
                .within(Duration.ofHours(1));
            
            // Create pattern stream
            PatternStream<BaseEvent> patternStream = CEP.pattern(inputStream, pattern);
            
            // Define what to do when pattern is detected - using PatternProcessFunction in Flink 1.20
            DataStream<StrategyReadyEvent> result = patternStream.process(
                new PatternProcessFunction<BaseEvent, StrategyReadyEvent>() {
                    @Override
                    public void processMatch(Map<String, List<BaseEvent>> match, Context ctx, Collector<StrategyReadyEvent> out) {
                        // Get the first event of each type
                        EventA a = (EventA) match.get("a").get(0);
                        EventB b = (EventB) match.get("b").get(0);
                        EventC c = (EventC) match.get("c").get(0);
                        
                        StrategyReadyEvent output = new StrategyReadyEvent();
                        output.setId(a.getId() + "_" + b.getId() + "_" + c.getId());
                        output.setStrategy(a.getStrategy());
                        output.setDate(a.getDate());
                        output.setExchange(b.getExchange());
                        output.setHash(c.getHash());
                        
                        log.info("Pattern matched: {}", output);
                        out.collect(output);
                    }
                }
            );
            
            // Print the results
            result.print();
            
            // Execute the Flink job
            env.execute("Simple Pattern Detection");
        } catch (Exception e) {
            log.error("Error creating simple pattern", e);
        }
    }
    
    /**
     * Creates a pattern based on the JSON configuration
     * This will be implemented in future phases
     */
    public void createPatternFromConfig(PatternConfig config) {
        // To be implemented
        log.info("Pattern from config: {}", config);
    }
}