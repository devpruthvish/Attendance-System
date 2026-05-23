package com.attendance.repository;

import com.attendance.entity.Subject;
import com.attendance.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    List<Subject> findByTeacher(Teacher teacher);

    List<Subject> findByClassName(String className);
}
