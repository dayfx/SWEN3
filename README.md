# Paperless - Document Management System

## Architecture

![Architecture Diagram](public/semester-project-architecture.png)

### Project Structure

```
SWEN3/
├── openapi/
│   ├── openapi.yaml              # API Specification
│   └── openapi-gen.sh            # Code Generation
│
├── PaperlessREST/                # REST API (Port 8081)
│   ├── src/main/java/com/fhtechnikum/paperless/
│   │   ├── controller/
│   │   ├── services/
│   │   ├── persistence/
│   │   ├── messaging/
│   │   └── PaperlessRestApplication.java
│   ├── src/main/resources/
│   │   └── application.properties
│   ├── Dockerfile
│   └── pom.xml
│
├── PaperlessServices/            # Worker Services (Port 8082)
│   ├── src/main/java/com/fhtechnikum/paperlessservices/
│   │   ├── consumer/
│   │   ├── config/
│   │   └── PaperlessServicesApplication.java
│   ├── src/main/resources/
│   │   └── application.properties
│   ├── Dockerfile
│   └── pom.xml
│
├── web-ui/                       # Frontend (Port 80)
│   ├── static/
│   ├── Dockerfile
│   └── nginx.conf
│
├── docker-compose.yml
├── pom.xml
└── README.md
```

### Services

| Service | Port | Description |
|---------|------|-------------|
| web-ui | 80 | Frontend |
| paperless-rest | 8081 | REST API Backend |
| paperless-services | 8082 | OCR Worker |
| postgres | 5432 | PostgreSQL Database |
| queue | 5672, 15672 | RabbitMQ |

## Prerequisites

- Java JDK 21+
- Maven 3.8+
- Docker Desktop
- Docker Compose
- Git

## Installation

### Clone Repository

```bash
git clone <repository-url>
cd SWEN3
```

### Environment Variables

(will be changed)

Currently all environment variables are hardcoded directly in `docker-compose.yml`.

### Build Project

```bash
mvn clean install
```

### Build Docker Images

```bash
docker compose build
```

## Starting the Project

### With Docker Compose

```bash
docker compose up
```

All services start in foreground.

```bash
docker compose up -d
```

All services start in background.


## Access

After starting, the following URLs are available:

- Frontend: http://localhost:80
- REST API: http://localhost:8081
- RabbitMQ Management: http://localhost:15672 (guest/guest)
- PostgreSQL: localhost:5432 (paperless/paperless)

## Development

### OpenAPI Code Generation

```bash
cd openapi
./openapi-gen.sh
```


### Compile MapStruct

```bash
mvn clean compile
```

## Environment Variables

(will be changed)

### PaperlessREST

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_RABBITMQ_HOST`
- `SPRING_RABBITMQ_PORT`
- `SPRING_RABBITMQ_USERNAME`
- `SPRING_RABBITMQ_PASSWORD`

### PaperlessServices

- `SPRING_RABBITMQ_HOST`
- `SPRING_RABBITMQ_PORT`
- `SPRING_RABBITMQ_USERNAME`
- `SPRING_RABBITMQ_PASSWORD`

### PostgreSQL

- `POSTGRES_DB`
- `POSTGRES_USER`
- `POSTGRES_PASSWORD`

## Configuration

### Database

Current: `spring.jpa.hibernate.ddl-auto=create-drop`

Database is deleted on every restart.


