# Dental Admin

An admin application for a dental clinic. Staff log in and manage patients, dentists, treatments (price list) and appointments. The app prevents double-booking: a dentist cannot have two overlapping appointments.

- **Backend:** Java Spring Boot (REST API, JWT authentication)
- **Frontend:** React + Vite
- **Database:** PostgreSQL (Neon)

> **Note:** patient data is health data and is sensitive. This project is a learning/demo project. Before using it with real patients, review data protection obligations, use a paid production database, HTTPS, backups and proper access control.

## Features

| Module | What it does |
|---|---|
| Login | Admin logs in with email and password, the session is kept with a JWT token |
| Dashboard | Number of patients and dentists, plus today's appointments |
| Appointments | Pick a date, create and edit appointments, change status (scheduled, completed, cancelled), overlap check per dentist |
| Patients | Create, edit, delete, search by first name, last name or phone |
| Dentists | Create, edit, delete, active/inactive flag |
| Treatments | Price list: name, price (KM) and duration in minutes (the duration decides when an appointment ends) |

## Tech stack

| Part | Technologies |
|---|---|
| Backend | Java 21, Spring Boot 4, Spring Web MVC, Spring Data JPA (Hibernate), Bean Validation, Maven |
| Authentication | JWT (HS256, jjwt), BCrypt (spring-security-crypto) |
| Database | PostgreSQL on Neon |
| Frontend | React, Vite, React Router |

## Project structure

```
dental-admin/
├── backend/                         Spring Boot (pom.xml lives here)
│   └── src/main/java/com/example/backend/
│       ├── BackendApplication.java
│       ├── config/                  WebConfig, PasswordConfig, DataSeeder
│       ├── controller/              Auth, Patient, Dentist, Treatment, Appointment
│       ├── dto/                     request and response records
│       ├── entity/                  Patient, Dentist, Treatment, Appointment, AdminUser
│       ├── exception/               ApiException, GlobalExceptionHandler
│       ├── repository/              database access
│       ├── security/                JwtService, AuthInterceptor
│       └── service/                 AuthService, AppointmentService
├── frontend/                        React + Vite (package.json lives here)
│   └── src/
│       ├── api.js                   calls to the backend
│       ├── components/              Layout, ProtectedRoute, CrudPage
│       └── pages/                   Login, Dashboard, Appointments, Patients, Dentists, Treatments
└── README.md
```

## Prerequisites

