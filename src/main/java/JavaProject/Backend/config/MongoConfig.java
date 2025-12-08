package JavaProject.Backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "JavaProject.Backend.repository")
// @EnableMongoAuditing 제거 (JpApplication에 이미 있음)
public class MongoConfig {
    
    // MongoDB 연결 설정은 application.yml에서 관리
    // 추가 커스텀 설정이 필요한 경우 이곳에서 Bean 등록
}
