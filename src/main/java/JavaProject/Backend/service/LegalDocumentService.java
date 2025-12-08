package JavaProject.Backend.service;

import JavaProject.Backend.domain.LegalDocument;
import JavaProject.Backend.repository.LegalDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class LegalDocumentService {

    private final LegalDocumentRepository repository;

    /**
     * 검색: 제목 / 조항제목 / 본문
     * (법령 '제목' 기준 중복 제거)
     */
    public List<LegalDocument> searchByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String trimmed = keyword.trim();

        // 1. 모든 검색 결과 가져오기
        List<LegalDocument> titleMatches = repository.findByTitleContainingIgnoreCase(trimmed);
        List<LegalDocument> articleTitleMatches = repository.findByArticleTitleContainingIgnoreCase(trimmed);
        List<LegalDocument> contentMatches = repository.findByArticleTextContainingIgnoreCase(trimmed);

        // 2. 통합 리스트 생성
        List<LegalDocument> allMatches = new ArrayList<>();
        allMatches.addAll(titleMatches);
        allMatches.addAll(articleTitleMatches);
        allMatches.addAll(contentMatches);

        // 3. 중복 제거 (Key: 법령 제목 String)
        Map<String, LegalDocument> uniqueLawsByTitle = new LinkedHashMap<>();

        for (LegalDocument doc : allMatches) {
            String title = doc.getTitle();
            if (title != null && !title.trim().isEmpty()) {
                uniqueLawsByTitle.putIfAbsent(title.trim(), doc);
            }
        }

        return new ArrayList<>(uniqueLawsByTitle.values());
    }

    /**
     * 법령 상세 조회 (병합 로직)
     */
    public Map<String, Object> getMergedLawByLawId(String lawId) {
        if (lawId == null || lawId.trim().isEmpty()) {
            return null;
        }

        List<LegalDocument> docs = repository.findByLawId(lawId);

        if (docs == null || docs.isEmpty()) {
            return null;
        }

        LegalDocument first = docs.get(0);
        List<Map<String, String>> articles = new ArrayList<>();

        for (LegalDocument d : docs) {
            Map<String, String> article = new LinkedHashMap<>();
            article.put("articleNo", Optional.ofNullable(d.getArticleNo()).orElse("0"));
            article.put("articleTitle", Optional.ofNullable(d.getArticleTitle()).orElse(""));
            article.put("articleText", Optional.ofNullable(d.getArticleText()).orElse(""));
            articles.add(article);
        }

        // 정렬 로직: 숫자 변환 가능한 경우 숫자로 비교
        articles.sort((a, b) -> {
            String no1 = a.get("articleNo");
            String no2 = b.get("articleNo");
            try {
                return Integer.compare(Integer.parseInt(no1), Integer.parseInt(no2));
            } catch (NumberFormatException e) {
                return no1.compareTo(no2);
            }
        });

        Map<String, Object> merged = new LinkedHashMap<>();
        merged.put("lawId", lawId);
        merged.put("title", first.getTitle());
        merged.put("sourceUrl", first.getSourceUrl());
        merged.put("articles", articles);

        return merged;
    }
}