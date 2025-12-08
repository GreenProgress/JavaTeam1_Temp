package JavaProject.Backend.repository;

import JavaProject.Backend.domain.Situation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SituationRepository extends MongoRepository<Situation, String> {

    // 제목 검색
    List<Situation> findByTitleContainingIgnoreCase(String title);

    // 설명 검색
    List<Situation> findByDescriptionContainingIgnoreCase(String description);

    // 카테고리 검색
    List<Situation> findByCategoryContainingIgnoreCase(String category);
}