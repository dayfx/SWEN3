# Paperless - Document Management System

## Architecture

![Architecture Diagram](public/semester-project-architecture.png)

### Project Structure

```
SWEN3/
├── openapi/
│   └── openapi.yaml              # API Specification
│
├── PaperlessREST/                # REST API (Port 8081)
│   ├── src/main/java/com/fhtechnikum/paperless/
│   │   ├── controller/           # REST Controllers
│   │   ├── services/             # Business Logic + Mappers
│   │   ├── persistence/          # Entities + Repositories
│   │   ├── messaging/            # RabbitMQ Producer
│   │   └── config/               # Configuration Classes
│   └── src/test/java/            # Unit + Integration Tests
│
├── PaperlessServices/            # Worker Services (Port 8082)
│   ├── src/main/java/com/fhtechnikum/paperlessservices/
│   │   ├── services/             # OCR + GenAI Workers
│   │   ├── persistence/          # Elasticsearch Repository
│   │   └── config/               # RabbitMQ + MinIO Config
│   └── src/test/java/            # Unit Tests
│
├── web-ui/                       # Frontend (Port 80)
│   ├── static/
│   │   ├── js/                   # JavaScript (documents.js, upload.js)
│   │   └── css/                  # Stylesheets
│   ├── Dockerfile
│   └── nginx.conf
│
├── docs/                         # Documentation
├── .env.example                  # Environment template
├── docker-compose.yml
└── README.md
```

### Services

| Service | Port | Description |
|---------|------|-------------|
| web-ui | 80 | Frontend (nginx) |
| paperless-rest | 8081 | REST API Backend |
| paperless-services | 8082 | OCR + GenAI Worker |
| postgres | 5432 | PostgreSQL Database |
| queue | 5672, 15672 | RabbitMQ Message Broker |
| minio | 9000, 9090 | MinIO Object Storage |
| elasticsearch | 9200 | Elasticsearch Search Engine |
| kibana | 5601 | Kibana Dashboard |

## Prerequisites

- Java JDK 21+
- Maven 3.8+
- Docker Desktop
- Docker Compose
- Git
- Google Gemini API Key (for AI summaries)

## Installation

### 1. Clone Repository

```bash
git clone <repository-url>
cd SWEN3
```

### 2. Environment Variables

Copy the example environment file:
```bash
cp .env.example .env
```

Edit `.env` and add your Google Gemini API key:
```bash
GEMINI_API_KEY=your-api-key-here
```

### 3. Build and Start

```bash
# Build Docker images
docker compose build

# Start all services
docker compose up -d
```

## Usage

### Access Points

| Service | URL | Credentials |
|---------|-----|-------------|
| Frontend | http://localhost | - |
| REST API | http://localhost:8081 | - |
| Swagger UI | http://localhost:8081/swagger-ui.html | - |
| RabbitMQ | http://localhost:15672 | guest / guest |
| MinIO Console | http://localhost:9090 | minioadmin / minioadmin |
| Kibana | http://localhost:5601 | - |

### Upload a Document

1. Go to http://localhost/upload.html
2. Select a PDF/DOCX/TXT file (max 10MB)
3. Enter title and author
4. Click "Upload"
5. The document will be processed automatically (OCR + AI Summary)

### Search Documents

1. Go to http://localhost/documents.html
2. Enter a search term in the search bar
3. Results include matches from document content AND notes

### Add Notes to Documents

1. Click on a document card to open details
2. Scroll down to the "Notes" section
3. Enter your note and click "Add Note"
4. Notes are searchable via Elasticsearch

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/documents | Get all documents |
| GET | /api/documents/{id} | Get document by ID |
| POST | /api/documents | Upload new document |
| PUT | /api/documents/{id} | Update document |
| DELETE | /api/documents/{id} | Delete document |
| GET | /api/documents/search?query= | Search documents |
| GET | /api/documents/{id}/notes | Get notes for document |
| POST | /api/documents/{id}/notes | Add note to document |
| DELETE | /api/notes/{id} | Delete note |

## Testing

### Run Integration Tests

```bash
cd PaperlessREST
mvn test -Dtest="*IntegrationTest"
```


### Run All Tests

```bash
mvn test
```

