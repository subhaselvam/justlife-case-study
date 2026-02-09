# Justlife Case Study - Cleaning Booking (Spring Boot 3 + Java 21 + Gradle + PostgreSQL + Flyway)

## Run with Docker
```bash
docker compose up -d --build
```

Swagger UI: http://localhost:8080/swagger-ui.html

## APIs (current)
Base path: `v1`

### Date-only availability
Returns the list of professionals and their available time windows for the given date.

`GET /v1/availability?date=2026-02-14`

### Exact-slot availability
Returns only professionals available for the given exact slot.

`POST /v1/availability`
```json
{ "start": "2026-02-14T10:00:00", "durationMinutes": 120 }
```

### Booking create
`POST /v1/bookings/`
```json
{
  "start": "2026-02-14T10:00:00",
  "durationMinutes": 120,
  "professionalCount": 2,
  "customerId": 123,
  "preferredProfessionalGender": "FEMALE",
  "notes": "Please call on arrival"
}
```

### Booking get
`GET /v1/bookings/id/{id}`

### Booking update
`PUT /v1/bookings/id/{id}`
```json
{
  "start": "2026-02-14T14:00:00",
  "durationMinutes": 240,
  "preferredProfessionalGender": "MALE",
  "notes": "Updated notes"
}
```

## Notes
- Uses **LocalDateTime** end-to-end.
- Availability is computed from persisted bookings/assignments (no separate availability table).
- Business rules:
  - Working hours: 08:00–22:00
  - Not working on Fridays
  - 30-min break buffer around each booking per professional
  - Booking duration must be **120 or 240** minutes
  - professionalCount must be **1..3**
  - Multi-professional bookings must use professionals from the same vehicle
- Seed data: 5 vehicles, 25 professionals (5 per vehicle) via Flyway V2 migration.

## Tests
```bash
./gradlew test
```

## Enhancements for Production

### Currently Implemented (Basic Structure)
For this case study, we've kept a simplified model with default data. The following entities are designed for future enhancement:

- **Region**: Geographic service areas (currently using default region)
- **Service**: Cleaning service types (deep cleaning, regular cleaning, etc.)
- **Service Category**: Service classifications (residential, commercial, etc.)
- **Preferred Gender**: Professional gender preference (MALE, FEMALE, NO_PREFERENCE)
- **Vehicle**: Professional transportation and grouping (5 vehicles seeded)
- **Professional**: Service providers with skills and availability (25 professionals seeded)

These entities provide the foundation for a more sophisticated booking system but currently use default/seed values for demonstration purposes.

## Design Patterns Used

### 1. **Service Layer Pattern**
- Clear separation between controllers and business logic
- Services: `BookingService`, `AvailabilityService`, `ProfessionalService`
- Encapsulates business rules and orchestrates data access

### 2. **Repository Pattern**
- Data access abstraction using Spring Data JPA
- Repositories: `BookingRepository`, `ProfessionalRepository`, `VehicleRepository`
- Custom query methods for complex availability checks

### 3. **DTO Pattern (Data Transfer Objects)**
- Request/Response DTOs separate from domain entities
- Clean API contracts independent of database schema
- Examples: `BookingRequest`, `BookingResponse`, `AvailabilityQueryRequest`

### 4. **Strategy Pattern**
- Availability calculation strategies for different scenarios
- Time slot computation and conflict detection
- Gender preference filtering strategy

### 5. **Builder Pattern** (via Lombok)
- Entity and DTO construction using `@Builder` annotation
- Improves readability and maintainability

### 6. **Singleton Pattern**
- Test container management (`PostgresTestContainer`)
- Single database container shared across all integration tests

### 7. **Template Method Pattern**
- `IntegrationTestBase` provides test structure
- Subclasses inherit common test configuration

## Assumptions

1. **Time Management**
   - All times are in Asia/Kolkata timezone (IST)
   - Working hours: 08:00 to 22:00 daily except Fridays
   - 30-minute break buffer required between bookings per professional
   - Bookings can only be 120 or 240 minutes duration

2. **Professional Management**
   - Professionals belong to exactly one vehicle
   - Multi-professional bookings require professionals from the same vehicle
   - Maximum 3 professionals can be assigned to a single booking
   - Gender preference is optional but recommended for customer satisfaction

3. **Vehicle Management**
   - Each vehicle has 5 professionals (current seed data)
   - Vehicle assignment is static (no rotation implemented yet)
   - Professionals cannot be shared across vehicles

4. **Booking Rules**
   - A professional can only have one active booking at a time
   - Bookings cannot overlap even with break buffer
   - Customer can specify gender preference (MALE, FEMALE, NO_PREFERENCE)
   - Bookings can be updated but not cancelled (soft delete not implemented)

5. **Data Seeding**
   - Default region, service category, and service are created via migration
   - 5 vehicles and 25 professionals seeded for testing
   - Real production would have dynamic data management

