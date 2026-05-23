package com.attendance.service;

import com.attendance.entity.Attendance;
import com.attendance.entity.Student;
import com.attendance.entity.Subject;
import com.attendance.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    public Optional<Attendance> getAttendanceById(Long id) {
        return attendanceRepository.findById(id);
    }

    public List<Attendance> getAttendanceByStudent(Student student) {
        return attendanceRepository.findByStudent(student);
    }

    public List<Attendance> getAttendanceByStudentAndSubject(Student student, Subject subject) {
        return attendanceRepository.findByStudentAndSubject(student, subject);
    }

    public List<Attendance> getAttendanceBySubjectDateHour(Subject subject, LocalDate date, Integer hour) {
        return attendanceRepository.findBySubjectAndDateAndHour(subject, date, hour);
    }

    public void markAttendance(Subject subject, LocalDate date, Integer hour,
                               Map<Long, String> studentStatusMap, StudentService studentService) {
        for (Map.Entry<Long, String> entry : studentStatusMap.entrySet()) {
            Long studentId = entry.getKey();
            String status = entry.getValue();
            Student student = studentService.getStudentById(studentId).orElse(null);
            if (student == null) continue;

            Optional<Attendance> existing = attendanceRepository
                    .findByStudentAndSubjectAndDateAndHour(student, subject, date, hour);

            Attendance attendance = existing.orElse(new Attendance());
            attendance.setStudent(student);
            attendance.setSubject(subject);
            attendance.setDate(date);
            attendance.setHour(hour);
            attendance.setStatus(status);
            attendanceRepository.save(attendance);
        }
    }

    public Attendance saveAttendance(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    public void deleteAttendance(Long id) {
        attendanceRepository.deleteById(id);
    }

    public double calculateAttendancePercentage(Student student, Subject subject) {
        long total = attendanceRepository.countByStudentAndSubject(student, subject);
        if (total == 0) return 0.0;
        long present = attendanceRepository.countPresentByStudentAndSubject(student, subject);
        return Math.round((present * 100.0 / total) * 10.0) / 10.0;
    }

    public double calculateOverallAttendancePercentage(Student student) {
        List<Attendance> all = attendanceRepository.findByStudent(student);
        if (all.isEmpty()) return 0.0;
        long present = all.stream().filter(a -> "present".equalsIgnoreCase(a.getStatus())).count();
        return Math.round((present * 100.0 / all.size()) * 10.0) / 10.0;
    }

    public List<Map<String, Object>> getSubjectWiseAttendance(Student student, List<Subject> subjects) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Subject subject : subjects) {
            long total = attendanceRepository.countByStudentAndSubject(student, subject);
            long present = attendanceRepository.countPresentByStudentAndSubject(student, subject);
            long absent = total - present;
            double pct = total > 0 ? Math.round((present * 100.0 / total) * 10.0) / 10.0 : 0.0;

            Map<String, Object> map = new LinkedHashMap<>();
            map.put("subjectId", subject.getId());
            map.put("subjectName", subject.getName());
            map.put("subjectCode", subject.getCode());
            map.put("totalClasses", total);
            map.put("present", present);
            map.put("absent", absent);
            map.put("percentage", pct);
            map.put("status", pct >= 75 ? "good" : (pct >= 50 ? "warning" : "danger"));
            list.add(map);
        }
        return list;
    }

    public long getTotalPresent() { return attendanceRepository.countAllPresent(); }
    public long getTotalAbsent()  { return attendanceRepository.countAllAbsent(); }
    public long getTotalRecords() { return attendanceRepository.count(); }
}
