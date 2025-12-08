package JavaProject.Backend.controller;

import JavaProject.Backend.domain.Situation;
import JavaProject.Backend.service.SituationService;
// 👇 새로 추가된 import
import JavaProject.Backend.domain.Question;
import JavaProject.Backend.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/situations")
public class SituationController {

    private final SituationService situationService;
    private final QuestionService questionService; // ✨ QuestionService 주입

    // 1) 전체 목록 조회 + 키워드 검색
    @GetMapping
    public ResponseEntity<?> searchSituations(@RequestParam(required = false) String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                List<Situation> all = situationService.getAllSituations();
                return ResponseEntity.ok(all);
            }

            List<Situation> result = situationService.searchSituations(keyword);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("상황 검색 중 오류 발생");
        }
    }

    // 2) ID 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> getSituation(@PathVariable String id) {

        try {
            Situation situation = situationService.getSituationById(id);
            return ResponseEntity.ok(situation);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("상황 정보를 찾을 수 없습니다.");
        }
    }

    // ✨ 3) 상황에 연결된 질문 목록 조회 엔드포인트 추가 (NoResourceFoundException 해결)
    // 요청 경로: GET /api/situations/{id}/Question
    @GetMapping("/{id}/Question")
    public ResponseEntity<?> getQuestionsBySituation(@PathVariable String id) {
        try {
            // Situation ID를 사용하여 해당 질문 목록을 가져오는 서비스 로직 호출
            List<Question> questions = questionService.getQuestionsBySituationId(id);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("질문 목록 로딩 중 오류 발생");
        }
    }
}