package com.attendance.service;

import com.attendance.entity.Subject;
import com.attendance.entity.Teacher;
import com.attendance.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public Optional<Subject> getSubjectById(Long id) {
        return subjectRepository.findById(id);
    }

    public List<Subject> getSubjectsByTeacher(Teacher teacher) {
        return subjectRepository.findByTeacher(teacher);
    }

    public List<Subject> getSubjectsByClass(String className) {
        return subjectRepository.findByClassName(className);
    }

    // Filter by teacher AND class — done in Java to avoid missing repo method
    public List<Subject> getSubjectsByTeacherAndClass(Teacher teacher, String className) {
        return subjectRepository.findByTeacher(teacher).stream()
                .filter(s -> className.equals(s.getClassName()))
                .toList();
    }

    public Subject saveSubject(Subject subject) {
        return subjectRepository.save(subject);
    }

    public void deleteSubject(Long id) {
        subjectRepository.deleteById(id);
    }

    public long getTotalCount() {
        return subjectRepository.count();
    }
}
