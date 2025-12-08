package JavaProject.Backend.controller;

import JavaProject.Backend.domain.User;
import JavaProject.Backend.security.JwtTokenProvider;
import JavaProject.Backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원가입
     * POST /auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        log.info("회원가입 요청 수신: userId={}", request.get("userId"));

        try {
            String userId = request.get("userId");
            String password = request.get("password");
            String nickname = request.get("nickname");

            User user = authService.register(userId, password, nickname);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "회원가입이 완료되었습니다.");
            response.put("userId", user.getUserId());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            log.warn("회원가입 실패: {}", e.getMessage());

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", e.getMessage()
                    )
            );
        }
    }

    /**
     * 로그인
     * POST /auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {

        log.info("로그인 요청 수신: userId={}", request.get("userId"));

        String userId = request.get("userId");
        String password = request.get("password");

        Optional<User> userOpt = authService.authenticate(userId, password);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            log.info("로그인 성공: userId={}", userId);

            String token = jwtTokenProvider.createToken(user.getUserId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "로그인 성공");
            response.put("userId", user.getUserId());
            response.put("nickname", user.getNickname());
            response.put("token", token);

            return ResponseEntity.ok(response);
        }

        log.warn("로그인 실패: 아이디 또는 비밀번호 오류. userId={}", userId);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of(
                        "success", false,
                        "message", "아이디 또는 비밀번호가 올바르지 않습니다."
                )
        );
    }

    /**
     * 회원 탈퇴
     */
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> withdraw(@PathVariable String userId) {
        try {
            authService.withdraw(userId);
            return ResponseEntity.ok(
                    Map.of("success", true, "message", "회원 탈퇴 완료")
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", e.getMessage())
            );
        }
    }
}