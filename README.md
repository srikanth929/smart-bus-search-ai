# AI Bus Seat Finder

A production-style Spring Boot backend that lets users search for buses and seats using
natural language (e.g. *"Find me a bus from Ongole to Chennai tomorrow with an available
window seat under ₹1000"*). The AI never invents bus, price, or seat data — it always calls
backend tools that hit the real database.

## Tech stack

- Java 21, Spring Boot 3.3
- Spring Web, Spring Data JPA (Hibernate), MySQL
- Spring AI (OpenAI-compatible ChatClient + `@Tool` function calling)
- Lombok, Bean Validation, springdoc-openapi (Swagger UI)
- Maven

## Architecture

```
Client → AIController → AIService (ChatClient)
                              │  tool calling
                 ┌────────────┴─────────────┐
                 ▼                          ▼
          BusSearchTool               SeatSearchTool
                 │                          │
                 ▼                          ▼
            BusService                 SeatService
                 │                          │
                 └───────────┬──────────────┘
                              ▼
                        MySQL (Bus, Seat)
```

The `BusController` / `SeatController` expose the same search/availability logic directly as
plain REST endpoints, so the app works with or without the AI layer.

## Project structure

```
com.example.busai
├── controller   (AIController, BusController, SeatController)
├── service      (AIService, BusService, SeatService)
├── repository   (BusRepository, SeatRepository)
├── entity       (Bus, Seat, SeatType, BusType)
├── dto          (AIChatRequest/Response, BusResponse, SeatResponse, BusSearchRequest/Criteria)
├── ai           (BusSearchTool, SeatSearchTool — the @Tool-annotated Spring AI tools)
├── exception    (GlobalExceptionHandler + domain exceptions)
└── config       (AIConfig, OpenApiConfig, DemoDataLoader)
```

## Running locally

1. **Create the database** (or let it auto-create):
   ```
   CREATE DATABASE bus_ai;
   ```

2. **Set environment variables**:
   ```bash
   export DB_USERNAME=root
   export DB_PASSWORD=your_mysql_password
   export OPENAI_API_KEY=sk-...        # or any Spring-AI-compatible key
   ```
   > Spring AI's OpenAI starter also works against OpenAI-compatible gateways
   > (Azure OpenAI, local proxies, etc.) — just point `spring.ai.openai.base-url`
   > at your provider in `application.yml` if it isn't OpenAI itself.

3. **Run**:
   ```bash
   mvn spring-boot:run
   ```

   On first startup, `DemoDataLoader` inserts 10 demo buses (Ongole, Chennai, Nellore,
   Vijayawada, Hyderabad, Bangalore routes) with 36 seats each — some buses are deliberately
   given zero available window seats so filtering can be demonstrated.

4. **Swagger UI**: http://localhost:8080/swagger-ui.html

## Example requests

### Plain search
```bash
curl "http://localhost:8080/api/buses/search?source=Ongole&destination=Chennai&date=2026-08-21"
```

### Optimized search with seat/price/type filters
```bash
curl "http://localhost:8080/api/buses/search-with-seats?source=Ongole&destination=Chennai&date=2026-08-21&seatType=WINDOW&maxPrice=1000"
```

### All seats on a bus
```bash
curl "http://localhost:8080/api/buses/1/seats"
```

### Available window seats on a bus
```bash
curl "http://localhost:8080/api/buses/1/available-seats?seatType=WINDOW"
```

### AI chat (natural language)
```bash
curl -X POST http://localhost:8080/api/ai/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Find me a bus from Ongole to Chennai tomorrow with an available window seat under 1000"}'
```

Response:
```json
{
  "message": "I found 3 buses matching your requirements: ...",
  "buses": []
}
```
> Note: the current `AIChatResponse.buses` field is reserved for a future enhancement where
> tool-call results are also returned as structured JSON alongside the AI's natural-language
> reply. Today the model's text response already lists real bus/seat details (name, times,
> price, seat number) because it is required to relay only what the tools returned — see the
> system prompt in `AIConfig`.

## Design notes

- **No hallucination guarantee**: the system prompt explicitly forbids inventing data, and the
  only way the model can mention a bus/seat is by calling `searchBuses`,
  `searchBusesWithPreferences`, or `findAvailableSeats`, all of which query MySQL through
  `BusService` / `SeatService`.
- **Optimized query**: `BusRepository.searchWithSeatFilter` uses a single JPQL join query with
  optional (`:param IS NULL OR ...`) filters for seat type, price, bus type, and departure
  window, avoiding N+1 lookups per bus.
- **Seat booking concurrency** (future booking feature, requirement #16): `SeatService.bookSeat`
  demonstrates the intended pattern — a `PESSIMISTIC_WRITE` row lock
  (`SeatRepository.findByIdForUpdate`) plus an `@Version` optimistic-lock column on `Seat` as
  defense in depth, so two concurrent requests can never both book the same seat.
- **DTOs only**: entities are never returned directly from controllers.
- **Error handling**: `GlobalExceptionHandler` covers no-buses-found, no-seats-available,
  invalid source/destination/date/price, DB failures, AI provider failures, and tool-calling
  failures, returning consistent JSON error bodies.

## Extending toward booking

The `SeatService.bookSeat` method and `Seat.version` field are the seam for a future
`BookingService` → `PaymentService` → `NotificationService` flow, as sketched in the original
spec:

```
User → AI → Search Bus → Check Seat → User selects seat → BookingService → PaymentService → Booking Confirmation
```

## Tests

- `BusRepositoryTest` — `@DataJpaTest` verifying the optimized seat-filter query
- `BusControllerTest` — `@WebMvcTest` for the REST endpoints
- `BusServiceTest` / `SeatServiceTest` — unit tests for validation and booking-concurrency logic
- `BusSearchToolTest` — unit test asserting the AI tool only relays real service results

Run all tests:
```bash
mvn test
```
