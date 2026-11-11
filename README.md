# CloudHub ZhiXue — Build & Run Guide

## Prerequisites
- Java 21+
- Maven 3.8+
- Node.js 18+ / npm 9+
- Docker & Docker Compose

---

## 1. Start Infrastructure (MySQL + Redis)

```bash
cd "D:\IDE\1.1Test\VibeCodingTest\云枢智学"
docker compose up -d
```

Wait until MySQL is ready:
```bash
docker compose logs -f mysql
# Wait for: "ready for connections."
```

The database `yunshu_db` and all 22 tables will be auto-created from `scripts/sql/schema.sql`.

---

## 2. Backend Development

```bash
cd backend
mvn clean install -DskipTests
cd yunshu-admin
mvn spring-boot:run
```

Backend starts at **http://localhost:8080**  
Swagger UI at **http://localhost:8080/swagger-ui.html**

---

## 3. Frontend Development

### Portal (Student Side) — :5173
```bash
cd frontend/portal
npm install
npm run dev
```
Open http://localhost:5173

### Admin (Management Side) — :3001
```bash
cd frontend/admin
npm install
npm run dev
```
Open http://localhost:3001

---

## 4. Production Build

```bash
# Build both frontends
cd frontend/portal && npm run build
cd ../admin && npm run build

# Package backend (static files are embedded via Maven resource filtering)
cd ../../backend
mvn clean package -DskipTests
java -jar yunshu-admin/target/yunshu-admin.jar --spring.profiles.active=prod
```

---

## 5. Environment Variables (.env)

Copy `.env.example` → `.env` and adjust as needed:

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_ROOT_PASSWORD` | root123456 | MySQL root password |
| `DB_NAME` | yunshu_db | Database name |
| `DB_PORT` | 3307 | MySQL host port |
| `REDIS_PASSWORD` | redis123456 | Redis password |
| `JWT_STUDENT_SECRET` | — | Student JWT signing secret (256-bit+) |
| `JWT_ADMIN_SECRET` | — | Admin JWT signing secret (256-bit+) |
| `AI_BASE_URL` | https://api.openai.com/v1 | OpenAI-compatible API base URL |
| `AI_API_KEY` | — | LLM API key |
| `MINIO_ENABLED` | false | Enable MinIO file storage |
| `UPLOAD_DIR` | ./uploads | Local file upload directory |

---

## 6. Project Structure

```
云枢智学/
├── docs/                    # Design documents & specs
│   ├── rules/               # Database/API design rules
│   └── design/              # Architecture, DB, API docs
├── prototype/               # Static HTML/CSS/JS prototypes (read-only reference)
├── scripts/sql/             # Schema SQL
├── backend/                 # Spring Boot 3 + MyBatis multi-module
│   ├── pom.xml              # Parent POM
│   ├── yunshu-common/       # Shared enums, utils, exceptions
│   ├── yunshu-framework/    # Security, rate-limit, cache
│   ├── yunshu-system/       # Users, roles, subjects, catalogs
│   ├── yunshu-question/     # Question bank business
│   ├── yunshu-interview-question/  # Interview question bank
│   ├── yunshu-course/       # Course management
│   ├── yunshu-route/        # Study route management
│   ├── yunshu-portal/       # Student-facing controllers
│   ├── yunshu-file/         # File upload service
│   ├── yunshu-schedule/     # Scheduled tasks
│   └── yunshu-admin/        # Main app (Spring Boot entry point)
├── frontend/
│   ├── portal/              # Vue 3 + TS + Vite + Element Plus (student side)
│   └── admin/               # Vue 3 + TS + Vite + Element Plus (admin side)
└── docker-compose.yml       # MySQL + Redis stack
```

---

## 7. Default Accounts

| Role | Username | Password |
|------|----------|----------|
| Super Admin | `super_admin` | `admin123456` |

> First login should force password change. The BCrypt hash for `admin123456` is pre-loaded in `schema.sql`.
