package com.example.mongodbdemo.repository;

import com.example.mongodbdemo.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    // Find users by name (case-insensitive)
    List<User> findByNameIgnoreCase(String name);
    
    // Find users by age range
    List<User> findByAgeBetween(Integer minAge, Integer maxAge);
    
    // Find users older than specified age
    List<User> findByAgeGreaterThan(Integer age);
    
    // Find users younger than specified age
    List<User> findByAgeLessThan(Integer age);
    
    // Custom query using MongoDB JSON syntax
    @Query("{ 'email' : ?0 }")
    User findByEmailAddress(String email);
    
    // Check if user exists by email
    boolean existsByEmail(String email);
}