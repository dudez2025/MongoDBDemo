package com.example.mongodbdemo.controller;

import com.example.mongodbdemo.model.User;
import com.example.mongodbdemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // GET всех пользователей - требуют авторизации (USER или ADMIN)
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // GET пользователя по ID - требуют авторизации
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        Optional<User> user = userRepository.findById(id);
        
        if (user.isPresent()) {
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not found with id: " + id);
            errorResponse.put("timestamp", String.valueOf(System.currentTimeMillis()));
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    // GET поиск по имени - требуют авторизации
    @GetMapping("/search/name")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<User>> getUsersByName(@RequestParam String name) {
        List<User> users = userRepository.findByNameIgnoreCase(name);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // GET поиск по возрасту - требуют авторизации
    @GetMapping("/search/age-range")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<User>> getUsersByAgeRange(
            @RequestParam Integer minAge,
            @RequestParam Integer maxAge) {
        List<User> users = userRepository.findByAgeBetween(minAge, maxAge);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // GET пользователя по email - требуют авторизации
    @GetMapping("/search/email")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> getUserByEmail(@RequestParam String email) {
        User user = userRepository.findByEmailAddress(email);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not found with email: " + email);
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    // POST создание пользователя - только ADMIN
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        // Проверка на существующий email
        if (userRepository.existsByEmail(user.getEmail())) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Email already exists: " + user.getEmail());
            errorResponse.put("timestamp", String.valueOf(System.currentTimeMillis()));
            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }
        
        User savedUser = userRepository.save(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    // PUT обновление пользователя - только ADMIN
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable String id, @Valid @RequestBody User userDetails) {
        Optional<User> userOptional = userRepository.findById(id);
        
        if (!userOptional.isPresent()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not found with id: " + id);
            errorResponse.put("timestamp", String.valueOf(System.currentTimeMillis()));
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        
        User user = userOptional.get();
        
        // Проверка уникальности email при его изменении
        if (!user.getEmail().equals(userDetails.getEmail()) && 
            userRepository.existsByEmail(userDetails.getEmail())) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Email already exists: " + userDetails.getEmail());
            errorResponse.put("timestamp", String.valueOf(System.currentTimeMillis()));
            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }
        
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setAge(userDetails.getAge());
        
        User updatedUser = userRepository.save(user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    // DELETE пользователя - только ADMIN
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        Optional<User> user = userRepository.findById(id);
        
        if (!user.isPresent()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not found with id: " + id);
            errorResponse.put("timestamp", String.valueOf(System.currentTimeMillis()));
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        
        userRepository.deleteById(id);
        
        Map<String, String> successResponse = new HashMap<>();
        successResponse.put("message", "User deleted successfully");
        successResponse.put("id", id);
        successResponse.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    // DELETE всех пользователей - только ADMIN
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteAllUsers() {
        long count = userRepository.count();
        userRepository.deleteAll();
        
        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("message", "All users deleted successfully");
        successResponse.put("deletedCount", count);
        successResponse.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }
}