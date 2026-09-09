package sentinel;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {

    private static final String UPLOAD_DIR = "uploads/documents/";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "application/pdf"
    );
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
        ".jpg", ".jpeg", ".png", ".pdf"
    );

    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public DocumentUploadResponse uploadDocument(
            MultipartFile file, String documentType, Long userId) throws IOException {

        // Validate file not empty
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Validate file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds 10MB limit");
        }

        // Validate content type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Invalid file type. Only JPG, PNG, PDF allowed");
        }

        // Validate extension
        String originalName = file.getOriginalFilename();
        if (originalName == null) {
            throw new IllegalArgumentException("Invalid file name");
        }
        String extension = originalName.substring(
            originalName.lastIndexOf(".")).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Invalid file extension");
        }

        // Generate SHA-256 hash
        String fileHash = generateHash(file.getBytes());

        // Check for duplicate
        if (documentRepository.findByFileHash(fileHash).isPresent()) {
            throw new IllegalArgumentException("Duplicate document detected");
        }

        // Create upload directory
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save file with unique name
        String storedFileName = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(storedFileName);
        Files.write(filePath, file.getBytes());

        // Save to database
        Document document = new Document();
        document.setOriginalFileName(originalName);
        document.setStoredFileName(storedFileName);
        document.setDocumentType(documentType);
        document.setFilePath(filePath.toString());
        document.setFileHash(fileHash);
        document.setFileSize(formatFileSize(file.getSize()));
        document.setStatus("UPLOADED");
        document.setUploadedBy(userId);

        Document saved = documentRepository.save(document);

        return new DocumentUploadResponse(
            saved.getId(),
            saved.getOriginalFileName(),
            saved.getDocumentType(),
            saved.getFileHash(),
            saved.getFileSize(),
            saved.getStatus(),
            "Document uploaded successfully"
        );
    }

    private String generateHash(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating hash", e);
        }
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        else if (bytes < 1024 * 1024) return (bytes / 1024) + " KB";
        else return (bytes / (1024 * 1024)) + " MB";
    }

    public List<Document> getDocumentsByUser(Long userId) {
        return documentRepository.findByUploadedBy(userId);
    }

    public Document getDocumentById(Long id) {
        return documentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Document not found"));
    }
}