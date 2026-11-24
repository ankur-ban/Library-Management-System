
package com.library.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.library.entity.Student;
import com.library.repository.StudentRepository;
import com.library.exception.ResourceNotFoundException;
import com.library.exception.DuplicateResourceException;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    // Constructor injection
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // Register student
    public Student registerStudent(Student student) {

        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        return studentRepository.save(student);
    }

    // Get all students
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // Get student by ID
    public Student getStudentById(Long id) {

        return studentRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException("Student not found with ID: " + id));
    }

    // Update student
    public Student updateStudent(Long id, Student updatedStudent) {

        Student existingStudent = getStudentById(id);

        if (studentRepository.existsByEmail(updatedStudent.getEmail())
                && !existingStudent.getEmail().equals(updatedStudent.getEmail())) {

            throw new DuplicateResourceException("Email already exists");
        }

        existingStudent.setStudentName(updatedStudent.getStudentName());
        existingStudent.setEmail(updatedStudent.getEmail());
        existingStudent.setDepartment(updatedStudent.getDepartment());
        existingStudent.setPhoneNumber(updatedStudent.getPhoneNumber());

        return studentRepository.save(existingStudent);
    }

    // Delete student
    public void deleteStudent(Long id) {

        Student student = getStudentById(id);

        studentRepository.delete(student);
    }
}
