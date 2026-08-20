🚌 AI Bus Seat Finder

«Find the right bus without checking every seat manually. 🤖✨»

An AI-powered bus search assistant that understands natural-language travel requests and automatically finds buses matching the user's preferences — including window seat availability, budget, bus type, departure time, source, destination, and travel date.

---

💡 The Problem

Imagine you're travelling from Ongole to Chennai and you specifically want a window seat.

Normally, you have to:

🔎 Search buses
     ↓
🚌 Open Bus 1
     ↓
💺 Check seat layout
     ↓
❌ No window seat
     ↓
↩️ Go back
     ↓
🚌 Open Bus 2
     ↓
💺 Check again
     ↓
🔁 Repeat...

This becomes time-consuming and repetitive when there are many buses.

🚀 The Solution

With AI, the user can simply say:

«"Find me a bus from Ongole to Chennai tomorrow with an available window seat under ₹1000."»

The AI understands the request, calls the appropriate backend tools, checks actual seat availability, filters the results, and returns only the matching buses.

---

🤖 How It Works

                    👤 USER
                      │
                      │ Natural Language
                      ▼
              ┌─────────────────┐
              │   🤖 AI CHAT    │
              │   Spring AI     │
              └────────┬────────┘
                       │
                  🧠 Tool Calling
                       │
          ┌────────────┴────────────┐
          ▼                         ▼
   🚌 Bus Search Tool       💺 Seat Search Tool
          │                         │
          └────────────┬────────────┘
                       ▼
                ⚙️ Business Logic
                       │
                       ▼
                  🗄️ MySQL
                       │
                       ▼
              🎯 Matching Results
                       │
                       ▼
                    👤 USER

---

✨ Key Features

🤖 AI-Powered Search

Users can search using normal conversational language.

"Find buses from Ongole to Chennai tomorrow with window seats."

💺 Seat Preference

Search specifically for:

- 🪟 Window seats
- 💺 Aisle seats
- 🛏️ Sleeper seats
- 🪑 Seater seats

💰 Budget Filtering

"Find a bus under ₹1000."

🕐 Time Preferences

"Find a bus leaving after 8 PM."

🚌 Bus Type

"Find an AC sleeper bus with a window seat."

🎯 Combined Preferences

"Find an AC sleeper from Ongole to Chennai tomorrow,
after 8 PM, under ₹1000, with a window seat."

The AI extracts all these requirements automatically.

---

🧠 AI + Backend Architecture

One important principle of this project:

«The AI should never guess real-world availability.»

The LLM is responsible for understanding the user's intent.

The backend is responsible for actual data and business logic.

             👤 User
                │
                ▼
        🤖 Spring AI / LLM
                │
                │ Tool Calling
                ▼
      🔧 Backend Tools
                │
        ┌───────┴────────┐
        ▼                ▼
   🚌 Bus API       💺 Seat API
        │                │
        └───────┬────────┘
                ▼
             🗄️ MySQL
                │
                ▼
        ✅ Real Availability

This prevents AI hallucination of:

❌ Fake buses
❌ Fake prices
❌ Fake seat availability

---

🛠️ Technology Stack

Technology| Purpose
☕ Java 21| Backend development
🍃 Spring Boot| Application framework
🤖 Spring AI| AI integration
🧠 LLM| Natural-language understanding
🔧 Tool Calling| AI → Backend communication
🌐 Spring Web| REST APIs
🗄️ MySQL| Database
🧩 Spring Data JPA| Data persistence
🛡️ Bean Validation| Request validation
📦 Maven| Dependency management
📖 Swagger/OpenAPI| API documentation
🐳 Docker| Containerization
⚡ Redis| Future caching
📨 Kafka| Future event-driven communication

---

📁 Project Structure

ai-bus-seat-finder/
│
├── 📁 src/main/java/com/example/busai
│   │
│   ├── 🎮 controller
│   │   ├── AIController.java
│   │   ├── BusController.java
│   │   └── SeatController.java
│   │
│   ├── ⚙️ service
│   │   ├── AIService.java
│   │   ├── BusService.java
│   │   └── SeatService.java
│   │
│   ├── 🗄️ repository
│   │   ├── BusRepository.java
│   │   └── SeatRepository.java
│   │
│   ├── 📦 entity
│   │   ├── Bus.java
│   │   └── Seat.java
│   │
│   ├── 📋 dto
│   │   ├── AIChatRequest.java
│   │   ├── AIChatResponse.java
│   │   ├── BusResponse.java
│   │   ├── SeatResponse.java
│   │   └── BusSearchCriteria.java
│   │
│   ├── 🤖 ai
│   │   ├── BusSearchTool.java
│   │   └── SeatSearchTool.java
│   │
│   ├── ⚠️ exception
│   │   ├── GlobalExceptionHandler.java
│   │   └── ResourceNotFoundException.java
│   │
│   └── 🔧 config
│       └── AIConfig.java
│
├── 📁 src/main/resources
│   ├── application.yml
│   └── data.sql
│
├── 📄 pom.xml
└── 📄 README.md

---

🗄️ Database Design

🚌 Bus

Bus
├── id
├── busName
├── operatorName
├── source
├── destination
├── travelDate
├── departureTime
├── arrivalTime
├── busType
├── price
└── totalSeats

💺 Seat

Seat
├── id
├── busId
├── seatNumber
├── seatType
├── deck
└── available

Relationship

🚌 BUS
  │
  │ 1
  │
  │
  │ *
  ▼
💺 SEATS

One bus can have multiple seats.

---

🧪 Demo Scenario

👤 User Request

Find me a bus from Ongole to Chennai tomorrow
with an available window seat under ₹1000.

🤖 AI Understanding