## Future Enhancements

### High Priority

#### 1. **Vehicle Rotation and Assignment Management**
- **Problem**: Currently, professionals are statically assigned to vehicles
- **Enhancement**:
  - Dynamic vehicle assignment based on geographic zones
  - Vehicle rotation scheduling (daily/weekly patterns)
  - Real-time vehicle tracking and location-based assignment
  - Maintenance scheduling and vehicle downtime management
  - Professional reassignment when vehicles are unavailable
- **Implementation**:
  ```java
  - VehicleRotationService: Manage daily vehicle schedules
  - VehicleAssignmentStrategy: Assign vehicles based on booking location
  - VehicleMaintenanceScheduler: Handle vehicle downtime
  ```

#### 2. **Priority-Based Booking Updates**
- **Problem**: All booking updates are treated equally, no priority system
- **Enhancement**:
  - VIP customer priority for rebooking
  - Emergency rescheduling capabilities
  - Automatic professional reallocation on high-priority changes
  - Notification system for affected bookings
  - Compensation/discount logic for priority bumps
- **Implementation**:
  ```java
  - BookingPriorityService: Manage booking priorities
  - CustomerTierService: Track customer loyalty levels
  - RebookingStrategy: Handle cascade effects of priority updates
  - NotificationService: Alert affected parties
  ```

#### 3. **Booking Cancellation and Soft Delete**
- Implement booking cancellation with cancellation policies
- Soft delete for audit trails
- Refund/credit management
- Last-minute cancellation penalties

#### 4. **Professional Availability Management**
- Professionals can mark time off/vacation
- Sick leave management
- Personal unavailability slots
- Shift preference management

### Medium Priority

#### 5. **Advanced Scheduling Optimization**
- **Route Optimization**: Minimize travel time between bookings
- **Load Balancing**: Distribute bookings evenly across professionals
- **Smart Suggestions**: Recommend optimal time slots to customers
- **Buffer Time Optimization**: Dynamic break times based on booking complexity

#### 6. **Multi-Region Support**
- Region-based service availability
- Different working hours per region
- Regional pricing and service offerings
- Cross-region booking restrictions

#### 7. **Service Catalog Management**
- Multiple service types (deep clean, regular clean, move-in/out)
- Service categories (residential, commercial, industrial)
- Service-specific duration rules
- Add-on services and packages
- Dynamic pricing based on service complexity

#### 8. **Professional Skills and Matching**
- Skill-based professional assignment
- Certification requirements per service type
- Performance ratings and customer feedback
- Preferred professional lists per customer

#### 9. **Real-Time Booking Management**
- WebSocket support for real-time availability updates
- Live booking status tracking
- GPS-based professional location tracking
- Estimated arrival time calculations

### Low Priority

#### 10. **Analytics and Reporting**
- Booking trends and patterns
- Professional utilization rates
- Revenue analytics
- Customer satisfaction metrics
- Vehicle efficiency reports

#### 11. **Customer Enhancements**
- Recurring booking support (weekly/monthly cleaning)
- Customer preferences and history
- Loyalty program integration
- Multi-location support per customer

#### 12. **Payment Integration**
- Payment gateway integration
- Deposit and balance management
- Refund processing
- Invoice generation

#### 13. **Communication System**
- SMS/Email notifications
- In-app messaging between customers and professionals
- Automated reminders
- Feedback collection

## Technical Debt and Improvements

### Performance
- Implement caching for availability queries (Redis/Caffeine)
- Database query optimization and indexing
- Pagination for large result sets
- Async processing for non-critical operations

### Security
- Authentication and authorization (OAuth2/JWT)
- Role-based access control (RBAC)
- API rate limiting
- Input sanitization and validation improvements

### Monitoring
- Application performance monitoring (APM)
- Distributed tracing
- Health checks and alerting
- Logging improvements (structured logging)

### Testing
- Increase test coverage (target: >80%)
- Performance/load testing
- Contract testing with consumers
- Chaos engineering for resilience

### Architecture
- Event-driven architecture for booking state changes
- CQRS for read/write separation
- Saga pattern for distributed transactions
- Circuit breaker for external service calls

## Technology Stack

- **Java 21** with modern features
- **Spring Boot 3.3.2** for application framework
- **PostgreSQL 16** for persistent storage
- **Flyway** for database migrations
- **Testcontainers** for integration testing
- **Lombok** for boilerplate reduction
- **Swagger/OpenAPI** for API documentation
- **Gradle 8.10** for build automation

## Configuration Management

### Application Properties
- Main configuration: `src/main/resources/application.yml`
- Test configuration: `src/test/resources/application-test.yml`
- Environment-specific overrides via environment variables
- Database credentials externalized for security

### Test Configuration
All test database properties (username, password, image version, etc.) are now centralized in `application-test.yml` for easy management and consistency across test suites.
