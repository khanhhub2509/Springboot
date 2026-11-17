package com.example.springboot.Service;

import com.example.springboot.Repositories.AuthorRepository;
import com.example.springboot.Repositories.BookRepository;
import com.example.springboot.Repositories.CategoryRepository;
import com.example.springboot.Repositories.PublisherRepository;
import com.example.springboot.entities.book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class BookService {
    @Autowired
    public BookRepository bookRepository;

    @Autowired
    public CategoryRepository categoryRepository;

    @Autowired
    public AuthorRepository authorRepository;

    @Autowired
    public PublisherRepository publisherRepository;

    private final Path uploadPath = Paths.get("uploads");

    public BookService(){
        try {
            if (!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }
        } catch (Exception e) {
            throw new RuntimeException("Khong tao duoc thu muc upload");
        }
    }

    public List<book> getAllBook(){
        return bookRepository.findAll();
    }

    public book getBookById(Long id){
        return bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Khong tim thay"));
    }

    public book createBook(book book, MultipartFile file){
        Long categoryId = book.getCategory().getId();
        Long authorId = book.getAuthor().getId();
        Long publisherId = book.getPublisher().getId();
        if(!categoryRepository.existsById(categoryId)){
            throw new RuntimeException("Khong ton tai category");
        }
        if(!authorRepository.existsById(authorId)){
            throw new RuntimeException("Khong ton tai tac gia");
        }
        if(!publisherRepository.existsById(publisherId)){
            throw new RuntimeException("Khong ton tai nha xuat ban");
        }

        //Xử lý ảnh
        try {
            if (file != null & !file.isEmpty()){
                String fileName = saveImage(file);
                book.setImage(fileName);
            }
        } catch (IOException e) {
            throw new RuntimeException("Không thể lưu");
        }
        return bookRepository.save(book);
    }

    private String saveImage(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);
        return fileName;
    }

    public book updateBook(book book, Long id, MultipartFile file){
        book existingBook = getBookById(id);
        Long categoryId = book.getCategory().getId();
        Long authorId = book.getAuthor().getId();
        Long publisherId = book.getPublisher().getId();
        if(!categoryRepository.existsById(categoryId)){
            throw new RuntimeException("Khong ton tai category");
        }
        if(!authorRepository.existsById(authorId)){
            throw new RuntimeException("Khong ton tai tac gia");
        }
        if(!publisherRepository.existsById(publisherId)){
            throw new RuntimeException("Khong ton tai nha xuat ban");
        }
        existingBook.setBookName(book.getBookName());
        existingBook.setPublishYear(book.getPublishYear());
        existingBook.setQuantity(book.getQuantity());
        existingBook.setPrice(book.getPrice());
        existingBook.setImage(book.getImage());
        existingBook.setCategory(book.getCategory());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setPublisher(book.getPublisher());

        try {
            if(file != null & !file.isEmpty()){
                deleteOldImage(existingBook.getImage());
                String fileName = saveImage(file);
                existingBook.setImage(fileName);
            }
        } catch (IOException e){
            throw new RuntimeException("Không thể cập nhật ảnh");
        }
        return bookRepository.save(existingBook);
    }

    private void deleteOldImage(String fileName) {
        try {
            if (fileName != null && !fileName.isEmpty()){
                Path oldFilePath = uploadPath.resolve(fileName);
                Files.deleteIfExists(oldFilePath);
            }
        } catch (Exception e){
            System.out.println("Không thể xóa");
        }
    }

    public void deleteBook(Long id){
        book existingBook = getBookById(id);
        deleteOldImage(existingBook.getImage());
        bookRepository.delete(existingBook);
        return;
    }
}
