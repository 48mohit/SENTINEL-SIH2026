package sentinel;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/risk")
@CrossOrigin(origins = "http://localhost:3000")
public class RiskController {

    private final RiskScoringService riskScoringService;
    private final RiskAssessmentRepository riskAssessmentRepository;

    public RiskController(RiskScoringService riskScoringService,
            RiskAssessmentRepository riskAssessmentRepository) {
        this.riskScoringService = riskScoringService;
        this.riskAssessmentRepository = riskAssessmentRepository;
    }

    // Assess a document
    @PostMapping("/assess/{documentId}")
    public ResponseEntity<ApiResponse> assessDocument(@PathVariable Long documentId) {
        try {
            RiskAssessment assessment = riskScoringService.assessDocument(documentId);
            return ResponseEntity.ok(
                new ApiResponse(true, "Risk assessment completed", assessment));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // Get assessment for a document
    @GetMapping("/document/{documentId}")
    public ResponseEntity<ApiResponse> getAssessment(@PathVariable Long documentId) {
        try {
            RiskAssessment assessment = riskAssessmentRepository
                .findByDocumentId(documentId)
                .orElseThrow(() -> new RuntimeException("No assessment found"));
            return ResponseEntity.ok(new ApiResponse(true, "Assessment found", assessment));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get all high risk documents
    @GetMapping("/high-risk")
    public ResponseEntity<ApiResponse> getHighRisk() {
        List<RiskAssessment> highRisk = riskAssessmentRepository
            .findByTotalScoreGreaterThan(59);
        return ResponseEntity.ok(new ApiResponse(true, "High risk documents", highRisk));
    }

    // Get dashboard statistics
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse> getStats() {
        List<RiskAssessment> all = riskAssessmentRepository.findAll();
        long low = all.stream().filter(r -> r.getRiskLevel().equals("LOW")).count();
        long medium = all.stream().filter(r -> r.getRiskLevel().equals("MEDIUM")).count();
        long high = all.stream().filter(r -> r.getRiskLevel().equals("HIGH")).count();
        long critical = all.stream().filter(r -> r.getRiskLevel().equals("CRITICAL")).count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", all.size());
        stats.put("low", low);
        stats.put("medium", medium);
        stats.put("high", high);
        stats.put("critical", critical);
        stats.put("averageScore", all.stream()
            .mapToInt(RiskAssessment::getTotalScore).average().orElse(0));

        return ResponseEntity.ok(new ApiResponse(true, "Statistics", stats));
    }
}