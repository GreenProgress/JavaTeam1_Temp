package JavaProject.Backend.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "AnalysisResult")
@CompoundIndexes({
    @CompoundIndex(name = "user_created_idx", def = "{'userId': 1, 'createdAt': -1}")
})
public class AnalysisResult {
    @Id
    private String id; 
    private String userId; 
    @Indexed
    private String sessionId; 
    @Indexed
    private String situationId; 
    
    // [신규] 상황 제목
    private String situationTitle;

    private String resultSummary; 
    private List<String> procedures; 
    private List<String> checklist; 
    private List<RelatedLawInfo> relatedLaws; 

    @Builder.Default
    private String pdfGenerationStatus = "READY"; 
    private String pdfFilePath; 

    @CreatedDate 
    private Date createdAt; 

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class RelatedLawInfo { 
        private String lawId; 
        private String title; 
        private List<String> relevantArticles; 
    }
}