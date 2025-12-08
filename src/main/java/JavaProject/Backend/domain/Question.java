package JavaProject.Backend.domain;

import lombok.*;
import org.bson.types.ObjectId; // [수정] import 추가
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {
    @Id
    private String id;
    
    // [수정] 타입을 String에서 ObjectId로 변경
    private ObjectId situationId; 
    
    private String situationTitle;
    private String questionText;
    private String questionType;
    private int orderIndex;
    private boolean active;
    private String helpText;
}