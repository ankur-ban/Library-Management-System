
package com.library.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.library.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

    // Find books by category
    List<Book> findByCategory(String category);

    // Check whether ISBN already exists
    boolean existsByIsbn(String isbn);
}
