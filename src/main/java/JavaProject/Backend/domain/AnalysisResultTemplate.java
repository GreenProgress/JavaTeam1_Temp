package JavaProject.Backend.domain;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class AnalysisResultTemplate {
    private String resultSummary;
    private List<String> procedures;
    private List<String> checklist;
    private List<RelatedLawInfoTemplate> relatedLaws;
    private String copyFrom; 

    @Getter
    @Setter
    public static class RelatedLawInfoTemplate {
        private String lawId;
        private String title;
        private List<String> relevantArticles;
    }
}