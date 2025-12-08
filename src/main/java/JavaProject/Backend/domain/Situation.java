package JavaProject.Backend.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "Situation")
@Getter @Setter
public class Situation {

    @Id
    private String id;

    private String title;
    private String description;
    private String summary;

    private boolean active;
    private String updatedAt;
    private Integer displayOrder;
    private String category;

    // 현재 사용하는 관련법령 배열
    private List<RelatedLaw> relatedLaws;

    // 하위 문서에서 사용되는 경우 있음
    private String lawId;
}