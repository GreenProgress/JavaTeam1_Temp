package JavaProject.Backend.domain;

import lombok.Data;

@Data
public class RelatedLaw {

    private String lawId;
    private String title;

    public RelatedLaw() {}

    public RelatedLaw(String lawId, String title) {
        this.lawId = lawId;
        this.title = title;
    }
}
