-- ============================================================
-- Student Attendance Management System - MySQL SQL Script
-- ALL table names and column names are LOWERCASE
-- Run this script ONCE after creating the database
-- ============================================================

-- Create and use the database
CREATE DATABASE IF NOT EXISTS attendance_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE attendance_db;

-- ============================================================
-- TABLE: users
-- Stores login credentials for all roles
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    role       VARCHAR(50)  NOT NULL,   -- 'ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_STUDENT'
    enabled    TINYINT(1)   NOT NULL DEFAULT 1
);

-- ============================================================
-- TABLE: teachers
-- ============================================================
CREATE TABLE IF NOT EXISTS teachers (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(150) NOT NULL,
    email      VARCHAR(150),
    department VARCHAR(100),
    user_id    BIGINT UNIQUE,
    CONSTRAINT fk_teacher_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- ============================================================
-- TABLE: students
-- ============================================================
CREATE TABLE IF NOT EXISTS students (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(150) NOT NULL,
    class_name  VARCHAR(50)  NOT NULL,
    roll_number VARCHAR(50),
    email       VARCHAR(150),
    user_id     BIGINT UNIQUE,
    CONSTRAINT fk_student_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- ============================================================
-- TABLE: subjects
-- ============================================================
CREATE TABLE IF NOT EXISTS subjects (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(150) NOT NULL,
    code        VARCHAR(50),
    class_name  VARCHAR(50),
    teacher_id  BIGINT,
    CONSTRAINT fk_subject_teacher FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE SET NULL
);

-- ============================================================
-- TABLE: attendance
-- One row per student per subject per date per hour
-- ============================================================
CREATE TABLE IF NOT EXISTS attendance (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id  BIGINT      NOT NULL,
    subject_id  BIGINT      NOT NULL,
    date        DATE        NOT NULL,
    hour        INT         NOT NULL,
    status      VARCHAR(20) NOT NULL,  -- 'present' or 'absent'
    remarks     VARCHAR(255),
    CONSTRAINT fk_att_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_att_subject FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
    CONSTRAINT uq_attendance UNIQUE (student_id, subject_id, date, hour)
);

-- ============================================================
-- SAMPLE DATA
-- Passwords are BCrypt-encoded. Plain text shown in comments.
-- Generate fresh hashes with: https://bcrypt-generator.com (rounds=10)
--
-- admin     -> admin123
-- teacher1  -> teacher123
-- teacher2  -> teacher123
-- student1  -> student123
-- student2  -> student123
-- student3  -> student123
-- ============================================================

-- NOTE: If Hibernate already created the tables via ddl-auto=update,
--       skip the CREATE TABLE statements above and only run the INSERTs.
--       Also use INSERT IGNORE to avoid duplicate key errors on re-run.

INSERT IGNORE INTO users (id, username, password, role, enabled) VALUES
(1,  'admin',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ROLE_ADMIN',   1),
(2,  'teacher1', '$2a$10$8XMo5MpFGBMq.u4ZkqSqhO1YhKGERtQJKfHcSnPNTk.7CXuPHxnpS', 'ROLE_TEACHER', 1),
(3,  'teacher2', '$2a$10$8XMo5MpFGBMq.u4ZkqSqhO1YhKGERtQJKfHcSnPNTk.7CXuPHxnpS', 'ROLE_TEACHER', 1),
(4,  'student1', '$2a$10$7EZflHXCIgNpCGk5GkM.Puc3mCMNTcJF.PKy95NiWvFG1E4x3KFKK', 'ROLE_STUDENT', 1),
(5,  'student2', '$2a$10$7EZflHXCIgNpCGk5GkM.Puc3mCMNTcJF.PKy95NiWvFG1E4x3KFKK', 'ROLE_STUDENT', 1),
(6,  'student3', '$2a$10$7EZflHXCIgNpCGk5GkM.Puc3mCMNTcJF.PKy95NiWvFG1E4x3KFKK', 'ROLE_STUDENT', 1),
(7,  'student4', '$2a$10$7EZflHXCIgNpCGk5GkM.Puc3mCMNTcJF.PKy95NiWvFG1E4x3KFKK', 'ROLE_STUDENT', 1),
(8,  'student5', '$2a$10$7EZflHXCIgNpCGk5GkM.Puc3mCMNTcJF.PKy95NiWvFG1E4x3KFKK', 'ROLE_STUDENT', 1);

INSERT IGNORE INTO teachers (id, name, email, department, user_id) VALUES
(1, 'Dr. Priya Nair',   'priya.nair@school.com',   'Computer Science', 2),
(2, 'Prof. Arjun Mehta','arjun.mehta@school.com',  'Mathematics',      3);

INSERT IGNORE INTO students (id, name, class_name, roll_number, email, user_id) VALUES
(1, 'Rahul Sharma',    'CS-3A', 'CS2024001', 'rahul@student.com',  4),
(2, 'Ananya Krishnan', 'CS-3A', 'CS2024002', 'ananya@student.com', 5),
(3, 'Vikram Patel',    'CS-3A', 'CS2024003', 'vikram@student.com', 6),
(4, 'Sneha Iyer',      'CS-3B', 'CS2024004', 'sneha@student.com',  7),
(5, 'Rohan Das',       'CS-3B', 'CS2024005', 'rohan@student.com',  8);

INSERT IGNORE INTO subjects (id, name, code, class_name, teacher_id) VALUES
(1, 'Data Structures',        'CS301', 'CS-3A', 1),
(2, 'Operating Systems',      'CS302', 'CS-3A', 1),
(3, 'Discrete Mathematics',   'MA301', 'CS-3A', 2),
(4, 'Database Management',    'CS303', 'CS-3B', 1),
(5, 'Linear Algebra',         'MA302', 'CS-3B', 2);

-- Sample attendance for CS-3A students (subject: Data Structures, id=1)
-- Date: 2024-01-15, Hour 1
INSERT IGNORE INTO attendance (student_id, subject_id, date, hour, status) VALUES
(1, 1, '2024-01-15', 1, 'present'),
(2, 1, '2024-01-15', 1, 'present'),
(3, 1, '2024-01-15', 1, 'absent');

-- Date: 2024-01-15, Hour 2
INSERT IGNORE INTO attendance (student_id, subject_id, date, hour, status) VALUES
(1, 1, '2024-01-15', 2, 'present'),
(2, 1, '2024-01-15', 2, 'absent'),
(3, 1, '2024-01-15', 2, 'present');

-- Date: 2024-01-16, Hour 1
INSERT IGNORE INTO attendance (student_id, subject_id, date, hour, status) VALUES
(1, 1, '2024-01-16', 1, 'present'),
(2, 1, '2024-01-16', 1, 'present'),
(3, 1, '2024-01-16', 1, 'present');

-- Date: 2024-01-17, Hour 1
INSERT IGNORE INTO attendance (student_id, subject_id, date, hour, status) VALUES
(1, 1, '2024-01-17', 1, 'absent'),
(2, 1, '2024-01-17', 1, 'present'),
(3, 1, '2024-01-17', 1, 'absent');

-- Operating Systems (subject_id=2) for CS-3A
INSERT IGNORE INTO attendance (student_id, subject_id, date, hour, status) VALUES
(1, 2, '2024-01-15', 3, 'present'),
(2, 2, '2024-01-15', 3, 'present'),
(3, 2, '2024-01-15', 3, 'present'),
(1, 2, '2024-01-16', 3, 'absent'),
(2, 2, '2024-01-16', 3, 'present'),
(3, 2, '2024-01-16', 3, 'absent'),
(1, 2, '2024-01-17', 3, 'present'),
(2, 2, '2024-01-17', 3, 'present'),
(3, 2, '2024-01-17', 3, 'present');

-- Discrete Mathematics (subject_id=3) for CS-3A
INSERT IGNORE INTO attendance (student_id, subject_id, date, hour, status) VALUES
(1, 3, '2024-01-15', 4, 'present'),
(2, 3, '2024-01-15', 4, 'absent'),
(3, 3, '2024-01-15', 4, 'present'),
(1, 3, '2024-01-16', 4, 'present'),
(2, 3, '2024-01-16', 4, 'present'),
(3, 3, '2024-01-16', 4, 'absent');

-- Database Management (subject_id=4) for CS-3B
INSERT IGNORE INTO attendance (student_id, subject_id, date, hour, status) VALUES
(4, 4, '2024-01-15', 1, 'present'),
(5, 4, '2024-01-15', 1, 'absent'),
(4, 4, '2024-01-16', 1, 'present'),
(5, 4, '2024-01-16', 1, 'present'),
(4, 4, '2024-01-17', 1, 'absent'),
(5, 4, '2024-01-17', 1, 'present');

-- ============================================================
-- VERIFY: Quick check queries after insert
-- ============================================================
-- SELECT COUNT(*) as total_users     FROM users;
-- SELECT COUNT(*) as total_teachers  FROM teachers;
-- SELECT COUNT(*) as total_students  FROM students;
-- SELECT COUNT(*) as total_subjects  FROM subjects;
-- SELECT COUNT(*) as total_records   FROM attendance;
