package com.example.springboot.Controllers;

import com.example.springboot.Service.BookService;
import com.example.springboot.entities.book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/books")
public class BookController {
    @Autowired
    public BookService bookService;

    @GetMapping
    public List<book> getAllBook() {
        return bookService.getAllBook();
    }

    @GetMapping("/{id}")
    public book getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public book createBook(
            @ModelAttribute book book,
            @RequestParam(value = "imageFile", required = false) MultipartFile file
            ){
        return bookService.createBook(book, file);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public book updateBook(
            @ModelAttribute book book,
            @PathVariable Long id,
            @RequestParam(value = "imageFile", required = false) MultipartFile file
    ){
        return bookService.updateBook(book, id, file);
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id){
        bookService.deleteBook(id);
        return;
    }
}
