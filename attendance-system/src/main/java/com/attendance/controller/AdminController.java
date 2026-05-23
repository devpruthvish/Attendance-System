package com.attendance.controller;

import com.attendance.entity.*;
import com.attendance.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired private StudentService studentService;
    @Autowired private TeacherService teacherService;
    @Autowired private SubjectService subjectService;
    @Autowired private AttendanceService attendanceService;
    @Autowired private UserService userService;

    // ── DASHBOARD ────────────────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalStudents",  studentService.getTotalCount());
        model.addAttribute("totalTeachers",  teacherService.getTotalCount());
        model.addAttribute("totalSubjects",  subjectService.getTotalCount());
        model.addAttribute("totalPresent",   attendanceService.getTotalPresent());
        model.addAttribute("totalAbsent",    attendanceService.getTotalAbsent());
        model.addAttribute("totalAttendance",attendanceService.getTotalRecords());
        return "admin/dashboard";
    }

    // ── STUDENTS ─────────────────────────────────────────────
    @GetMapping("/students")
    public String listStudents(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        return "admin/students";
    }

    @GetMapping("/students/add")
    public String showAddStudentForm(Model model) {
        model.addAttribute("editMode", false);
        model.addAttribute("student", new Student());
        return "admin/student-form";
    }

    @PostMapping("/students/save")
    public String saveStudent(@RequestParam String name,
                              @RequestParam String className,
                              @RequestParam(required = false) String rollNumber,
                              @RequestParam(required = false) String email,
                              @RequestParam String username,
                              @RequestParam String password,
                              RedirectAttributes ra) {
        try {
            if (userService.usernameExists(username)) {
                ra.addFlashAttribute("error", "Username '" + username + "' already exists!");
                return "redirect:/admin/students/add";
            }
            User user = new User();
            user.setUsername(username);
            user.setPassword(password); // encoded inside saveUser()
            user.setRole("ROLE_STUDENT");
            User saved = userService.saveUser(user);

            Student s = new Student();
            s.setName(name);
            s.setClassName(className);
            s.setRollNumber(rollNumber);
            s.setEmail(email);
            s.setUser(saved);
            studentService.saveStudent(s);
            ra.addFlashAttribute("success", "Student added! Login: " + username + " / " + password);
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/admin/students";
    }

    @GetMapping("/students/edit/{id}")
    public String showEditStudentForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Optional<Student> st = studentService.getStudentById(id);
        if (st.isEmpty()) { ra.addFlashAttribute("error", "Student not found!"); return "redirect:/admin/students"; }
        model.addAttribute("student", st.get());
        model.addAttribute("editMode", true);
        return "admin/student-form";
    }

    @PostMapping("/students/update/{id}")
    public String updateStudent(@PathVariable Long id,
                                @RequestParam String name,
                                @RequestParam String className,
                                @RequestParam(required = false) String rollNumber,
                                @RequestParam(required = false) String email,
                                RedirectAttributes ra) {
        Student s = studentService.getStudentById(id).orElse(null);
        if (s == null) { ra.addFlashAttribute("error", "Not found!"); return "redirect:/admin/students"; }
        s.setName(name); s.setClassName(className); s.setRollNumber(rollNumber); s.setEmail(email);
        studentService.saveStudent(s);
        ra.addFlashAttribute("success", "Student updated!");
        return "redirect:/admin/students";
    }

    @GetMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes ra) {
        studentService.deleteStudent(id);
        ra.addFlashAttribute("success", "Student deleted!");
        return "redirect:/admin/students";
    }

    // ── TEACHERS ─────────────────────────────────────────────
    @GetMapping("/teachers")
    public String listTeachers(Model model) {
        model.addAttribute("teachers", teacherService.getAllTeachers());
        return "admin/teachers";
    }

    @GetMapping("/teachers/add")
    public String showAddTeacherForm(Model model) {
        model.addAttribute("editMode", false);
        model.addAttribute("teacher", new Teacher());
        return "admin/teacher-form";
    }

    @PostMapping("/teachers/save")
    public String saveTeacher(@RequestParam String name,
                              @RequestParam(required = false) String department,
                              @RequestParam(required = false) String email,
                              @RequestParam String username,
                              @RequestParam String password,
                              RedirectAttributes ra) {
        try {
            if (userService.usernameExists(username)) {
                ra.addFlashAttribute("error", "Username '" + username + "' already exists!");
                return "redirect:/admin/teachers/add";
            }
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setRole("ROLE_TEACHER");
            User saved = userService.saveUser(user);

            Teacher t = new Teacher();
            t.setName(name); t.setDepartment(department); t.setEmail(email); t.setUser(saved);
            teacherService.saveTeacher(t);
            ra.addFlashAttribute("success", "Teacher added! Login: " + username + " / " + password);
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/admin/teachers";
    }

    @GetMapping("/teachers/edit/{id}")
    public String showEditTeacherForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Optional<Teacher> t = teacherService.getTeacherById(id);
        if (t.isEmpty()) { ra.addFlashAttribute("error", "Not found!"); return "redirect:/admin/teachers"; }
        model.addAttribute("teacher", t.get());
        model.addAttribute("editMode", true);
        return "admin/teacher-form";
    }

    @PostMapping("/teachers/update/{id}")
    public String updateTeacher(@PathVariable Long id,
                                @RequestParam String name,
                                @RequestParam(required = false) String email,
                                @RequestParam(required = false) String department,
                                RedirectAttributes ra) {
        Teacher t = teacherService.getTeacherById(id).orElse(null);
        if (t == null) { ra.addFlashAttribute("error", "Not found!"); return "redirect:/admin/teachers"; }
        t.setName(name); t.setEmail(email); t.setDepartment(department);
        teacherService.saveTeacher(t);
        ra.addFlashAttribute("success", "Teacher updated!");
        return "redirect:/admin/teachers";
    }

    @GetMapping("/teachers/delete/{id}")
    public String deleteTeacher(@PathVariable Long id, RedirectAttributes ra) {
        teacherService.deleteTeacher(id);
        ra.addFlashAttribute("success", "Teacher deleted!");
        return "redirect:/admin/teachers";
    }

    // ── SUBJECTS ─────────────────────────────────────────────
    @GetMapping("/subjects")
    public String listSubjects(Model model) {
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "admin/subjects";
    }

    @GetMapping("/subjects/add")
    public String showAddSubjectForm(Model model) {
        model.addAttribute("subject", new Subject());
        model.addAttribute("teachers", teacherService.getAllTeachers());
        model.addAttribute("editMode", false);
        return "admin/subject-form";
    }

    @PostMapping("/subjects/save")
    public String saveSubject(@RequestParam String name,
                              @RequestParam(required = false) String code,
                              @RequestParam(required = false) String className,
                              @RequestParam(required = false) Long teacherId,
                              RedirectAttributes ra) {
        try {
            Subject sub = new Subject();
            sub.setName(name); sub.setCode(code); sub.setClassName(className);
            if (teacherId != null) teacherService.getTeacherById(teacherId).ifPresent(sub::setTeacher);
            subjectService.saveSubject(sub);
            ra.addFlashAttribute("success", "Subject saved!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/admin/subjects";
    }

    @GetMapping("/subjects/edit/{id}")
    public String showEditSubjectForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Optional<Subject> sub = subjectService.getSubjectById(id);
        if (sub.isEmpty()) { ra.addFlashAttribute("error", "Not found!"); return "redirect:/admin/subjects"; }
        model.addAttribute("subject", sub.get());
        model.addAttribute("teachers", teacherService.getAllTeachers());
        model.addAttribute("editMode", true);
        return "admin/subject-form";
    }

    @PostMapping("/subjects/update/{id}")
    public String updateSubject(@PathVariable Long id,
                                @RequestParam String name,
                                @RequestParam(required = false) String code,
                                @RequestParam(required = false) String className,
                                @RequestParam(required = false) Long teacherId,
                                RedirectAttributes ra) {
        Subject sub = subjectService.getSubjectById(id).orElse(null);
        if (sub == null) { ra.addFlashAttribute("error", "Not found!"); return "redirect:/admin/subjects"; }
        sub.setName(name); sub.setCode(code); sub.setClassName(className);
        if (teacherId != null) teacherService.getTeacherById(teacherId).ifPresent(sub::setTeacher);
        subjectService.saveSubject(sub);
        ra.addFlashAttribute("success", "Subject updated!");
        return "redirect:/admin/subjects";
    }

    @GetMapping("/subjects/delete/{id}")
    public String deleteSubject(@PathVariable Long id, RedirectAttributes ra) {
        subjectService.deleteSubject(id);
        ra.addFlashAttribute("success", "Subject deleted!");
        return "redirect:/admin/subjects";
    }

    // ── ATTENDANCE ────────────────────────────────────────────
    @GetMapping("/attendance")
    public String listAttendance(Model model) {
        model.addAttribute("attendanceList", attendanceService.getAllAttendance());
        return "admin/attendance";
    }

    @GetMapping("/attendance/delete/{id}")
    public String deleteAttendance(@PathVariable Long id, RedirectAttributes ra) {
        attendanceService.deleteAttendance(id);
        ra.addFlashAttribute("success", "Record deleted!");
        return "redirect:/admin/attendance";
    }

    @GetMapping("/attendance/report")
    public String attendanceReport(@RequestParam(required = false) Long studentId,
                                   @RequestParam(required = false) Long subjectId,
                                   Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("subjects", subjectService.getAllSubjects());
        if (studentId != null && subjectId != null) {
            studentService.getStudentById(studentId).ifPresent(st -> {
                subjectService.getSubjectById(subjectId).ifPresent(sub -> {
                    model.addAttribute("records", attendanceService.getAttendanceByStudentAndSubject(st, sub));
                    model.addAttribute("selectedStudent", st);
                    model.addAttribute("selectedSubject", sub);
                    model.addAttribute("percentage", attendanceService.calculateAttendancePercentage(st, sub));
                });
            });
        }
        return "admin/attendance-report";
    }
}
