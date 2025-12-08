package JavaProject.Backend.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * 사용자 엔티티
 * 
 * MongoDB Collection: users
 * 
 * 주요 기능:
 * - 회원가입/로그인 인증
 * - 분석 결과 이력 관리
 * - PDF 재다운로드 권한 관리
 * 
 * @author JP Team
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "User")
public class User {
    
    @Id
    private String id; // MongoDB 문서 고유 ID
    
    
    @Indexed(unique = true) // 유니크 제약 조건, 인덱스 적용
    private String userId; // 사용자 아이디 (로그인 ID)
    
    
    private String passwordHash; // 암호화된 비밀번호, BCrypt로 암호화하여 저장
    
    
    private String nickname; // 사용자 닉네임
    
    
    private List<String> roles; // 사용자 권한 목록 (예: ["USER"], ["USER", "ADMIN"])
    
    
    @CreatedDate
    @Indexed
    private Date createdAt; // 계정 생성 일시, @CreatedDate로 자동 설정
    
    
    @Builder.Default
    private Boolean active = true; // 계정 활성화 여부, 기본값: true
    
    
}
