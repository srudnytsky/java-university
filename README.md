# 🎓 University Management System API

RESTful API for managing students, teachers, courses and enrollments.  
Built with **Spring Boot 3**, **Spring Data JPA**, **PostgreSQL**, documented via **Swagger UI**.

---

## 🚀 Tech Stack

| Technology | Version |
|---|---|
| Java | 21 (Temurin) |
| Spring Boot | 3.3.5 |
| Spring Data JPA | 3.3.5 |
| PostgreSQL | 15+ |
| Springdoc OpenAPI | 2.6.0 |
| Lombok | latest |
| H2 (tests) | latest |
| JUnit 5 + Mockito | latest |

---

## ⚙️ Setup & Run

### 1. Prerequisites
- Java 21+
- Maven 3.9+
- PostgreSQL running locally

### 2. Create the database
```sql
CREATE DATABASE university_db;
```

### 3. Configure credentials
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/university_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### 4. Run
```bash
mvn spring-boot:run
```

### 5. Run tests
```bash
mvn test
```

---

## 📖 API Documentation

After startup, open:  
**Swagger UI:** http://localhost:8080/swagger-ui.html  
**OpenAPI JSON:** http://localhost:8080/api-docs

---

## 🗂️ Project Structure

```
src/main/java/ua/university/sms/
├── controller/          # REST endpoints (StudentController, TeacherController, CourseController, EnrollmentController)
├── service/             # Business logic + GPA calculation
├── repository/          # Spring Data JPA interfaces
├── model/
│   ├── entity/          # JPA entities + enums (StudentStatus, TeacherPosition, Grade)
│   └── dto/             # Request/Response DTOs
└── exception/           # GlobalExceptionHandler + custom exceptions
```

---

## 📡 Endpoints Overview

### Students `/api/students`
| Method | URL | Description |
|---|---|---|
| POST | `/api/students` | Create student |
| GET | `/api/students` | List all (filter: `?status=ACTIVE&year=2022`, pagination) |
| GET | `/api/students/{id}` | Get by ID |
| PUT | `/api/students/{id}` | Update |
| DELETE | `/api/students/{id}` | Delete |
| GET | `/api/students/search?q=` | Search by name/email |
| GET | `/api/students/unpaid` | Students with unpaid enrollments |
| GET | `/api/students/top?n=10` | Top N students by GPA |
| GET | `/api/students/{id}/transcript` | Full transcript + GPA |

### Teachers `/api/teachers`
| Method | URL | Description |
|---|---|---|
| POST | `/api/teachers` | Create teacher |
| GET | `/api/teachers` | List all (filter: `?position=PROFESSOR`) |
| GET | `/api/teachers/{id}` | Get by ID |
| PUT | `/api/teachers/{id}` | Update |
| DELETE | `/api/teachers/{id}` | Delete |

### Courses `/api/courses`
| Method | URL | Description |
|---|---|---|
| POST | `/api/courses` | Create course |
| GET | `/api/courses` | List all (filter: `?teacherId=1&credits=4`) |
| GET | `/api/courses/{id}` | Get by ID |
| PUT | `/api/courses/{id}` | Update |
| DELETE | `/api/courses/{id}` | Delete |

### Enrollments `/api/enrollments`
| Method | URL | Description |
|---|---|---|
| POST | `/api/enrollments` | Create enrollment (grade=NA, paid=false) |
| GET | `/api/enrollments` | List all |
| GET | `/api/enrollments/{id}` | Get by ID |
| GET | `/api/enrollments/student/{id}` | By student |
| GET | `/api/enrollments/course/{id}` | By course |
| PUT | `/api/enrollments/{id}/grade` | Set grade |
| PUT | `/api/enrollments/{id}/paid` | Mark as paid |
| DELETE | `/api/enrollments/{id}` | Delete |
| GET | `/api/enrollments/gpa/course/{id}` | Avg GPA by course |
| GET | `/api/enrollments/gpa/semester?semester=` | Avg GPA by semester |

---

## 🧮 GPA Calculation

| Grade | Points |
|---|---|
| A | 4.0 |
| B | 3.0 |
| C | 2.0 |
| D | 1.0 |
| F | 0.0 |
| NA | not counted |

GPA = Σ(grade_points × credits) / Σ(credits) — weighted by course credits.

---

## ✅ Checklist

- [x] CRUD for Student, Teacher, Course
- [x] Enrollment with grade & payment management
- [x] GPA calculation (weighted by credits)
- [x] Transcript endpoint
- [x] Filtering by status, year, teacher, credits
- [x] Search by name/email
- [x] Students with unpaid courses
- [x] Average GPA by course/semester
- [x] Top-N students by GPA
- [x] DTOs everywhere (no JPA entities exposed)
- [x] Swagger UI with all endpoints described
- [x] GlobalExceptionHandler with proper HTTP codes
- [x] Unit tests (Mockito) + Integration tests (MockMvc + H2)
- [x] Layered architecture: Controller → Service → Repository
- [x] Payable interface implemented on Enrollment
