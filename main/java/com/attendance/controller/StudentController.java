package com.attendance.controller;

import com.attendance.entity.*;
import com.attendance.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Controller
@RequestMapping("/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    @Autowired private StudentService studentService;
    @Autowired private SubjectService subjectService;
    @Autowired private AttendanceService attendanceService;
    @Autowired private UserService userService;

    private Student getLoggedInStudent(Authentication auth) {
        return userService.getUserByUsername(auth.getName())
                .flatMap(user -> studentService.getStudentByUser(user))
                .orElse(null);
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        Student student = getLoggedInStudent(auth);

        if (student == null) {
            model.addAttribute("student", null);
            model.addAttribute("attendanceSummary", Collections.emptyList());
            model.addAttribute("overallPercentage", 0.0);
            model.addAttribute("overallStatus", "danger");
            model.addAttribute("profileError",
                    "Your student profile is not linked. Please contact admin.");
            return "student/dashboard";
        }

        List<Subject> subjects = subjectService.getSubjectsByClass(student.getClassName());
        List<Map<String, Object>> summary = attendanceService.getSubjectWiseAttendance(student, subjects);
        double overall = attendanceService.calculateOverallAttendancePercentage(student);

        model.addAttribute("student", student);
        model.addAttribute("attendanceSummary", summary);
        model.addAttribute("overallPercentage", overall);
        model.addAttribute("overallStatus", overall >= 75 ? "good" : (overall >= 50 ? "warning" : "danger"));
        return "student/dashboard";
    }

    @GetMapping("/attendance/{subjectId}")
    public String subjectAttendance(@PathVariable Long subjectId, Authentication auth, Model model) {
        Student student = getLoggedInStudent(auth);
        if (student == null) return "redirect:/student/dashboard";

        Subject subject = subjectService.getSubjectById(subjectId).orElse(null);
        if (subject == null) return "redirect:/student/dashboard";

        List<Attendance> records = attendanceService.getAttendanceByStudentAndSubject(student, subject);
        double pct = attendanceService.calculateAttendancePercentage(student, subject);
        long present = records.stream().filter(a -> "present".equalsIgnoreCase(a.getStatus())).count();

        model.addAttribute("student", student);
        model.addAttribute("subject", subject);
        model.addAttribute("records", records);
        model.addAttribute("percentage", pct);
        model.addAttribute("totalClasses", records.size());
        model.addAttribute("presentCount", present);
        model.addAttribute("absentCount", records.size() - present);
        return "student/attendance-detail";
    }
}