1. **Java JDK 21** (https://adoptium.net), verify with `java -version`
2. **Node.js LTS** (https://nodejs.org), verify with `node -v` and `npm -v`
3. **A Neon account** and a PostgreSQL database (https://neon.tech)

Maven does not need to be installed, the project ships with the Maven wrapper (`mvnw`).

## Getting started

### 1. Database

Create a project in Neon, then open **Connect**, turn off **Connection pooling** (the host must not contain `-pooler`) and copy the connection details. The tables are created automatically on the first start (`spring.jpa.hibernate.ddl-auto=update`).

### 2. Backend

Open `backend/src/main/resources/application.properties` and fill in your own host and user from Neon:

```properties
spring.datasource.url=jdbc:postgresql://YOUR_HOST/neondb?sslmode=require
spring.datasource.username=YOUR_USER
spring.datasource.password=${DB_PASSWORD}
```

Do not paste your password into the file. Set the secrets as environment variables and start the app (**PowerShell**):

```powershell
cd backend
$env:DB_PASSWORD="your-database-password"
$env:JWT_SECRET="a-secret-of-at-least-32-characters"
.\mvnw.cmd spring-boot:run
```

Mac / Linux / Git Bash:

```bash
cd backend
export DB_PASSWORD="your-database-password"
export JWT_SECRET="a-secret-of-at-least-32-characters"
./mvnw spring-boot:run
```

The backend is ready when the console shows `Started BackendApplication`. It runs on **http://localhost:8080**.

| Variable | Description |
|---|---|
| `DB_PASSWORD` | password for the Neon database |
| `JWT_SECRET` | secret used to sign tokens, at least 32 characters |
| `ADMIN_PASSWORD` | optional, password of the first admin (default `Admin12345`, change it) |

In IntelliJ they can be set under **Run, Edit Configurations, Environment variables** (`DB_PASSWORD=...;JWT_SECRET=...`).

On the first start, when no admin exists yet, the app creates one with the email from `app.admin.email` (default `admin@ordinacija.ba`). There is no registration page, admins are created by the system owner.

### 3. Frontend

In a new terminal (keep the backend running):

```powershell
cd frontend
npm install
```

Create a file `frontend/.env`:

```
VITE_API_URL=http://localhost:8080
```

Start the dev server:

```powershell
npm run dev
```

Open **http://localhost:5173** and log in with `admin@ordinacija.ba` and your admin password.

## API

All endpoints except login require this header:

```
Authorization: Bearer <token>
```

| Method | Path | Description |
|---|---|---|
| POST | `/api/auth/login` | log in (`email`, `password`), returns `{ token, email }` |
| GET | `/api/patients?q=` | list patients, optional search by name or phone |
| GET, POST | `/api/patients`, `/api/patients/{id}` | get one, create |
| PUT, DELETE | `/api/patients/{id}` | update, delete |
| GET, POST | `/api/dentists` | list, create |
| PUT, DELETE | `/api/dentists/{id}` | update, delete |
| GET, POST | `/api/treatments` | list, create |
| PUT, DELETE | `/api/treatments/{id}` | update, delete |
| GET | `/api/appointments?date=2026-10-20` | appointments for a day (today if no date) |
| POST | `/api/appointments` | create an appointment |
| PUT | `/api/appointments/{id}` | update an appointment |
| PATCH | `/api/appointments/{id}/status` | change status (`SCHEDULED`, `COMPLETED`, `CANCELLED`) |
| DELETE | `/api/appointments/{id}` | delete an appointment |

Create an appointment:

```json
{
  "patientId": 1,
  "dentistId": 1,
  "treatmentId": 1,
  "startTime": "2026-10-20T09:00",
  "note": "First visit"
}
```

The end time is calculated automatically (start time plus treatment duration). If the dentist already has an appointment in that time, the API returns `409`.

### Error format

```json
{ "message": "Provjerite unesene podatke", "fields": { "firstName": "Ime je obavezno" } }
```

The `fields` object is only present for validation errors.

### Quick test from PowerShell

```powershell
$login = Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/auth/login -ContentType "application/json" -Body '{"email":"admin@ordinacija.ba","password":"YOUR_ADMIN_PASSWORD"}'
$h = @{ Authorization = "Bearer $($login.token)" }
Invoke-RestMethod -Uri http://localhost:8080/api/patients -Headers $h
```

## Common problems

| Problem | Fix |
|---|---|
| `Could not resolve placeholder 'DB_PASSWORD'` or `'JWT_SECRET'` | the variable is not set in the same terminal you start the backend from |
| `password authentication failed` | wrong value in `DB_PASSWORD`, reset the password in Neon |
| `Failed to determine a suitable driver class` or `Unable to determine Dialect` | `spring.datasource.url` is wrong or the database is not reachable, check host, user and the `jdbc:postgresql://` prefix |
| `Port 8080 was already in use` | stop the other process: `netstat -ano`, then `taskkill /PID <number> /F` |
| CORS error in the browser | `app.client-url` in `application.properties` must match the frontend address (`http://localhost:5173`), and the backend must be restarted after changes |
| Frontend cannot reach the backend | check `frontend/.env` and restart `npm run dev` (Vite reads `.env` only on start) |
| `mvnw.cmd is not recognized` | run it as `.\mvnw.cmd` in PowerShell, from the folder that contains `pom.xml` |
| First database connection is slow | the Neon free plan suspends idle databases, try again |

## Security

- Passwords and secrets are **never** committed to the repository, only provided through environment variables.
- `.env`, `target/` and `node_modules/` are listed in `.gitignore`.
- Passwords are stored as BCrypt hashes, and all `/api` routes except login require a valid token.
- Change the default admin password (`ADMIN_PASSWORD`) before any real use.

## Ideas for improvement

- Roles (administrator, receptionist, dentist) and Spring Security
- Calendar view of appointments
- Dental records per patient, with documents and images
- Email or SMS reminders
- Database migrations (Flyway) instead of `ddl-auto=update`
- Automated tests (`@WebMvcTest`, `@DataJpaTest`)
- Deployment: backend on Render or Railway, frontend on Vercel or Netlify
