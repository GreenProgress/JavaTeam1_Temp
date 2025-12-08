package JavaProject.Backend.service;

import JavaProject.Backend.domain.*;
import JavaProject.Backend.repository.LegalDocumentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GeminiService {

    @Value("${google.ai.api-key}") 
    private String apiKey;

    private final LegalDocumentRepository legalDocumentRepository;

    private final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-preview-09-2025:generateContent";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 1. [기존] 검색어 기반 상황 + 질문 생성
    public Map<String, Object> createSituationAndQuestions(String keyword) {
        String prompt = String.format(
            "사용자가 법률 문제로 '%s'를 검색했어. 이와 관련된 가장 대표적이고 구체적인 '법률 상황' 1개와, 이를 진단하기 위한 'YES/NO 질문' 3개를 한국어로 만들어줘. " +
            "반드시 아래 JSON 형식으로만 출력해 (마크다운 없이 순수 JSON):\n" +
            "{\n" +
            "  \"title\": \"상황 제목\",\n" +
            "  \"description\": \"[AI 생성] 상황 설명...\",\n" +
            "  \"category\": \"카테고리\",\n" +
            "  \"questions\": [\n" +
            "    { \"questionText\": \"질문1\", \"helpText\": \"도움말1\" },\n" +
            "    { \"questionText\": \"질문2\", \"helpText\": \"도움말2\" },\n" +
            "    { \"questionText\": \"질문3\", \"helpText\": \"도움말3\" }\n" +
            "  ]\n" +
            "}", keyword);
        return callGemini(prompt);
    }

    // 2. [기존] 상황에 대한 질문만 생성
    public List<Map<String, String>> generateQuestions(String title, String description) {
        String prompt = String.format(
            "법률 상황: '%s'\n설명: %s\n\n" +
            "이 상황을 법률적으로 진단하기 위한 'YES/NO 질문' 3개를 한국어로 만들어줘. " +
            "반드시 아래 JSON 형식으로만 출력해 (마크다운 없이 순수 JSON):\n" +
            "{ \"questions\": [\n" +
            "  { \"questionText\": \"질문1\", \"helpText\": \"도움말1\" },\n" +
            "  { \"questionText\": \"질문2\", \"helpText\": \"도움말2\" },\n" +
            "  { \"questionText\": \"질문3\", \"helpText\": \"도움말3\" }\n" +
            "] }", title, description);

        try {
            Map<String, Object> response = callGemini(prompt);
            return (List<Map<String, String>>) response.get("questions"); 
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); 
        }
    }

    // 3. [수정됨] 답변 기반 분석 결과 생성
    public AnalysisResult generateAnalysis(String situationTitle, List<UserResponse> responses, List<Question> questions) {
        StringBuilder qnaBuilder = new StringBuilder();
        for (UserResponse resp : responses) {
            questions.stream()
                    .filter(q -> q.getId().equals(resp.getQuestionId()))
                    .findFirst()
                    .ifPresent(q -> qnaBuilder.append(String.format("- 질문: %s / 답변: %s\n", q.getQuestionText(), resp.getResponseValue())));
        }

        // 프롬프트 수정: lawId 요청 부분 단순화 (굳이 ID를 강제하지 않음)
        String prompt = String.format(
            "상황: %s\n%s\n" +
            "위 내용을 바탕으로 법률 진단 결과를 작성해줘. 한국 법령을 근거로 해야 해.\n" +
            "relatedLaws 작성 시: title은 정확한 법령명(예: 근로기준법)만 적어줘.\n" +
            "반드시 아래 JSON 형식으로만 출력해:\n" +
            "{\n" +
            "  \"resultSummary\": \"진단 요약\",\n" +
            "  \"procedures\": [\"절차1\", \"절차2\"],\n" +
            "  \"checklist\": [\"서류1\", \"서류2\"],\n" +
            "  \"relatedLaws\": [\n" +
            "    { \"lawId\": \"\", \"title\": \"법령명\", \"relevantArticles\": [\"제00조: 내용...\"] }\n" +
            "  ]\n" +
            "}", situationTitle, qnaBuilder.toString());

        Map<String, Object> aiResponse = callGemini(prompt);
        
        List<Map<String, Object>> rawLaws = (List<Map<String, Object>>) aiResponse.get("relatedLaws");
        List<AnalysisResult.RelatedLawInfo> relatedLaws = new ArrayList<>();
        
        if (rawLaws != null) {
            for (Map<String, Object> lawMap : rawLaws) {
                String rawLawId = (String) lawMap.getOrDefault("lawId", "");
                String title = (String) lawMap.get("title");
                
                String finalLawId = null;

                // ⭐ 로직 수정: DB에 있는 경우만 ID 사용, 없으면 null (일반 검색 링크 사용)
                if (rawLawId != null && !rawLawId.trim().isEmpty()) {
                    boolean existsInDb = !legalDocumentRepository.findByLawId(rawLawId).isEmpty();
                    if (existsInDb) {
                        finalLawId = rawLawId; // 내부 DB 연결
                    } 
                    // DB에 없으면 finalLawId는 null 유지 -> 프론트에서 일반 검색 링크 생성
                }

                relatedLaws.add(new AnalysisResult.RelatedLawInfo(
                    finalLawId,
                    title,
                    (List<String>) lawMap.get("relevantArticles")
                ));
            }
        }

        return AnalysisResult.builder()
                .resultSummary((String) aiResponse.get("resultSummary"))
                .procedures((List<String>) aiResponse.get("procedures"))
                .checklist((List<String>) aiResponse.get("checklist"))
                .relatedLaws(relatedLaws)
                .build();
    }

    private Map<String, Object> callGemini(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String url = GEMINI_URL + "?key=" + apiKey;

            Map<String, Object> contentPart = Map.of("text", prompt);
            Map<String, Object> part = Map.of("parts", List.of(contentPart));
            Map<String, Object> requestBody = Map.of("contents", List.of(part));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            String text = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
            
            if (text.contains("```")) {
                text = text.replaceAll("```json", "").replaceAll("```", "").trim();
            }
            return objectMapper.readValue(text, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("AI 호출 오류: " + e.getMessage());
        }
    }
}