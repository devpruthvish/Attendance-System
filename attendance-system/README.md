# 🎓 EduTrack — Student Attendance Management System

A complete Spring Boot web application for managing student attendance with role-based access for Admins, Teachers, and Students.

---

## 📋 Tech Stack

| Layer       | Technology                    |
|-------------|-------------------------------|
| Backend     | Java 17 + Spring Boot 3.2     |
| Security    | Spring Security + BCrypt      |
| ORM         | Spring Data JPA (Hibernate)   |
| Database    | MySQL 8.x                     |
| Frontend    | Thymeleaf + Custom CSS        |
| Build Tool  | Maven                         |

---

## 🗂️ Project Structure

```
attendance-system/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/attendance/
    │   │   ├── AttendanceApplication.java       ← Main entry point
    │   │   ├── config/
    │   │   │   ├── SecurityConfig.java          ← Spring Security setup
    │   │   │   ├── CustomAuthSuccessHandler.java← Role-based redirect after login
    │   │   │   └── DataInitializer.java         ← Seeds DB with sample data
    │   │   ├── controller/
    │   │   │   ├── HomeController.java          ← Login/root
    │   │   │   ├── AdminController.java         ← Admin CRUD
    │   │   │   ├── TeacherController.java       ← Teacher features
    │   │   │   └── StudentController.java       ← Student portal
    │   │   ├── entity/
    │   │   │   ├── User.java
    │   │   │   ├── Student.java
    │   │   │   ├── Teacher.java
    │   │   │   ├── Subject.java
    │   │   │   └── Attendance.java
    │   │   ├── repository/
    │   │   │   ├── UserRepository.java
    │   │   │   ├── StudentRepository.java
    │   │   │   ├── TeacherRepository.java
    │   │   │   ├── SubjectRepository.java
    │   │   │   └── AttendanceRepository.java
    │   │   └── service/
    │   │       ├── CustomUserDetailsService.java
    │   │       ├── UserService.java
    │   │       ├── StudentService.java
    │   │       ├── TeacherService.java
    │   │       ├── SubjectService.java
    │   │       └── AttendanceService.java
    │   └── resources/
    │       ├── application.properties
    │       ├── db-init.sql                      ← Manual SQL script (optional)
    │       ├── static/css/style.css
    │       └── templates/
    │           ├── auth/login.html
    │           ├── fragments/sidebar.html
    │           ├── admin/
    │           │   ├── dashboard.html
    │           │   ├── students.html / student-form.html
    │           │   ├── teachers.html / teacher-form.html
    │           │   ├── subjects.html / subject-form.html
    │           │   └── attendance.html / attendance-report.html
    │           ├── teacher/
    │           │   ├── dashboard.html
    │           │   ├── mark-attendance-select.html
    │           │   ├── mark-attendance-session.html
    │           │   └── attendance-report.html
    │           └── student/
    │               ├── dashboard.html
    │               └── attendance-detail.html
```

---

## 🍎 macOS Setup — Step by Step

### Step 1: Install Homebrew (if not already installed)

```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

### Step 2: Install Java 17

```bash
brew install openjdk@17

# Add to PATH (add this line to ~/.zshrc or ~/.bash_profile)
export JAVA_HOME=$(brew --prefix openjdk@17)
export PATH="$JAVA_HOME/bin:$PATH"

# Reload shell
source ~/.zshrc

# Verify
java -version
# Should show: openjdk 17.x.x
```

### Step 3: Install Maven

```bash
brew install maven

# Verify
mvn -version
# Should show: Apache Maven 3.x.x
```

### Step 4: Install MySQL

```bash
brew install mysql

# Start MySQL service
brew services start mysql

# Secure the installation (set root password)
mysql_secure_installation
# Follow prompts — set a root password, answer Y to security questions
```

### Step 5: Create the Database

```bash
# Connect to MySQL
mysql -u root -p
# Enter your root password when prompted
```

Inside the MySQL shell:

```sql
CREATE DATABASE attendance_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Verify it was created
SHOW DATABASES;

