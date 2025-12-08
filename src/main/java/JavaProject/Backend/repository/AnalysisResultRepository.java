package JavaProject.Backend.repository;

import JavaProject.Backend.domain.AnalysisResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnalysisResultRepository extends MongoRepository<AnalysisResult, String> {
    List<AnalysisResult> findByUserIdOrderByCreatedAtDesc(String userId);
    void deleteByUserId(String userId);
}