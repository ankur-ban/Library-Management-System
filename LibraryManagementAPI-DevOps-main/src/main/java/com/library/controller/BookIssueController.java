
package com.library.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.library.entity.BookIssue;
import com.library.service.BookIssueService;

@RestController
@RequestMapping("/api/issues")
public class BookIssueController {

    private final BookIssueService issueService;

    public BookIssueController(BookIssueService issueService) {
        this.issueService = issueService;
    }

    // Issue a book
    @PostMapping("/{studentId}/{bookId}")
    public ResponseEntity<BookIssue> issueBook(
            @PathVariable Long studentId,
            @PathVariable Long bookId) {

        BookIssue issue = issueService.issueBook(studentId, bookId);

        return new ResponseEntity<>(issue, HttpStatus.CREATED);
    }

    // Get all issues
    @GetMapping
    public ResponseEntity<List<BookIssue>> getAllIssues() {
        return ResponseEntity.ok(issueService.getAllIssues());
    }

    // Get issues by student
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<BookIssue>> getIssuesByStudent(
            @PathVariable Long studentId) {

        return ResponseEntity.ok(
                issueService.getIssuesByStudent(studentId));
    }

    // Return a book
    @PutMapping("/return/{issueId}")
    public ResponseEntity<BookIssue> returnBook(
            @PathVariable Long issueId) {

        return ResponseEntity.ok(
                issueService.returnBook(issueId));
    }

    // Get overdue issues
    @GetMapping("/overdue")
    public ResponseEntity<List<BookIssue>> getOverdueIssues() {
        return ResponseEntity.ok(
                issueService.getOverdueIssues());
    }
}
