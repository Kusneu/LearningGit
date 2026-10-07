# TaskForge: Developer Project & Task Tracker

TaskForge is a modular, medium-sized Java 21 console application engineered for practicing Git workflows, version control commands, branching strategies, and collaboration techniques.

---

## 🏗️ Architecture & Modules

```
src/
├── main/java/com/taskforge/
│   ├── model/                  # Domain entities & enums
│   │   ├── User.java           # User profile & credentials
│   │   ├── Role.java           # ADMIN, DEVELOPER, QA, MANAGER
│   │   ├── Task.java           # Task item with priority, tags, status
│   │   ├── TaskPriority.java   # LOW, MEDIUM, HIGH, CRITICAL
│   │   ├── TaskStatus.java     # TODO, IN_PROGRESS, IN_REVIEW, DONE, BLOCKED
│   │   ├── Project.java        # Project group & member management
│   │   ├── Comment.java        # Discussion comments on tasks
│   │   └── AuditLog.java       # System audit & event log
│   ├── repository/             # Generic persistence contracts & stores
│   │   ├── Repository.java     # Generic CRUD interface
│   │   ├── InMemoryRepository.java # Thread-safe concurrent map storage
│   │   ├── UserRepository.java
│   │   ├── ProjectRepository.java
│   │   ├── TaskRepository.java
│   │   └── AuditRepository.java
│   ├── service/                # Business logic services
│   │   ├── AuthService.java    # Authentication, hashing, session management
│   │   ├── ProjectService.java # Project creation, team membership
│   │   ├── TaskService.java    # Task lifecycle, assignment, filtering
│   │   ├── AuditService.java   # Auditing system actions
│   │   └── ExportService.java  # Markdown & CSV reporting
│   ├── util/                   # Shared utilities
│   │   ├── AnsiColor.java      # Terminal styling & color badges
│   │   ├── DateTimeUtil.java   # Formatted timestamps
│   │   ├── PasswordHasher.java # SHA-256 secure password hashing
│   │   └── Validator.java      # Input validation & regex checks
│   └── cli/                    # CLI user interface
│       ├── ConsoleApp.java     # Interactive terminal menus
│       └── Main.java           # Dependency wiring & seed data
└── test/java/com/taskforge/    # JUnit 5 Unit Tests
    ├── AuthServiceTest.java
    └── TaskServiceTest.java
```

---

## 🚀 How to Build and Run

### Run Unit Tests
```bash
mvn test
```

### Build Executable JAR
```bash
mvn package
```

### Run Application
```bash
java -jar target/taskforge-app-1.0.0-SNAPSHOT.jar
```

Default seeded accounts:
- **Username:** `admin` | **Password:** `admin123`
- **Username:** `alex` | **Password:** `password`
- **Username:** `sara` | **Password:** `password`
