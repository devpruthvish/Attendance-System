package com.attendance.repository;

import com.attendance.entity.Attendance;
import com.attendance.entity.Student;
import com.attendance.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByStudent(Student student);

    List<Attendance> findByStudentAndSubject(Student student, Subject subject);

    List<Attendance> findBySubjectAndDateAndHour(Subject subject, LocalDate date, Integer hour);

    Optional<Attendance> findByStudentAndSubjectAndDateAndHour(
            Student student, Subject subject, LocalDate date, Integer hour);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student = :student AND a.subject = :subject AND a.status = 'present'")
    long countPresentByStudentAndSubject(@Param("student") Student student, @Param("subject") Subject subject);

    long countByStudentAndSubject(Student student, Subject subject);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.status = 'present'")
    long countAllPresent();

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.status = 'absent'")
    long countAllAbsent();
}