-- Exit
EXIT;
```

### Step 6: Configure application.properties

Open `src/main/resources/application.properties` and update:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/attendance_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD_HERE   # ← Change this!
```

---

## ▶️ Running the Application

### Option A: Maven (recommended for development)

```bash
# Navigate to project root
cd attendance-system

# Run the app
mvn spring-boot:run
```

### Option B: Build JAR and Run

```bash
# Package the project
mvn clean package -DskipTests

# Run the JAR
java -jar target/attendance-system-1.0.0.jar
```

### Verify it's running

Open your browser and go to: **http://localhost:8080**

You should see the login page.

---

## 🔑 Default Login Accounts

These are auto-created by `DataInitializer.java` on first startup:

| Role    | Username | Password    |
|---------|----------|-------------|
| Admin   | admin    | admin123    |
| Teacher | teacher1 | teacher123  |
| Teacher | teacher2 | teacher123  |
| Student | student1 | student123  |
| Student | student2 | student123  |
| Student | student3 | student123  |
| Student | student4 | student123  |
| Student | student5 | student123  |

---

## 🎯 Feature Walkthrough

### Admin (login: admin / admin123)
1. Go to **Dashboard** → see total stats
2. Go to **Students** → Add/Edit/Delete students (creates login account too)
3. Go to **Teachers** → Add/Edit/Delete teachers
4. Go to **Subjects** → Add subjects, assign to teachers and classes
5. Go to **All Attendance** → View/Delete all attendance records
6. Go to **Reports** → Filter by student + subject to see percentage

### Teacher (login: teacher1 / teacher123)
1. Dashboard shows assigned subjects
2. Click **Mark Attendance** → Select subject, class, date, hour → Submit
3. Step 2 shows all students with Present/Absent radio buttons
4. Use **Mark All Present** button for bulk marking
5. Go to **View Reports** → filter by subject and student

### Student (login: student1 / student123)
1. Dashboard shows subject-wise attendance with percentage bars
2. Color coded: 🟢 ≥75% | 🟡 50-74% | 🔴 <50%
3. Click **View** on any subject to see day-by-day records

---

## 🗄️ Database Schema

```
users        → id, username, password, role, enabled
teachers     → id, name, email, department, user_id (FK→users)
students     → id, name, class_name, roll_number, email, user_id (FK→users)
subjects     → id, name, code, class_name, teacher_id (FK→teachers)
attendance   → id, student_id (FK), subject_id (FK), date, hour, status, remarks
              UNIQUE(student_id, subject_id, date, hour)
```

---

## 🧪 Optional: Run the SQL Script Manually

If you want to seed with extra sample data or reset:

```bash
mysql -u root -p attendance_db < src/main/resources/db-init.sql
```

---

## 🔧 Troubleshooting

| Problem | Solution |
|---------|----------|
| `Access denied for user 'root'` | Wrong password in `application.properties` |
| `Unknown database 'attendance_db'` | Run `CREATE DATABASE attendance_db;` in MySQL |
| `Port 8080 already in use` | Change `server.port=8081` in `application.properties` |
| `java: command not found` | Re-run `source ~/.zshrc` or open a new terminal |
| Tables not created | Check `spring.jpa.hibernate.ddl-auto=update` in properties |
| Can't log in with demo accounts | Wait for `DataInitializer` log: "✅ Sample data seeded" |

---

## 📄 Key Design Decisions

- **All SQL names are lowercase** (table names, column names) — required for case-sensitive MySQL
- **BCrypt** password hashing — passwords never stored in plain text
- **JPA Repositories** — no manual SQL, auto-generated queries from method names
- **Thymeleaf fragments** — sidebar is a reusable component included in all pages
- **DataInitializer** — safely seeds DB only once (checks `userRepository.count() > 0`)
- **Role-based redirect** — after login, each role goes to their own dashboard
