package JavaProject.Backend.service;

import JavaProject.Backend.domain.Situation;
import JavaProject.Backend.repository.SituationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import JavaProject.Backend.domain.Question;
import JavaProject.Backend.repository.QuestionRepository;
import org.bson.types.ObjectId;
import JavaProject.Backend.domain.RelatedLaw;         
import JavaProject.Backend.repository.LegalDocumentRepository; 

import java.util.ArrayList;
import java.util.LinkedHashMap; // 순서 유지를 위해 LinkedHashMap 사용
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SituationService {

    private final SituationRepository situationRepository;
    private final QuestionRepository questionRepository;
    private final GeminiService geminiService; 
    private final LegalDocumentRepository legalDocumentRepository; 

    public List<Situation> getAllSituations() {
        return situationRepository.findAll();
    }

    public Situation getSituationById(String id) {
        Situation situation = situationRepository.findById(id).orElse(null);
        if (situation != null && situation.getRelatedLaws() != null) {
             for (RelatedLaw rl : situation.getRelatedLaws()) {
                if (rl.getLawId() != null && !rl.getLawId().trim().isEmpty()) {
                    legalDocumentRepository.findFirstByLawId(rl.getLawId())
                            .ifPresentOrElse(doc -> rl.setTitle(doc.getTitle()), () -> rl.setTitle("(제목 없음)"));
                }
            }
        }
        return situation;
    }

    // [수정] ID 기준 중복 제거 적용
    public List<Situation> searchSituations(String keyword) {
        // 1. DB 검색 (제목, 설명, 카테고리)
        List<Situation> titleMatches = situationRepository.findByTitleContainingIgnoreCase(keyword);
        List<Situation> descMatches = situationRepository.findByDescriptionContainingIgnoreCase(keyword);
        List<Situation> catMatches = situationRepository.findByCategoryContainingIgnoreCase(keyword);
        
        // 2. 중복 제거 (Key: 상황 ID)
        // LinkedHashMap을 사용하여 조회된 순서(제목 우선)를 유지하며 중복을 제거합니다.
        Map<String, Situation> uniqueSituations = new LinkedHashMap<>();

        for (Situation s : titleMatches) uniqueSituations.putIfAbsent(s.getId(), s);
        for (Situation s : descMatches) uniqueSituations.putIfAbsent(s.getId(), s);
        for (Situation s : catMatches) uniqueSituations.putIfAbsent(s.getId(), s);
        
        if (!uniqueSituations.isEmpty()) {
            return new ArrayList<>(uniqueSituations.values());
        }

        // 3. DB에 없으면 AI 생성 (기존 로직 유지)
        try {
            System.out.println("AI 생성 시작: " + keyword);
            Map<String, Object> aiData = geminiService.createSituationAndQuestions(keyword);
            String aiTitle = (String) aiData.get("title");

            // 중복 방지 (제목으로 재확인)
            List<Situation> exists = situationRepository.findByTitleContainingIgnoreCase(aiTitle);
            if (!exists.isEmpty()) return exists;

            // 상황 저장
            Situation newSituation = new Situation();
            newSituation.setTitle(aiTitle);
            newSituation.setDescription((String) aiData.get("description"));
            newSituation.setCategory((String) aiData.get("category"));
            newSituation.setActive(true);
            Situation saved = situationRepository.save(newSituation);

            // 질문 저장
            List<Map<String, String>> aiQuestions = (List<Map<String, String>>) aiData.get("questions");
            int idx = 1;
            for (Map<String, String> qMap : aiQuestions) {
                Question q = Question.builder()
                        .situationId(new ObjectId(saved.getId()))
                        .situationTitle(saved.getTitle())
                        .questionText(qMap.get("questionText"))
                        .helpText(qMap.get("helpText"))
                        .questionType("YES_NO")
                        .orderIndex(idx++)
                        .active(true)
                        .build();
                questionRepository.save(q);
            }
            return List.of(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}