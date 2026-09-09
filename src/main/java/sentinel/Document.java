package sentinel;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String storedFileName;

    @Column(nullable = false)
    private String documentType;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private String fileHash;

    @Column(nullable = false)
    private String fileSize;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private Long uploadedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }
    public String getStoredFileName() { return storedFileName; }
    public void setStoredFileName(String storedFileName) { this.storedFileName = storedFileName; }
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public String getFileHash() { return fileHash; }
    public void setFileHash(String fileHash) { this.fileHash = fileHash; }
    public String getFileSize() { return fileSize; }
    public void setFileSize(String fileSize) { this.fileSize = fileSize; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(Long uploadedBy) { this.uploadedBy = uploadedBy; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
}