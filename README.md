# Notion Template API

A RESTful API designed to power a Notion-style productivity application. This backend service manages projects, tasks, user authentication, and statistics, built with modern Java technologies and containerized for easy deployment.

## Tech Stack

- **Java 21**: The latest LTS version for high performance and modern features.
- **Spring Boot 3.4.1**: Framework for building production-ready applications.
  - **Spring Security**: Robust authentication and access control.
  - **Spring Data JPA**: Efficient database interaction using Hibernate.
  - **Spring Validation**: Data integrity and input validation.
- **PostgreSQL**: Reliable object-relational database system.
- **JWT (JSON Web Token)**: Stateless authentication mechanism.
- **Docker & Docker Compose**: Containerization for consistent development and deployment environments.
- **Lombok**: Boilerplate code reduction.

## Key Features

- **Authentication & Security**: 
  - User registration and login.
  - Secure password hashing.
  - JWT-based stateless session management.
- **Project & Task Management**:
  - CRUD operations for Projects and Tasks.
  - "High" priority and status tracking.
- **Calendar Integration**: Manage dates and schedules.
- **User Dashboard**:
  - Profile management.
  - Statistics and analytics (UserStats).
- **Relational Data**: Complex relationships handling between Users, Projects, and Tasks.

## Prerequisites

- **Docker Desktop**: Recommended for the easiest setup.
- **Java 21 JDK** (Only if running without Docker).
- **Maven** (Only if running without Docker).

## Getting Started

### Option 1: Run with Docker (Recommended)

This project handles the database and application setup automatically using Docker Compose.

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/notion-template-api.git
   cd notion-template-api
   ```

2. **Start the application:**
   ```bash
   docker-compose up -d --build
   ```
   *This command spins up a PostgreSQL container and the Spring Boot backend.*

3. **Access the API:**
   The API will be available at `http://localhost:8080`.

### Option 2: Run Locally

If you prefer to run the application directly on your machine:

1. **Start the Database:**
   Ensure you have a PostgreSQL instance running. You can use the `docker-compose.yml` just for the database:
   ```bash
   docker-compose up -d postgres
   ```

2. **Configure Environment:**
   Edit `src/main/resources/application.properties` or set environment variables if necessary (refer to `docker-compose.yml` for required keys).

3. **Run the Application:**
   ```bash
   ./mvnw spring-boot:run
   ```

## API Resources

The API is organized around the following main resources:

- **Auth** (`/auth`): Handle user signup and signin.
- **Projects** (`/projects`): Manage user projects.
- **Tasks** (`/tasks`): Create and track tasks associated with projects.
- **Calendar** (`/calendar`): Date-specific data and events.
- **User & Profile** (`/users`, `/profile`): Manage user data and settings.
- **Stats** (`/stats`): Retrieve productivity statistics.

## Environment Variables

The application uses the following key environment variables (defaults provided in `docker-compose.yml` for development):

| Variable | Description | Default (Dev) |
|----------|-------------|---------------|
| `DATABASE_URL` | JDBC URL for PostgreSQL | `jdbc:postgresql://postgres:5432/notion_db` |
| `DATABASE_USER` | Database Username | `postgres` |
| `DATABASE_PASSWORD` | Database Password | `postgres` |
| `JWT_SECRET` | Secret key for signing tokens | *(dev-secret...)* |
| `JWT_EXPIRATION_HOURS` | Token validity duration | `2 horas` |
| `CORS_ALLOWED_ORIGINS` | Allowed frontend origins | `http://localhost:4200` |

## Contributing

Contributions are welcome! Please fork the repository and submit a Pull Request.

---
*Built by Sebastian*
