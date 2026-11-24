
package com.library.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.library.entity.Book;
import com.library.entity.BookIssue;
import com.library.entity.IssueStatus;
import com.library.entity.Student;
import com.library.repository.BookIssueRepository;
import com.library.repository.BookRepository;
import com.library.repository.StudentRepository;
import com.library.exception.ResourceNotFoundException;
import com.library.exception.BadRequestException;
@Service
public class BookIssueService {

    private final BookIssueRepository issueRepository;
    private final BookRepository bookRepository;
    private final StudentRepository studentRepository;

    public BookIssueService(
            BookIssueRepository issueRepository,
            BookRepository bookRepository,
            StudentRepository studentRepository) {

        this.issueRepository = issueRepository;
        this.bookRepository = bookRepository;
        this.studentRepository = studentRepository;
    }

    // Issue a book
    @Transactional
    public BookIssue issueBook(Long studentId, Long bookId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Book not found"));

        if (book.getAvailableCopies() <= 0) {
            throw new BadRequestException("No copies available");
        }

        long activeIssues =
                issueRepository.countByStudent_StudentIdAndStatus(
                        studentId, IssueStatus.ACTIVE);

        if (activeIssues >= 3) {
            throw new BadRequestException(
                    "Student cannot issue more than 3 active books");
        }

        BookIssue issue = new BookIssue();

        issue.setStudent(student);
        issue.setBook(book);
        issue.setIssueDate(LocalDate.now());
        issue.setDueDate(LocalDate.now().plusDays(14));
        issue.setStatus(IssueStatus.ACTIVE);

        book.setAvailableCopies(book.getAvailableCopies() - 1);

        bookRepository.save(book);

        return issueRepository.save(issue);
    }

    // Get all issues
    public List<BookIssue> getAllIssues() {
        return issueRepository.findAll();
    }

    // Get issues by student
    public List<BookIssue> getIssuesByStudent(Long studentId) {

        if (!studentRepository.existsById(studentId)) {
            throw new RuntimeException("Student not found");
        }

        return issueRepository.findByStudent_StudentId(studentId);
    }

    // Return a book
    @Transactional
    public BookIssue returnBook(Long issueId) {

        BookIssue issue = issueRepository.findById(issueId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Issue not found"));

        if (issue.getStatus() == IssueStatus.RETURNED) {
            throw new BadRequestException("Book already returned");
        }

        issue.setReturnDate(LocalDate.now());
        issue.setStatus(IssueStatus.RETURNED);

        Book book = issue.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);

        bookRepository.save(book);

        return issueRepository.save(issue);
    }

    // Get overdue issues
    public List<BookIssue> getOverdueIssues() {

        return issueRepository.findByStatusAndDueDateBefore(
                IssueStatus.ACTIVE, LocalDate.now());
    }
}
