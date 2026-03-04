package com.example.mongodbdemo.service;

import com.example.mongodbdemo.model.SecureDocument;
import com.example.mongodbdemo.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @PreAuthorize("hasRole('READ')")
    public List<SecureDocument> getAllDocuments() {
        return documentRepository.findAll();
    }

    @PreAuthorize("hasRole('WRITE')")
    public SecureDocument createDocument(SecureDocument document) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        document.setOwner(username);
        return documentRepository.save(document);
    }

    @PreAuthorize("hasRole('DELETE')")
    public void deleteDocument(String id) {
        documentRepository.deleteById(id);
    }

    @PostAuthorize("returnObject.map(doc -> doc.owner == authentication.name or !doc.confidential).orElse(true)")
    public Optional<SecureDocument> getDocumentById(String id) {
        return documentRepository.findById(id);
    }

    @PreAuthorize("hasAnyRole('WRITE', 'DELETE')")
    public String getSecureOperationInfo() {
        return "This operation requires WRITE or DELETE role. You have sufficient privileges.";
    }

    @PreAuthorize("hasRole('WRITE')")
    @PreFilter("filterObject.owner == authentication.name or filterObject.content.length() <= 100")
    public List<SecureDocument> createMultipleDocuments(List<SecureDocument> documents) {
        return documentRepository.saveAll(documents);
    }

    @PreAuthorize("hasRole('READ')")
    @PostFilter("filterObject.owner == authentication.name or !filterObject.confidential")
    public List<SecureDocument> getAllAccessibleDocuments() {
        return documentRepository.findAll();
    }

    @PreAuthorize("#username == authentication.name")
    public List<SecureDocument> getDocumentsByUser(@org.springframework.security.core.parameters.P String username) {
        return documentRepository.findByOwner(username);
    }

    @Secured("ROLE_READ")
    public String getReadOnlyInfo() {
        return "This information is readable by users with READ role. You have READ access.";
    }

    @RolesAllowed("WRITE")
    public String getWriteOnlyInfo() {
        return "This information is writable by users with WRITE role. You have WRITE access.";
    }
}