package JavaProject.Backend.controller;

import JavaProject.Backend.domain.LegalDocument;
import JavaProject.Backend.service.LegalDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/legal")
@CrossOrigin(origins = "http://localhost:5173")
public class LegalDocumentController {

    private final LegalDocumentService legalDocumentService;

    /**
     * 1) 법령 검색 (title, articleTitle, articleText)
     * GET /api/legal/search?keyword=xxxx
     */
    @GetMapping("/search")
    public ResponseEntity<List<LegalDocument>> searchLegalDocuments(@RequestParam String keyword) {
        List<LegalDocument> results = legalDocumentService.searchByKeyword(keyword);
        return ResponseEntity.ok(results);
    }

    /**
     * 2) 법령 상세 조회 (lawId 또는 조문 ID 가능)
     * GET /api/legal/{lawId}
     *
     * 예:
     *   /api/legal/000129      → 최저임금법 전체 조문 병합
     *   /api/legal/000129_5    → "000129"만 사용해서 전체 조문 병합
     */
    @GetMapping("/{lawId}")
    public ResponseEntity<?> getLawDetail(@PathVariable String lawId) {

        try {
            // 조문 ID라면 앞부분만 사용 (예: "000129_5" → "000129")
            String pureLawId = lawId.contains("_") ? lawId.split("_")[0] : lawId;

            Map<String, Object> mergedLaw = legalDocumentService.getMergedLawByLawId(pureLawId);

            if (mergedLaw == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("NOT_FOUND");
            }

            return ResponseEntity.ok(mergedLaw);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("ERROR: " + e.getMessage());
        }
    }
}
