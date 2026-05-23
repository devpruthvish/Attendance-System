package com.attendance.controller;

import com.attendance.entity.*;
import com.attendance.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/teacher")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherController {

    @Autowired private TeacherService teacherService;
    @Autowired private SubjectService subjectService;
    @Autowired private StudentService studentService;
    @Autowired private AttendanceService attendanceService;
    @Autowired private UserService userService;

    private Teacher getLoggedInTeacher(Authentication auth) {
        return userService.getUserByUsername(auth.getName())
                .flatMap(user -> teacherService.getTeacherByUser(user))
                .orElse(null);
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        Teacher teacher = getLoggedInTeacher(auth);
        if (teacher == null) return "redirect:/login";
        List<Subject> subjects = subjectService.getSubjectsByTeacher(teacher);
        model.addAttribute("teacher", teacher);
        model.addAttribute("subjects", subjects);
        model.addAttribute("totalSubjects", subjects.size());
        return "teacher/dashboard";
    }

    @GetMapping("/attendance/mark")
    public String showMarkForm(Authentication auth, Model model) {
        Teacher teacher = getLoggedInTeacher(auth);
        if (teacher == null) return "redirect:/login";
        model.addAttribute("subjects", subjectService.getSubjectsByTeacher(teacher));
        model.addAttribute("classNames", studentService.getAllClassNames());
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("hours", List.of(1,2,3,4,5,6,7,8));
        return "teacher/mark-attendance-select";
    }

    @GetMapping("/attendance/session")
    public String showSession(@RequestParam Long subjectId,
                              @RequestParam String className,
                              @RequestParam String date,
                              @RequestParam Integer hour,
                              Authentication auth, Model model) {
        Teacher teacher = getLoggedInTeacher(auth);
        if (teacher == null) return "redirect:/login";

        Subject subject = subjectService.getSubjectById(subjectId).orElse(null);
        if (subject == null) return "redirect:/teacher/attendance/mark";

        LocalDate attDate = LocalDate.parse(date);
        List<Student> students = studentService.getStudentsByClass(className);

        Map<Long, String> existingStatus = new HashMap<>();
        for (Attendance a : attendanceService.getAttendanceBySubjectDateHour(subject, attDate, hour)) {
            existingStatus.put(a.getStudent().getId(), a.getStatus());
        }

        model.addAttribute("subject", subject);
        model.addAttribute("className", className);
        model.addAttribute("date", attDate);
        model.addAttribute("hour", hour);
        model.addAttribute("students", students);
        model.addAttribute("existingStatus", existingStatus);
        return "teacher/mark-attendance-session";
    }

    @PostMapping("/attendance/save")
    public String saveAttendance(@RequestParam Long subjectId,
                                 @RequestParam String date,
                                 @RequestParam Integer hour,
                                 @RequestParam Map<String, String> allParams,
                                 RedirectAttributes ra) {
        Subject subject = subjectService.getSubjectById(subjectId).orElse(null);
        if (subject == null) { ra.addFlashAttribute("error", "Subject not found!"); return "redirect:/teacher/attendance/mark"; }

        LocalDate attDate = LocalDate.parse(date);
        Map<Long, String> statusMap = new HashMap<>();
        for (Map.Entry<String, String> e : allParams.entrySet()) {
            if (e.getKey().startsWith("status_")) {
                statusMap.put(Long.parseLong(e.getKey().replace("status_", "")), e.getValue());
            }
        }
        attendanceService.markAttendance(subject, attDate, hour, statusMap, studentService);
        ra.addFlashAttribute("success", "Attendance saved for " + attDate + " Hour " + hour);
        return "redirect:/teacher/dashboard";
    }

    @GetMapping("/attendance/report")
    public String viewReport(Authentication auth,
                             @RequestParam(required = false) Long subjectId,
                             @RequestParam(required = false) Long studentId,
                             Model model) {
        Teacher teacher = getLoggedInTeacher(auth);
        if (teacher == null) return "redirect:/login";
        model.addAttribute("subjects", subjectService.getSubjectsByTeacher(teacher));
        model.addAttribute("students", studentService.getAllStudents());
        if (subjectId != null && studentId != null) {
            subjectService.getSubjectById(subjectId).ifPresent(sub -> {
                studentService.getStudentById(studentId).ifPresent(st -> {
                    model.addAttribute("records", attendanceService.getAttendanceByStudentAndSubject(st, sub));
                    model.addAttribute("selectedSubject", sub);
                    model.addAttribute("selectedStudent", st);
                    model.addAttribute("percentage", attendanceService.calculateAttendancePercentage(st, sub));
                });
            });
        }
        return "teacher/attendance-report";
    }
}
