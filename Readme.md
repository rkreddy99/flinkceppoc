# Flink CEP POC

A proof-of-concept application demonstrating integration between Spring Boot and Apache Flink's Complex Event Processing (CEP) library.

## Prerequisites

- Java 17
- Maven
- Docker and Docker Compose

## Building the Application

```bash
mvn clean package
```

## Running with Docker Compose

```bash
docker-compose up -d
```

This will start:
- Flink JobManager (accessible at http://localhost:8081)
- Flink TaskManager
- Spring Boot application (accessible at http://localhost:8080)

## API Endpoints

### Test Pattern Detection

```
POST /api/patterns/test-pattern
```

This endpoint initiates a simple pattern detection job in Flink.

### Submit Event

```
POST /api/events
```

Example payload for EventA:
```json
{
  "eventType": "A",
  "strategy": "MACD",
  "date": "2023-01-01"
}
```

Example payload for EventB:
```json
{
  "eventType": "B",
  "strategy": "MACD",
  "date": "2023-01-01",
  "exchange": "NYSE"
}
```

Example payload for EventC:
```json
{
  "eventType": "C",
  "strategy": "MACD",
  "hash": "abcdef123456"
}
```

### Create Pattern

```
POST /api/patterns
```

Example payload:
```json
{
  "patternId": "STRATEGY_READY",
  "description": "Strategy deployment readiness check",
  "version": "1.0",
  "enabled": true,
  "enforceEventOrder": false,
  "timeWindow": "48h",
  "allowEventReuse": true,
  "events": [
    {"id": "a", "type": "EventA"},
    {"id": "b", "type": "EventB"},
    {"id": "c", "type": "EventC"}
  ],
  "correlationRules": {
    "strategy": ["a", "b", "c"],
    "date": ["a", "b"]
  },
  "outputEvent": {
    "type": "StrategyReadyEvent",
    "mapping": {
      "date": "a.date",
      "strategy": "a.strategy",
      "exchange": "b.exchange",
      "hash": "c.hash"
    }
  }
}
```

## Next Steps

This POC demonstrates basic integration between Spring Boot and Flink CEP. The following steps would be needed for a production-ready implementation:

1. Implement proper event sourcing to Flink (Kafka, etc.)
2. Complete the pattern conversion from JSON to Flink CEP API
3. Implement pattern validation
4. Add monitoring and management endpoints
5. Add tests