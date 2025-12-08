package JavaProject.Backend.service;

import JavaProject.Backend.domain.*;
import JavaProject.Backend.repository.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalysisService {
    
    private final UserResponseRepository userResponseRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final QuestionRepository questionRepository;
    private final GeminiService geminiService;
    
    private Map<String, AnalysisResultTemplate> templateMap = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        try {
            // resources/analysis-templates.json 파일 로드
            ClassPathResource resource = new ClassPathResource("analysis-templates.json");
            if(resource.exists()){
                Map<String, AnalysisResultTemplate> rawMap = objectMapper.readValue(
                    resource.getInputStream(), 
                    new TypeReference<Map<String, AnalysisResultTemplate>>() {}
                );
                // CopyFrom 처리
                for(Map.Entry<String, AnalysisResultTemplate> entry : rawMap.entrySet()){
                    if(entry.getValue().getCopyFrom() != null){
                        AnalysisResultTemplate original = rawMap.get(entry.getValue().getCopyFrom());
                        if(original != null) templateMap.put(entry.getKey(), original);
                    } else {
                        templateMap.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("JSON 템플릿 로드 실패: " + e.getMessage());
        }
    }

    public AnalysisResult analyzeResponses(String sessionId, String userId, String situationId) {
        ObjectId sitIdObj = new ObjectId(situationId);
        List<Question> questions = questionRepository.findBySituationIdOrderByOrderIndexAsc(sitIdObj);
        List<UserResponse> responses = userResponseRepository.findBySessionIdAndSituationIdOrderByCreatedAtDesc(sessionId, sitIdObj);
        
        int qCount = questions.size();
        if(responses.size() > qCount) responses = responses.subList(0, qCount);
        
        String scenarioKey = responses.stream()
                .sorted(Comparator.comparingInt(UserResponse::getOrderIndex))
                .map(r -> r.getResponseValue().toUpperCase())
                .collect(Collectors.joining("_"));
        String fullKey = situationId + "_" + scenarioKey;

        return analysisResultRepository.save(buildResult(sessionId, userId, situationId, fullKey, questions, responses));
    }

    private AnalysisResult buildResult(String sessionId, String userId, String situationId, String key, List<Question> questions, List<UserResponse> responses) {
        String title = questions.isEmpty() ? "진단 결과" : questions.get(0).getSituationTitle();
        
        AnalysisResult.AnalysisResultBuilder builder = AnalysisResult.builder()
                .sessionId(sessionId)
                .userId(userId)
                .situationId(situationId)
                .situationTitle(title) // 제목 저장
                .pdfGenerationStatus("READY");

        AnalysisResultTemplate template = templateMap.get(key);

        if (template != null) {
            builder.resultSummary(template.getResultSummary())
                   .procedures(template.getProcedures())
                   .checklist(template.getChecklist());
            if (template.getRelatedLaws() != null) {
                 List<AnalysisResult.RelatedLawInfo> laws = template.getRelatedLaws().stream()
                    .map(t -> new AnalysisResult.RelatedLawInfo(t.getLawId(), t.getTitle(), t.getRelevantArticles()))
                    .collect(Collectors.toList());
                builder.relatedLaws(laws);
            }
        } else {
            // AI 생성 로직
            try {
                AnalysisResult aiResult = geminiService.generateAnalysis(title, responses, questions);
                builder.resultSummary(aiResult.getResultSummary())
                       .procedures(aiResult.getProcedures())
                       .checklist(aiResult.getChecklist())
                       .relatedLaws(aiResult.getRelatedLaws());
            } catch (Exception e) {
                builder.resultSummary("AI 분석 실패: " + e.getMessage());
            }
        }
        return builder.build();
    }
    
    public List<AnalysisResult> getResultsByUser(String userId) {
        return analysisResultRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    public Optional<AnalysisResult> getResultById(String id) {
        return analysisResultRepository.findById(id);
    }
    public void deleteResult(String id) { analysisResultRepository.deleteById(id); }
    public void deleteAllResultsByUser(String userId) { analysisResultRepository.deleteByUserId(userId); }
}