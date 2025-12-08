package JavaProject.Backend.repository;

import JavaProject.Backend.domain.LegalDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LegalDocumentRepository extends MongoRepository<LegalDocument, String> {

    // 제목 검색
    List<LegalDocument> findByTitleContainingIgnoreCase(String keyword);

    // 조문 제목 검색
    List<LegalDocument> findByArticleTitleContainingIgnoreCase(String keyword);

    // 조문 내용 검색
    List<LegalDocument> findByArticleTextContainingIgnoreCase(String keyword);

    // 법령 코드로 조문 여러 개 조회 (병합용)
    List<LegalDocument> findByLawId(String lawId);

    // SituationService에서 사용하는 메서드
    Optional<LegalDocument> findFirstByLawId(String lawId);
}
