package JavaProject.Backend.controller;

import JavaProject.Backend.domain.AnalysisResult;
import JavaProject.Backend.service.AnalysisService;
import JavaProject.Backend.service.PdfService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/AnalysisResult")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;
    private final PdfService pdfService;

    /**
     * 🔥 분석 결과 생성 (userId는 JWT에서 자동 추출)
     * POST /api/AnalysisResult
     */
    @PostMapping
    public ResponseEntity<AnalysisResult> analyze(@RequestBody Map<String, String> req) {

        String sessionId = req.get("sessionId");
        String situationId = req.get("situationId");

        // JWT에서 userId 추출 (로그인 사용자)
        String userId = extractUserIdFromJwt();

        AnalysisResult result =
                analysisService.analyzeResponses(sessionId, userId, situationId);

        return ResponseEntity.ok(result);
    }

    /**
     * 사용자별 진단 결과 조회
     * 로그인된 사용자만 호출해야 함 (SecurityConfig에서 보호됨)
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AnalysisResult>> getByUser(@PathVariable String userId) {
        return ResponseEntity.ok(analysisService.getResultsByUser(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        analysisService.deleteResult(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> deleteAll(@PathVariable String userId) {
        analysisService.deleteAllResultsByUser(userId);
        return ResponseEntity.ok().build();
    }

    /** PDF 다운로드 */
    @GetMapping("/{resultId}/pdf")
    public ResponseEntity<?> downloadPdf(
            @PathVariable String resultId,
            @RequestParam(required = false) String title
    ) {
        AnalysisResult result = analysisService.getResultById(resultId).orElse(null);
        if (result == null) return ResponseEntity.notFound().build();

        try {
            byte[] pdfBytes = pdfService.generateAnalysisResultPdf(result, title);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"result.pdf\"")
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ----------------------------
    // 🔥 JWT에서 userId 추출
    // ----------------------------
    private String extractUserIdFromJwt() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof String principalUserId) {
            return principalUserId;   // 정상적인 로그인 사용자
        }

        return null; // 비로그인 사용자
    }
}
