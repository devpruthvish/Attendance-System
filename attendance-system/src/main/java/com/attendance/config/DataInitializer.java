package com.attendance.config;

import com.attendance.entity.*;
import com.attendance.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private TeacherRepository teacherRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private SubjectRepository subjectRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            System.out.println("✅ Database already seeded — skipping.");
            return;
        }

        System.out.println("🌱 Seeding database...");

        // ADMIN
        User adminUser = makeUser("admin", "admin123", "ROLE_ADMIN");

        // TEACHER 1
        User t1User = makeUser("teacher1", "teacher123", "ROLE_TEACHER");
        Teacher teacher1 = new Teacher();
        teacher1.setName("Dr. Priya Nair");
        teacher1.setEmail("priya.nair@school.com");
        teacher1.setDepartment("Computer Science");
        teacher1.setUser(t1User);
        teacherRepository.save(teacher1);

        // TEACHER 2
        User t2User = makeUser("teacher2", "teacher123", "ROLE_TEACHER");
        Teacher teacher2 = new Teacher();
        teacher2.setName("Prof. Arjun Mehta");
        teacher2.setEmail("arjun.mehta@school.com");
        teacher2.setDepartment("Mathematics");
        teacher2.setUser(t2User);
        teacherRepository.save(teacher2);

        // SUBJECTS
        saveSubject("Data Structures",      "CS301", "CS-3A", teacher1);
        saveSubject("Operating Systems",    "CS302", "CS-3A", teacher1);
        saveSubject("Discrete Mathematics", "MA301", "CS-3A", teacher2);
        saveSubject("Database Management",  "CS303", "CS-3B", teacher1);
        saveSubject("Linear Algebra",       "MA302", "CS-3B", teacher2);

        // STUDENTS
        saveStudent("student1", "Rahul Sharma",    "CS-3A", "CS2024001", "rahul@student.com");
        saveStudent("student2", "Ananya Krishnan", "CS-3A", "CS2024002", "ananya@student.com");
        saveStudent("student3", "Vikram Patel",    "CS-3A", "CS2024003", "vikram@student.com");
        saveStudent("student4", "Sneha Iyer",      "CS-3B", "CS2024004", "sneha@student.com");
        saveStudent("student5", "Rohan Das",       "CS-3B", "CS2024005", "rohan@student.com");

        System.out.println("✅ Seeding complete!");
        System.out.println("   admin     / admin123");
        System.out.println("   teacher1  / teacher123");
        System.out.println("   student1  / student123");
    }

    // Creates user with enabled=true and encoded password
    private User makeUser(String username, String password, String role) {
        User u = new User();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(password));
        u.setRole(role);
        u.setEnabled(true); // ← CRITICAL
        return userRepository.save(u);
    }

    private void saveSubject(String name, String code, String className, Teacher teacher) {
        Subject s = new Subject();
        s.setName(name); s.setCode(code); s.setClassName(className); s.setTeacher(teacher);
        subjectRepository.save(s);
    }

    private void saveStudent(String username, String name, String className, String roll, String email) {
        User u = makeUser(username, "student123", "ROLE_STUDENT");
        Student s = new Student();
        s.setName(name); s.setClassName(className); s.setRollNumber(roll); s.setEmail(email); s.setUser(u);
        studentRepository.save(s);
    }
}
