package com.example.mongodbdemo.repository;

import com.example.mongodbdemo.model.SecureDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends MongoRepository<SecureDocument, String> {
    
    List<SecureDocument> findByOwner(String owner);
    
    List<SecureDocument> findByConfidentialFalse();
}