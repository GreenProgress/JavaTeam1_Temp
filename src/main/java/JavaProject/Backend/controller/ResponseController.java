package JavaProject.Backend.controller;

import JavaProject.Backend.domain.UserResponse;
import JavaProject.Backend.repository.UserResponseRepository;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/responses")
@RequiredArgsConstructor
public class ResponseController {

    private final UserResponseRepository userResponseRepository;

    /**
     * 단일 응답 저장
     * POST /api/responses
     */
    @PostMapping
    public ResponseEntity<UserResponse> saveResponse(@RequestBody Map<String, String> request) {

        String sessionId = request.get("sessionId");
        String situationId = request.get("situationId");
        String questionId = request.get("questionId");
        String responseValue = request.get("responseValue");
        int orderIndex = Integer.parseInt(request.getOrDefault("orderIndex", "0"));

        // 🔥 JWT에서 userId 추출
        String userId = extractUserIdFromJwt();

        UserResponse response = UserResponse.builder()
                .sessionId(sessionId)
                .userId(userId)  // 로그인 시 자동 저장 (비로그인 = null)
                .situationId(new ObjectId(situationId))
                .questionId(questionId)
                .responseValue(responseValue)
                .orderIndex(orderIndex)
                .createdAt(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(userResponseRepository.save(response));
    }

    // -------------------------------
    // 🔥 JWT에서 userId 추출하는 메서드
    // -------------------------------
    private String extractUserIdFromJwt() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof String principalUserId) {
            return principalUserId;   // userId 그대로
        }
        return null;  // 비로그인 사용자
    }
}