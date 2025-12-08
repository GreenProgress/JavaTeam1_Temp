package JavaProject.Backend.repository;

import JavaProject.Backend.domain.UserResponse;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.bson.types.ObjectId;

import java.util.List;

@Repository
public interface UserResponseRepository extends MongoRepository<UserResponse, String> {

    // 세션 ID로 응답 조회 (비로그인 사용자)
    List<UserResponse> findBySessionId(String sessionId);

    // 세션 ID + 상황 ID
    List<UserResponse> findBySessionIdAndSituationId(String sessionId, String situationId);

    // 세션 ID + 상황 ID (최신순)
    List<UserResponse> findBySessionIdAndSituationIdOrderByCreatedAtDesc(String sessionId, ObjectId situationId);

    // 특정 질문 응답 존재 여부
    boolean existsBySessionIdAndQuestionId(String sessionId, String questionId);
    
    // 회원 탈퇴 시 삭제용
    void deleteByUserId(String userId);
}