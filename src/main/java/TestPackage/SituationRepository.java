package TestPackage;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface SituationRepository extends MongoRepository<Situation, String> {
    
    List<Situation> findByTitle(String title);
    long deleteByTitle(String title);

    List<Situation> findByCategoryAndActive(String category, boolean active);
}