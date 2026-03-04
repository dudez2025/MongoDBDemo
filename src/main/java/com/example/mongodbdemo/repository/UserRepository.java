package com.example.mongodbdemo.repository;

import com.example.mongodbdemo.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    List<User> findByNameIgnoreCase(String name);
    List<User> findByAgeBetween(Integer minAge, Integer maxAge);
    List<User> findByAgeGreaterThan(Integer age);
    List<User> findByAgeLessThan(Integer age);
    
    @Query("{ 'email' : ?0 }")
    User findByEmailAddress(String email);
    
    boolean existsByEmail(String email);
}