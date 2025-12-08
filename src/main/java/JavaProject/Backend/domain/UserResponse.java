package JavaProject.Backend.domain;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "UserResponse")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    @Id
    private String id;
    private String sessionId;
    
    private ObjectId situationId; 
    
    private String questionId;
    private String responseValue; // "YES", "NO" 등
    
    private int orderIndex;
    
    private String userId; // (nullable)

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}