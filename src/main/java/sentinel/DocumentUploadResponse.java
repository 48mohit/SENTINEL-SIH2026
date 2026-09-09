package sentinel;

public class DocumentUploadResponse {
    private Long documentId;
    private String originalFileName;
    private String documentType;
    private String fileHash;
    private String fileSize;
    private String status;
    private String message;

    public DocumentUploadResponse(Long documentId, String originalFileName,
            String documentType, String fileHash, String fileSize,
            String status, String message) {
        this.documentId = documentId;
        this.originalFileName = originalFileName;
        this.documentType = documentType;
        this.fileHash = fileHash;
        this.fileSize = fileSize;
        this.status = status;
        this.message = message;
    }

    public Long getDocumentId() { return documentId; }
    public String getOriginalFileName() { return originalFileName; }
    public String getDocumentType() { return documentType; }
    public String getFileHash() { return fileHash; }
    public String getFileSize() { return fileSize; }
    public String getStatus() { return status; }
    public String getMessage() { return message; }
}