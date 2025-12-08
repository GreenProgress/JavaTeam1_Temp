package JavaProject.Backend.service;

import JavaProject.Backend.domain.Question;
import JavaProject.Backend.domain.Situation; // 추가
import JavaProject.Backend.repository.QuestionRepository;
import JavaProject.Backend.repository.SituationRepository; // 추가
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final SituationRepository situationRepository; // DB 조회용
    private final GeminiService geminiService; // 질문 생성용

    public List<Question> getQuestionsBySituationId(String situationId) {
        ObjectId objectId;
        try {
            objectId = new ObjectId(situationId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 situationId 형식입니다: " + situationId);
        }
        
        // 1. 기존 질문 조회
        List<Question> questions = questionRepository.findBySituationIdOrderByOrderIndexAsc(objectId);

        // 2. 질문이 없다면 AI로 생성하여 저장 후 반환
        if (questions.isEmpty()) {
            Situation situation = situationRepository.findById(situationId)
                    .orElseThrow(() -> new IllegalArgumentException("상황 정보를 찾을 수 없습니다."));

            try {
                // AI에게 질문 생성 요청
                List<Map<String, String>> aiQuestions = geminiService.generateQuestions(situation.getTitle(), situation.getDescription());
                
                int idx = 1;
                for (Map<String, String> qMap : aiQuestions) {
                    Question q = Question.builder()
                            .situationId(objectId)
                            .situationTitle(situation.getTitle())
                            .questionText(qMap.get("questionText"))
                            .helpText(qMap.get("helpText"))
                            .questionType("YES_NO")
                            .orderIndex(idx++)
                            .active(true)
                            .build();
                    
                    // 저장 및 리스트에 추가
                    questions.add(questionRepository.save(q));
                }
            } catch (Exception e) {
                System.err.println("질문 자동 생성 실패: " + e.getMessage());
                // 실패 시 빈 리스트 반환 (프론트엔드에서 처리)
            }
        }
        
        return questions;
    }
}