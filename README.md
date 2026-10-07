# Movie Recommendation Platform

A RESTful backend service for a movie-focused social platform where users can discover films, share opinions, build friendships, and track activity through a personalized feed.

## Features

**Movies**
- Create, update and retrieve movies
- MPA age rating classification
- Genre and director filtering and sorting
- Movie popularity ranking based on likes
- Search by title

**Users & Social**
- User registration and profile management
- Friendship system — send, confirm and remove friends
- View shared movie interests between users

**Reviews & Likes**
- Add, update and delete reviews
- Like and dislike reviews
- Aggregate review usefulness rating

**Activity Feed**
- Chronological feed of user actions
- Events for friendships, likes and reviews

## Tech Stack

- **Java 21**
- **Spring Boot 3.2**
- **H2** — embedded database with schema and seed data
- **Spring JDBC** — explicit SQL, no ORM
- **Logbook 3.7** — HTTP request/response logging
- **Spring Validation** — declarative request validation
- **Lombok** — boilerplate reduction
- **JUnit 5** — unit and integration tests
- **Maven**

## Architecture

The application follows a layered architecture:

**Controller** → **Service** → **DAO/Repository** → **H2**

- `controller` — REST endpoints, request/response handling
- `service` — business logic across films, users, reviews, events
- `dao` — data access layer with repository abstractions and row mappers
- `model` — core domain entities
- `dto` — API-facing models decoupled from domain
- `validation` — custom validators
- `exception` — custom exceptions and global error handler

## Getting Started

### Requirements
- Java 21 or higher
- Maven installed

### Run locally

```bash
git clone https://github.com/alonazrnko/movie-social-api.git
cd movie-social-api
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

Database schema and seed data are initialized automatically on startup via `schema.sql` and `data.sql`.

### Run tests

```bash
mvn test
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/films` | Get all films |
| POST | `/films` | Add a new film |
| PUT | `/films/{id}/like/{userId}` | Like a film |
| GET | `/films/popular` | Get popular films |
| GET | `/films/search` | Search films by title |
| GET | `/users` | Get all users |
| POST | `/users` | Create a user |
| PUT | `/users/{id}/friends/{friendId}` | Add friend |
| GET | `/users/{id}/feed` | Get user activity feed |
| POST | `/reviews` | Add a review |
| PUT | `/reviews/{id}/like/{userId}` | Like a review |

## Testing

- Repository integration tests for all core entities — `FilmRepositoryTest`, `UserRepositoryTest`, `ReviewRepositoryTest` and others
- Tests run against in-memory H2 database — no external dependencies required
- Full application context test via `FilmorateApplicationTests`

## Key Design Decisions

- **Spring JDBC over JPA** — explicit SQL queries give full control and make data access behavior transparent and predictable
- **Repository abstractions** — base repository layer shared across entities reduces duplication in data access code
- **H2 with schema.sql and data.sql** — database is fully reproducible from source, no manual setup needed
- **Logbook** — all HTTP traffic is logged automatically, simplifying debugging without manual logging in controllers
- **EventService** — activity feed is implemented as a dedicated service that records user actions across all domains
