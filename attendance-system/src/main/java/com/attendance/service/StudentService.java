package com.attendance.service;

import com.attendance.entity.Student;
import com.attendance.entity.User;
import com.attendance.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public Optional<Student> getStudentByUser(User user) {
        return studentRepository.findByUserId(user.getId());
    }

    public List<Student> getStudentsByClass(String className) {
        return studentRepository.findByClassName(className);
    }

    public List<String> getAllClassNames() {
        return studentRepository.findDistinctClassNames();
    }

    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    public long getTotalCount() {
        return studentRepository.count();
    }
}
