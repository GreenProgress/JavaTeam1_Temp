package JavaProject.Backend.repository;

import JavaProject.Backend.domain.Question;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends MongoRepository<Question, String> {
    
    List<Question> findBySituationIdAndActiveTrueOrderByOrderIndexAsc(ObjectId situationId);
    
    List<Question> findBySituationIdOrderByOrderIndexAsc(ObjectId situationId);
    
    long countBySituationIdAndActiveTrue(ObjectId situationId);
}