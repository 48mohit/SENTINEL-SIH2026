package sentinel;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "http://localhost:3000")
public class DocumentController {

    private final DocumentService documentService;
    private final UserRepository userRepository;

    public DocumentController(DocumentService documentService,
            UserRepository userRepository) {
        this.documentService = documentService;
        this.userRepository = userRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") String documentType,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

            DocumentUploadResponse response = documentService
                .uploadDocument(file, documentType, user.getId());

            return ResponseEntity.ok(
                new ApiResponse(true, "Document uploaded successfully", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new ApiResponse(false, "Upload failed: " + e.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getMyDocuments(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
            List<Document> documents = documentService.getDocumentsByUser(user.getId());
            return ResponseEntity.ok(new ApiResponse(true, "Documents retrieved", documents));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getDocument(@PathVariable Long id) {
        try {
            Document document = documentService.getDocumentById(id);
            return ResponseEntity.ok(new ApiResponse(true, "Document found", document));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}