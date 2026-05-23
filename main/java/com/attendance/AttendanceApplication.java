package com.attendance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Student Attendance Management System.
 * @SpringBootApplication enables auto-configuration, component scanning, etc.
 */
@SpringBootApplication
public class AttendanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AttendanceApplication.class, args);
        System.out.println("✅ Attendance Management System started at http://localhost:8080");
    }
}
