package sentinel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RiskAssessmentRepository extends JpaRepository<RiskAssessment, Long> {
    Optional<RiskAssessment> findByDocumentId(Long documentId);
    List<RiskAssessment> findByRiskLevel(String riskLevel);
    List<RiskAssessment> findByTotalScoreGreaterThan(int score);
}