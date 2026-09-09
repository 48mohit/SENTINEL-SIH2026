package sentinel;

import org.springframework.stereotype.Service;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class RiskScoringService {

    private final RiskAssessmentRepository riskAssessmentRepository;
    private final DocumentRepository documentRepository;

    public RiskScoringService(RiskAssessmentRepository riskAssessmentRepository,
            DocumentRepository documentRepository) {
        this.riskAssessmentRepository = riskAssessmentRepository;
        this.documentRepository = documentRepository;
    }

    public RiskAssessment assessDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Document not found"));

        List<String> explanations = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();
        int totalScore = 0;

        // Signal 1: Document Type Validation (0-25 points)
        int documentTypeScore = assessDocumentType(document, explanations, recommendations);
        totalScore += documentTypeScore;

        // Signal 2: File Metadata Analysis (0-25 points)
        int metadataScore = assessFileMetadata(document, explanations, recommendations);
        totalScore += metadataScore;

        // Signal 3: File Integrity Check (0-25 points)
        int fileIntegrityScore = assessFileIntegrity(document, explanations, recommendations);
        totalScore += fileIntegrityScore;

        // Signal 4: Document Validation Rules (0-25 points)
        int validationScore = assessDocumentValidation(document, explanations, recommendations);
        totalScore += validationScore;

        // Determine risk level
        String riskLevel = determineRiskLevel(totalScore);

        // Build explanation
        String explanation = String.join(" | ", explanations);
        String recommendation = String.join(" | ", recommendations);

        // Save assessment
        RiskAssessment assessment = new RiskAssessment();
        assessment.setDocumentId(documentId);
        assessment.setTotalScore(totalScore);
        assessment.setRiskLevel(riskLevel);
        assessment.setDocumentTypeScore(documentTypeScore);
        assessment.setMetadataScore(metadataScore);
        assessment.setFileIntegrityScore(fileIntegrityScore);
        assessment.setValidationScore(validationScore);
        assessment.setExplanation(explanation);
        assessment.setRecommendations(recommendation);
        assessment.setStatus("COMPLETED");

        return riskAssessmentRepository.save(assessment);
    }

    private int assessDocumentType(Document doc, List<String> explanations,
            List<String> recommendations) {
        int score = 0;
        String docType = doc.getDocumentType().toUpperCase();

        if (docType.equals("PASSPORT") || docType.equals("NATIONAL_ID") ||
            docType.equals("VISA") || docType.equals("DRIVING_LICENSE")) {
            explanations.add("Document type '" + docType + "' is recognized and valid (+0)");
        } else {
            score += 15;
            explanations.add("+15: Unknown document type '" + docType + "' detected");
            recommendations.add("Verify document type manually");
        }

        String fileName = doc.getOriginalFileName().toLowerCase();
        if (!fileName.endsWith(".jpg") && !fileName.endsWith(".jpeg") &&
            !fileName.endsWith(".png") && !fileName.endsWith(".pdf")) {
            score += 10;
            explanations.add("+10: Suspicious file extension detected");
            recommendations.add("Only accept JPG, PNG, PDF documents");
        }

        return Math.min(score, 25);
    }

    private int assessFileMetadata(Document doc, List<String> explanations,
            List<String> recommendations) {
        int score = 0;

        try {
            Path filePath = Paths.get(doc.getFilePath());
            if (!Files.exists(filePath)) {
                score += 25;
                explanations.add("+25: Document file not found on server");
                recommendations.add("Re-upload the document");
                return score;
            }

            long fileSize = Files.size(filePath);

            if (fileSize < 1024) {
                score += 20;
                explanations.add("+20: File size extremely small (" + fileSize + " bytes) — possible corruption");
                recommendations.add("Request original high-quality document");
            } else if (fileSize > 20 * 1024 * 1024) {
                score += 10;
                explanations.add("+10: File size unusually large — possible embedded content");
                recommendations.add("Scan file for embedded objects");
            } else {
                explanations.add("File size normal: " + doc.getFileSize() + " (+0)");
            }

            String fileName = doc.getOriginalFileName();
            if (fileName.contains("copy") || fileName.contains("edited") ||
                fileName.contains("modified") || fileName.contains("fake")) {
                score += 15;
                explanations.add("+15: Suspicious keywords in filename: " + fileName);
                recommendations.add("Verify document authenticity");
            }

        } catch (Exception e) {
            score += 15;
            explanations.add("+15: Error reading file metadata");
        }

        return Math.min(score, 25);
    }

    private int assessFileIntegrity(Document doc, List<String> explanations,
            List<String> recommendations) {
        int score = 0;

        String hash = doc.getFileHash();
        if (hash == null || hash.isEmpty()) {
            score += 25;
            explanations.add("+25: No integrity hash found — cannot verify document");
            recommendations.add("Re-upload document to generate integrity hash");
            return score;
        }

        if (hash.length() != 64) {
            score += 20;
            explanations.add("+20: Invalid SHA-256 hash length — possible tampering");
            recommendations.add("Verify document integrity");
        } else {
            explanations.add("Document integrity hash verified (SHA-256) (+0)");
        }

        try {
            Path filePath = Paths.get(doc.getFilePath());
            if (Files.exists(filePath)) {
                byte[] bytes = Files.readAllBytes(filePath);
                java.security.MessageDigest digest =
                    java.security.MessageDigest.getInstance("SHA-256");
                byte[] hashBytes = digest.digest(bytes);
                StringBuilder sb = new StringBuilder();
                for (byte b : hashBytes) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) sb.append('0');
                    sb.append(hex);
                }
                String currentHash = sb.toString();

                if (!currentHash.equals(hash)) {
                    score += 25;
                    explanations.add("+25: CRITICAL — File hash mismatch detected! Document may have been tampered");
                    recommendations.add("URGENT: Do not process this document — possible tampering detected");
                } else {
                    explanations.add("Hash integrity verified — file unchanged since upload (+0)");
                }
            }
        } catch (Exception e) {
            score += 10;
            explanations.add("+10: Could not verify file hash");
        }

        return Math.min(score, 25);
    }

    private int assessDocumentValidation(Document doc, List<String> explanations,
            List<String> recommendations) {
        int score = 0;

        if (doc.getOriginalFileName() == null || doc.getOriginalFileName().isEmpty()) {
            score += 10;
            explanations.add("+10: Missing original filename");
        }

        if (doc.getDocumentType() == null || doc.getDocumentType().isEmpty()) {
            score += 15;
            explanations.add("+15: Missing document type classification");
            recommendations.add("Classify document type before processing");
        }

        String storedName = doc.getStoredFileName();
        if (storedName == null || storedName.length() < 10) {
            score += 10;
            explanations.add("+10: Invalid stored filename format");
        } else {
            explanations.add("Document storage reference valid (+0)");
        }

        return Math.min(score, 25);
    }

    private String determineRiskLevel(int score) {
        if (score <= 29) return "LOW";
        else if (score <= 59) return "MEDIUM";
        else if (score <= 79) return "HIGH";
        else return "CRITICAL";
    }
}