package JavaProject.Backend.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 법령 문서 엔티티
 *
 * MongoDB Collection: LegalDocument  (대문자!)
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "LegalDocument")  // ← 여기 꼭 대문자 그대로!
public class LegalDocument {

    @Id
    private String id;          // MongoDB 문서 고유 ID (_id)

    private String lawKey;      // 법령 키 (예: "009338_1")

    @Indexed
    private String lawId;       // 법령 코드 (예: "009338")

    @TextIndexed(weight = 10)
    private String title;       // 법령 제목

    private String articleNo;   // 조문 번호 (예: "제1조")
    private String articleTitle; // 조문 제목

    @TextIndexed(weight = 5)
    private String articleText; // 조문 내용

    private String sourceUrl;   // 출처 URL
    private String lastUpdated; // 업데이트 날짜 (문자열)

    // DB에 실제 필드명이 categori 로 되어 있으면 그대로 둔다.
    private String categori;    

    private String situationId; // 상황과 법령 연결용 (현재 null 허용)
}
