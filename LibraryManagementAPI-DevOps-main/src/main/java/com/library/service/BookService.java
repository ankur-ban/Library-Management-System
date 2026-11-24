
package com.library.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.library.entity.Book;
import com.library.repository.BookRepository;
import com.library.exception.ResourceNotFoundException;
import com.library.exception.DuplicateResourceException;
import com.library.exception.BadRequestException;

@Service
public class BookService {

    private final BookRepository bookRepository;

    // Constructor injection
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // Add new book
    public Book addBook(Book book) {

        if (bookRepository.existsByIsbn(book.getIsbn())) {
        	throw new DuplicateResourceException("ISBN already exists");
        }

        if (book.getTotalCopies() == null || book.getTotalCopies() < 0) {
            throw new BadRequestException("Total copies cannot be negative");
        }

        book.setAvailableCopies(book.getTotalCopies());

        return bookRepository.save(book);
    }

    // Get all books
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // Get book by ID
    public Book getBookById(Long id) {

        return bookRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException("Book not found with ID: " + id));
    }

    // Update book
    public Book updateBook(Long id, Book updatedBook) {

        Book existingBook = getBookById(id);

        int issuedCopies = existingBook.getTotalCopies()
                - existingBook.getAvailableCopies();

        if (updatedBook.getTotalCopies() == null
                || updatedBook.getTotalCopies() < issuedCopies) {
            throw new BadRequestException(
                    "Total copies cannot be less than currently issued copies");
        }

        if (bookRepository.existsByIsbn(updatedBook.getIsbn())
                && !existingBook.getIsbn().equals(updatedBook.getIsbn())) {
            throw new DuplicateResourceException("ISBN already exists");
        }

        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setAuthor(updatedBook.getAuthor());
        existingBook.setIsbn(updatedBook.getIsbn());
        existingBook.setCategory(updatedBook.getCategory());
        existingBook.setTotalCopies(updatedBook.getTotalCopies());

        existingBook.setAvailableCopies(
                updatedBook.getTotalCopies() - issuedCopies);

        return bookRepository.save(existingBook);
    }

    // Delete book
    public void deleteBook(Long id) {

        Book book = getBookById(id);

        bookRepository.delete(book);
    }

    // Search books by category
    public List<Book> getBooksByCategory(String category) {
        return bookRepository.findByCategory(category);
    }
}
