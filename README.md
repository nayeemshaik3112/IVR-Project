# IVR Backend System

## Architecture Overview

This repository contains a Spring Boot 3 / Java 17 microservices IVR backend composed of Eureka discovery, Spring Cloud Config, API Gateway, an IVR orchestration layer, domain services for customers, policies, claims, and notifications, plus Kafka, Axon Server, Zipkin, Prometheus, and Grafana.

## Parent Project Structure

```text
ivr-backend-system/
├── pom.xml
├── docker-compose.yml
├── config-repo/
├── monitoring/
│   ├── prometheus/
│   └── grafana/
├── discovery-server/
├── config-server/
├── api-gateway/
├── ivr-orchestrator-service/
├── customer-service/
├── policy-service/
├── claims-service/
└── notification-service/
```

## Run Steps

1. Start infrastructure:
   ```bash
   docker compose up -d
   ```
2. Build all services:
   ```bash
   mvn clean package
   ```
3. Start services in this order:
   1. `discovery-server`
   2. `config-server`
   3. `api-gateway`
   4. `customer-service`
   5. `policy-service`
   6. `claims-service`
   7. `notification-service`
   8. `ivr-orchestrator-service`
4. Test the IVR aggregation endpoint:
   ```bash
   curl "http://localhost:8080/api/ivr/respond?phone=9876543210"
   ```

## Key Endpoints

- Eureka: `http://localhost:8761`
- Config Server: `http://localhost:8888`
- Gateway: `http://localhost:8080`
- Axon Server: `http://localhost:8024`
- Zipkin: `http://localhost:9411`
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000`

## Final Zip Structure Instructions

1. From the project root, ensure all modules and infrastructure files are present.
2. Remove `target/` folders if you want a clean distribution.
3. Compress the root folder:
   ```bash
   powershell Compress-Archive -Path .\* -DestinationPath .\ivr-backend-system.zip -Force
   ```
4. Share the generated `ivr-backend-system.zip`.
