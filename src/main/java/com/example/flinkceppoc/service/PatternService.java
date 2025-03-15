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
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

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
                .within(Time.hours(1));
            
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
    
    /**
     * Tests pattern detection with a specific event sequence with correlated values
     * @param strategy The strategy ID to use across events
     * @param date The date to use in events A and B
     * @param exchange The exchange to use in event B
     * @param hash The hash to use in event C
     */
    public void testWithEventSequence(String strategy, String date, String exchange, String hash) {
        try {
            // Create sample events for testing with the specified values
            List<BaseEvent> eventList = new ArrayList<>();
            
            EventA eventA = new EventA();
            eventA.setId("A-" + UUID.randomUUID().toString().substring(0, 8));
            eventA.setStrategy(strategy);
            eventA.setDate(date);
            
            EventB eventB = new EventB();
            eventB.setId("B-" + UUID.randomUUID().toString().substring(0, 8));
            eventB.setStrategy(strategy);
            eventB.setDate(date);
            eventB.setExchange(exchange);
            
            EventC eventC = new EventC();
            eventC.setId("C-" + UUID.randomUUID().toString().substring(0, 8));
            eventC.setStrategy(strategy);
            eventC.setHash(hash);
            
            // Add some unrelated events to test the pattern matching
            EventA unrelatedA = new EventA();
            unrelatedA.setId("UA-" + UUID.randomUUID().toString().substring(0, 8));
            unrelatedA.setStrategy("DIFFERENT_" + strategy);
            unrelatedA.setDate(date);
            
            // Add events to the list - mix the order to test pattern detection
            eventList.add(unrelatedA);  // First add an unrelated event
            eventList.add(eventA);      // Then our correlated events
            eventList.add(eventB);
            eventList.add(eventC);
            
            log.info("Testing with events: {}", eventList);
            
            // Create a stream of BaseEvent objects
            DataStream<BaseEvent> inputStream = env.fromCollection(eventList);
            
            // Define pattern: A -> B -> C with correlation conditions
            Pattern<BaseEvent, ?> pattern = Pattern.<BaseEvent>begin("a")
                .where(new SimpleCondition<BaseEvent>() {
                    @Override
                    public boolean filter(BaseEvent event) {
                        return event instanceof EventA && ((EventA) event).getStrategy().equals(strategy);
                    }
                })
                .followedBy("b")
                .where(new SimpleCondition<BaseEvent>() {
                    @Override
                    public boolean filter(BaseEvent event) {
                        if (!(event instanceof EventB)) return false;
                        EventB eventB = (EventB) event;
                        return eventB.getStrategy().equals(strategy) && eventB.getDate().equals(date);
                    }
                })
                .followedBy("c")
                .where(new SimpleCondition<BaseEvent>() {
                    @Override
                    public boolean filter(BaseEvent event) {
                        return event instanceof EventC && ((EventC) event).getStrategy().equals(strategy);
                    }
                })
                .within(Time.hours(1));
            
            // Create pattern stream
            PatternStream<BaseEvent> patternStream = CEP.pattern(inputStream, pattern);
            
            // Define what to do when pattern is detected
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
            
            // Execute the Flink job with a descriptive name
            env.execute("Test Event Sequence Pattern - Strategy: " + strategy);
        } catch (Exception e) {
            log.error("Error testing event sequence", e);
            throw new RuntimeException("Failed to test event sequence", e);
        }
    }
}