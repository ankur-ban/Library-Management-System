
package com.library.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.library.entity.BookIssue;
import com.library.entity.IssueStatus;

public interface BookIssueRepository extends JpaRepository<BookIssue, Long> {

    long countByStudent_StudentIdAndStatus(
            Long studentId, IssueStatus status);

    List<BookIssue> findByStudent_StudentId(Long studentId);

    List<BookIssue> findByStatusAndDueDateBefore(
            IssueStatus status, LocalDate date);
}
