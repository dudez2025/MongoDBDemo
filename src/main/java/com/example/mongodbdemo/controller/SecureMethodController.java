package com.example.mongodbdemo.controller;

import com.example.mongodbdemo.model.SecureDocument;
import com.example.mongodbdemo.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/secure-methods")
public class SecureMethodController {

    @Autowired
    private DocumentService documentService;

    /**
     * 1. @Secured - только для пользователей с ролью READ
     */
    @Secured("ROLE_READ")
    @GetMapping("/read-only")
    public ResponseEntity<Map<String, String>> getReadOnlyInfo() {
        Map<String, String> response = new HashMap<>();
        response.put("message", documentService.getReadOnlyInfo());
        response.put("annotation", "@Secured(\"ROLE_READ\")");
        response.put("description", "Этот метод доступен только пользователям с ролью READ");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 2. @RolesAllowed - только для пользователей с ролью WRITE
     */
    @RolesAllowed("WRITE")
    @GetMapping("/write-only")
    public ResponseEntity<Map<String, String>> getWriteOnlyInfo() {
        Map<String, String> response = new HashMap<>();
        response.put("message", documentService.getWriteOnlyInfo());
        response.put("annotation", "@RolesAllowed(\"WRITE\")");
        response.put("description", "Этот метод доступен только пользователям с ролью WRITE");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 3. @PreAuthorize - для пользователей с ролью WRITE или DELETE
     */
    @PreAuthorize("hasAnyRole('WRITE', 'DELETE')")
    @GetMapping("/write-or-delete")
    public ResponseEntity<Map<String, String>> getWriteOrDeleteInfo() {
        Map<String, String> response = new HashMap<>();
        response.put("message", documentService.getSecureOperationInfo());
        response.put("annotation", "@PreAuthorize(\"hasAnyRole('WRITE', 'DELETE')\")");
        response.put("description", "Этот метод доступен пользователям с ролью WRITE или DELETE");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 4. Проверка, что username совпадает с аутентифицированным пользователем
     */
    @GetMapping("/user-documents")
    public ResponseEntity<?> getUserDocuments(@RequestParam String username) {
        List<SecureDocument> documents = documentService.getDocumentsByUser(username);
        
        Map<String, Object> response = new HashMap<>();
        response.put("username", username);
        response.put("documents", documents);
        response.put("count", documents.size());
        response.put("annotation", "@PreAuthorize(\"#username == authentication.name\")");
        response.put("description", "Доступ разрешен только если username совпадает с именем текущего пользователя");
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 5. Создание документа (требуется WRITE)
     */
    @PostMapping("/documents")
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<?> createDocument(@Valid @RequestBody SecureDocument document) {
        SecureDocument created = documentService.createDocument(document);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * 6. Получение документа с пост-проверкой доступа
     */
    @GetMapping("/documents/{id}")
    @PreAuthorize("hasRole('READ')")
    public ResponseEntity<?> getDocumentById(@PathVariable String id) {
        Optional<SecureDocument> document = documentService.getDocumentById(id);
        
        if (document.isPresent()) {
            return new ResponseEntity<>(document.get(), HttpStatus.OK);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Document not found");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 7. Получение всех доступных документов с фильтрацией
     */
    @GetMapping("/accessible-documents")
    @PreAuthorize("hasRole('READ')")
    public ResponseEntity<List<SecureDocument>> getAccessibleDocuments() {
        List<SecureDocument> documents = documentService.getAllAccessibleDocuments();
        return new ResponseEntity<>(documents, HttpStatus.OK);
    }

    /**
     * 8. @Secured для DELETE
     */
    @Secured("ROLE_DELETE")
    @DeleteMapping("/documents/{id}")
    public ResponseEntity<?> deleteDocument(@PathVariable String id) {
        documentService.deleteDocument(id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Document deleted successfully");
        response.put("id", id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 9. Информация о всех методах
     */
    @GetMapping("/info")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getSecurityInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("available_roles", new String[]{"READ", "WRITE", "DELETE"});
        info.put("test_users", Map.of(
            "reader", "reader (только READ)",
            "writer", "writer (только WRITE)",
            "deleter", "deleter (только DELETE)",
            "editor", "editor (READ и WRITE)",
            "admin", "admin (READ, WRITE, DELETE)"
        ));
        info.put("endpoints", Map.of(
            "GET /api/secure-methods/read-only", "@Secured - только READ",
            "GET /api/secure-methods/write-only", "@RolesAllowed - только WRITE",
            "GET /api/secure-methods/write-or-delete", "@PreAuthorize - WRITE или DELETE",
            "GET /api/secure-methods/user-documents?username={name}", "Доступ только для владельца",
            "POST /api/secure-methods/documents", "Создание документа (WRITE)",
            "GET /api/secure-methods/documents/{id}", "Чтение документа с пост-проверкой",
            "GET /api/secure-methods/accessible-documents", "Все доступные документы с фильтрацией",
            "DELETE /api/secure-methods/documents/{id}", "Удаление документа (DELETE)"
        ));
        
        return new ResponseEntity<>(info, HttpStatus.OK);
    }
}