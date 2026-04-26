# Enterprise Insurance Architecture Expansion

## Target Architecture

```text
Experience Layer
  - API Gateway
  - IVR Orchestrator Service
  - IVR Session Service

Customer Domain
  - Customer Service
  - Customer Profile Service (planned)
  - KYC / Identity Verification Service (planned)

Policy and Coverage Domain
  - Policy Service
  - Enrollment Service (planned)
  - Eligibility Service
  - Coverage Service (planned)
  - Premium Calculation Service (planned)

Claims Domain
  - Claims Service (Axon CQRS/Event Sourcing + Outbox)
  - Claims Validation Service (planned)
  - Claims Processing Service (planned)
  - Claims Adjudication Service (planned)
  - Fraud Detection Service (planned)

Financial Domain
  - Billing Service (planned)
  - Payment Service (planned)

Communication Domain
  - Communication Preference Service
  - Notification Service

Platform Domain
  - Discovery Server
  - Config Server
  - Audit Service (planned)
  - Central Logging Service (planned)
  - Prometheus / Grafana / Zipkin / Axon Server
```

## Services Added in This Iteration

1. `eligibility-service`
   - Validates whether a customer-policy pair is active and serviceable for IVR and claims journeys.
2. `communication-preference-service`
   - Resolves the best outbound channel based on customer opt-ins, quiet hours, and fallback policy.
3. `ivr-session-service`
   - Persists IVR request/session state so orchestration has conversational context and auditable outcomes.

## Integration Points

- `ivr-orchestrator-service` -> Feign -> `ivr-session-service`
- `ivr-orchestrator-service` -> Feign -> `eligibility-service`
- `notification-service` -> Feign -> `communication-preference-service`
- `claims-service` -> Kafka -> `notification-service`
- `claims-service` -> Axon -> event store / projections / outbox

## End-to-End Flow

1. IVR request enters the API gateway and is routed to the orchestrator.
2. The orchestrator opens or resumes an IVR session and resolves the customer, policies, claims, and eligibility state.
3. Claims commands are handled through Axon and projected into the read model.
4. The outbox publishes claim lifecycle events to Kafka.
5. Notification service resolves the preferred communication channel before logging or dispatching the notification.
6. The orchestrator completes or fails the session with a persisted summary for auditability and recovery.

## Security Gap

The current codebase does not contain an existing JWT implementation to reuse. A proper next phase should add:

- `auth-service` or external IdP integration
- gateway token validation
- service-to-service trust model
- endpoint policy by role/scope

This expansion preserves the current structure and prepares those hooks without fabricating a non-existent security layer.