package sentinel;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "risk_assessments")
public class RiskAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long documentId;

    @Column(nullable = false)
    private int totalScore;

    @Column(nullable = false)
    private String riskLevel;

    @Column
    private int validationScore;

    @Column
    private int metadataScore;

    @Column
    private int fileIntegrityScore;

    @Column
    private int documentTypeScore;

    @Column(length = 2000)
    private String explanation;

    @Column(length = 2000)
    private String recommendations;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime assessedAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDocumentId() { return documentId; }
    public void setDocumentId(Long documentId) { this.documentId = documentId; }
    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    public int getValidationScore() { return validationScore; }
    public void setValidationScore(int validationScore) { this.validationScore = validationScore; }
    public int getMetadataScore() { return metadataScore; }
    public void setMetadataScore(int metadataScore) { this.metadataScore = metadataScore; }
    public int getFileIntegrityScore() { return fileIntegrityScore; }
    public void setFileIntegrityScore(int fileIntegrityScore) { this.fileIntegrityScore = fileIntegrityScore; }
    public int getDocumentTypeScore() { return documentTypeScore; }
    public void setDocumentTypeScore(int documentTypeScore) { this.documentTypeScore = documentTypeScore; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getAssessedAt() { return assessedAt; }
}