{
  "source": "Ongole",
  "destination": "Chennai",
  "date": "2026-08-21",
  "seatType": "WINDOW",
  "maxPrice": 1000
}

🔧 Tool Call

searchBusesWithPreferences(
    source = "Ongole",
    destination = "Chennai",
    date = "2026-08-21",
    seatType = "WINDOW",
    maxPrice = 1000
)

🎯 Result

🚌 Orange Travels
🕐 08:30 PM
💰 ₹850
🪟 Window Seat: 12A

🚌 VRL Travels
🕐 09:15 PM
💰 ₹920
🪟 Window Seat: 7A

🚌 IntrCity SmartBus
🕐 10:00 PM
💰 ₹780
🪟 Window Seat: 15F

🚌 SRS Travels
🕐 11:00 PM
💰 ₹950
🪟 Window Seat: 9A

---

🌐 API Endpoints

🤖 AI Chat

POST /api/ai/chat

Request

{
  "message": "Find a bus from Ongole to Chennai tomorrow with a window seat under 1000"
}

---

🔎 Search Buses

GET /api/buses/search

Example:

GET /api/buses/search?source=Ongole&destination=Chennai&date=2026-08-21

---

🎯 Search Buses With Preferences

GET /api/buses/search-with-seats

Example:

GET /api/buses/search-with-seats?source=Ongole&destination=Chennai&date=2026-08-21&seatType=WINDOW&maxPrice=1000

---

💺 Get Bus Seats

GET /api/buses/{busId}/seats

Example:

GET /api/buses/1/seats

---

🪟 Get Available Window Seats

GET /api/buses/{busId}/available-seats?seatType=WINDOW

---

🔧 Spring AI Tool Calling

The AI can access backend functionality through tools.

@Tool(description = """
Search buses based on source, destination,
date, seat type and maximum price.
""")
public List<BusResponse> searchBusesWithPreferences(
        String source,
        String destination,
        LocalDate date,
        String seatType,
        BigDecimal maxPrice) {

    return busService.searchWithPreferences(
            source,
            destination,
            date,
            seatType,
            maxPrice
    );
}

The AI doesn't directly access the database.

🤖 AI
 ↓
🔧 Tool
 ↓
⚙️ Service
 ↓
🗄️ Repository
 ↓
💾 Database

---

🚀 Example Conversations

Example 1 — Window Seat

👤 User

«Find a bus from Ongole to Chennai tomorrow with a window seat.»

🤖 AI

«I found 4 buses with available window seats. 🪟🚌»

---

Example 2 — Budget

👤 User

«Find the cheapest bus from Ongole to Chennai.»

🤖 AI

«The cheapest available bus is IntrCity SmartBus at ₹780. 💰»

---

Example 3 — Multiple Conditions

👤 User

«Find an AC sleeper from Ongole to Chennai after 8 PM, under ₹1000, with a window seat.»

🤖 AI

«I found 3 buses matching all your requirements. 🎯»

---

Example 4 — No Availability

👤 User

«Find a window seat on the 6 PM bus.»

🤖 AI

«I couldn't find an available window seat on the 6 PM buses. Would you like me to check buses between 7 PM and 10 PM? 🔎»

---

🔐 Important Design Principle

❌ Don't do this

User
 ↓
LLM
 ↓
"Seat 12A is available"

The AI could hallucinate.

✅ Do this

User
 ↓
LLM
 ↓
Tool Calling
 ↓
Backend API
 ↓
Database
 ↓
Actual Seat Availability
 ↓
LLM
 ↓
User

«AI understands the user.
Your backend knows the truth. 💡»

---

🔮 Future Enhancements

🎫 Booking

AI Search
    ↓
Select Bus
    ↓
Select Seat
    ↓
🔒 Lock Seat
    ↓
💳 Payment
    ↓
🎫 Booking Confirmation

⚡ Redis

Cache frequently searched routes:

Ongole → Chennai
Hyderabad → Chennai
Vijayawada → Chennai

📨 Kafka

Publish events such as:

SEAT_BOOKED
SEAT_RELEASED
BOOKING_CONFIRMED
PAYMENT_SUCCESS

🔔 Notifications

Send:

- 📱 SMS
- 📧 Email
- 🔔 Push notification

🧠 Personalized Recommendations

The AI could eventually learn preferences such as:

Preferred:
🪟 Window seat
🛏️ Sleeper
❄️ AC
💰 Budget < ₹1000
🌙 Night travel

Then the user could simply say:

«"Find me a bus to Chennai."»

And the system could automatically apply their preferences.

---

🗺️ Future Microservices Architecture

                         👤 User
                           │
                           ▼
                    🤖 AI Assistant
                           │
                    API Gateway
                           │
          ┌────────────────┼────────────────┐
          ▼                ▼                ▼
     🚌 Bus Service   💺 Seat Service   👤 User Service
          │                │                │
          └────────────────┼────────────────┘
                           │
                           ▼
                    🎫 Booking Service
                           │
                           ▼
                     💳 Payment Service
                           │
                           ▼
                   🔔 Notification Service

---

🎯 Project Goal

The goal of AI Bus Seat Finder is not simply to add a chatbot to a bus booking application.

It is to provide a natural-language interface over existing travel services, allowing users to express what they want instead of manually navigating through multiple screens.

«Search less. Choose faster. Travel smarter. 🚌🤖»

---

👨‍💻 Author

Srikanth

🎓 Computer Science & Engineering
☕ Java Backend Developer
🍃 Spring Boot | Spring AI | Microservices
🧠 DSA | AI Engineering

---

⭐ If you find this project interesting

Feel free to ⭐ the repository and share your ideas!

Built with ☕ Java + 🍃 Spring Boot + 🤖 Spring AI
