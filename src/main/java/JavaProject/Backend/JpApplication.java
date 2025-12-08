package JavaProject.Backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class JpApplication {
    public static void main(String[] args) {
        SpringApplication.run(JpApplication.class, args);
    }
}
