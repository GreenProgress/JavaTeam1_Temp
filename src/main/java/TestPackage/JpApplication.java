package TestPackage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing  // MongoDB 검사 기능 (createdAt, updatedAt 자동 관리)
public class JpApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(JpApplication.class, args);
	}
	
}
