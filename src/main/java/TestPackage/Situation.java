package TestPackage;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date; 

@Document(collection = "Situation")
public class Situation {

    @Id
    private String id;
    
    private String title;       
    private String category;    
    private boolean active;     
    private Date updatedAt;     

    // 생성자
    public Situation() {
        this.active = true; 
        this.updatedAt = new Date(); 
    }

    public Situation(String title, String category) {
        this.title = title;
        this.category = category;
        this.active = true;
        this.updatedAt = new Date();
    }
    
    @Override
    public String toString() {
        return "Situation{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", active=" + active +
                ", updatedAt=" + updatedAt +
                '}';
    }

    // --- Getter, Setter ---
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    
}