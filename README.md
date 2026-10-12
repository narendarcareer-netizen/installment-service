# Installment Service — card installment-plan microservice

A small, focused demo in the style of JPMorganChase's Consumer & Community Banking
engineering: a REST microservice that opens and manages **card installment plans**
(a purchase split into equal monthly payments at a fixed APR), persists them as a
ledger, and publishes lifecycle events.

## Why this exists

Built as a "prove it before the interview" project: instead of only describing
backend experience, this repo *is* the demonstration — Java 17, Spring Boot 3,
JPA, Kafka-ready event publishing, the same shape as the systems JPMC's
Wilmington CCB teams hire for.

## What it does

- `POST /api/plans` — open a plan: card ID, purchase amount, term (3–24 months), APR
- `GET /api/plans?cardId=...` — list a card's plans
- `GET /api/plans/{id}` — fetch one plan (includes computed total payable)
- `POST /api/plans/{id}/cancel` — cancel an active plan

Every create/cancel publishes a `PLAN_CREATED` / `PLAN_CANCELLED` event —
logged by default, or sent to the `installment-plan-events` Kafka topic when
`app.events.kafka-enabled=true`.

The monthly payment uses the standard loan amortization formula
`P·r / (1 − (1+r)^−n)`, covered by a unit test against the textbook value
($1,200 at 12% APR / 12 months → $106.62).

## Run it

```bash
# needs Java 17 + Maven
mvn spring-boot:run
```

Then:

```bash
curl -X POST localhost:8080/api/plans \
  -H 'Content-Type: application/json' \
  -d '{"cardId":"card-123","purchaseAmount":1200.00,"termMonths":12,"apr":12.00}'

curl 'localhost:8080/api/plans?cardId=card-123'
```

H2 console (to inspect the ledger): http://localhost:8080/h2-console
(JDBC URL `jdbc:h2:mem:installments`, user `sa`, empty password).

## Production-style mode

```bash
docker compose up -d          # Postgres + Kafka
SPRING_PROFILES_ACTIVE=postgres mvn spring-boot:run \
  -Dspring-boot.run.jvmArguments="-Dapp.events.kafka-enabled=true"
```

## Layout

- `domain/` — `InstallmentPlan` entity (the ledger), `PlanStatus`
- `dto/` — request/response records (the API never exposes entities)
- `repo/` — Spring Data JPA repository
- `service/` — business logic + amortization math
- `web/` — REST controller + error mapping
- `events/` — `PlanEventPublisher` abstraction: logging default, Kafka impl behind a flag
